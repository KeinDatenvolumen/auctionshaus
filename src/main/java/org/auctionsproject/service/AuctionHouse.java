package org.auctionsproject.service;

import org.auctionsproject.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AuctionHouse {
    private final String name;
    private final List<User> users = new CopyOnWriteArrayList<>();
    private final List<Auction> auctions = new CopyOnWriteArrayList<>();
    private final List<Auction> finishedAuctions = new CopyOnWriteArrayList<>();
    private final double commissionRate = 0.01;
    private double totalCommission = 0.0;

    // NEU: UI-Log Callback
    private Consumer<String> eventListener;

    public AuctionHouse(String name) {
        this.name = name;
    }

    public void setEventListener(Consumer<String> eventListener) {
        this.eventListener = eventListener;
    }

    private void emit(String msg) {
        if (eventListener != null) {
            eventListener.accept(msg);
        }
    }

    public String getName() { return name; }
    public List<User> getUsers() { return users; }
    public List<Auction> getAuctions() { return auctions; }
    public List<Auction> getFinishedAuctions() { return finishedAuctions; }
    public double getTotalCommission() { return totalCommission; }

    public void registerUser(User user) { users.add(user); }

    public Auction createAuction(Item item, Auctioneer auctioneer, List<Bidder> bidders, double step) {
        int id = auctions.size() + 1;
        Auction auction = new Auction(id, item, auctioneer, bidders, step);
        auctions.add(auction);
        emit("Auktion #" + id + " erstellt | Item=" + item.getName() + " | Start=" + item.getStartPrice() + " | Min=" + item.getMinPrice());
        return auction;
    }

    public void startSimulation(int parallelAuctions, long tickMillis) {
        BlockingQueue<Auction> queue = new LinkedBlockingQueue<>();
        AtomicBoolean producerDone = new AtomicBoolean(false);

        Thread producer = new Thread(() -> {
            auctions.stream()
                    .filter(a -> a.getStatus() == AuctionStatus.WAITING)
                    .forEach(a -> {
                        try {
                            queue.put(a);
                            emit("Queue: Auktion #" + a.getId() + " eingeplant");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
            producerDone.set(true);
        });

        producer.start();
        emit("Simulation gestartet (Producer/Consumer)");

        ExecutorService pool = Executors.newFixedThreadPool(parallelAuctions);

        for (int i = 0; i < parallelAuctions; i++) {
            pool.submit(() -> {
                while (true) {
                    try {
                        Auction auction = queue.poll(300, TimeUnit.MILLISECONDS);
                        if (auction == null) {
                            if (producerDone.get() && queue.isEmpty()) break;
                            continue;
                        }
                        runAuction(auction, tickMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        try {
            producer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pool.shutdownNow();
        }
        emit("Simulation beendet.");
    }

    private void runAuction(Auction auction, long tickMillis) {
        if (auction.getStatus() != AuctionStatus.WAITING) return;

        auction.start();
        emit("Auktion #" + auction.getId() + " START | Item=" + auction.getItem().getName() + " | Preis=" + fmt(auction.getCurrentPrice()));

        while (!auction.isFinished()) {
            List<Bidder> shuffled = new ArrayList<>(auction.getBidders());
            Collections.shuffle(shuffled);

            boolean soldNow = false;
            for (Bidder bidder : shuffled) {
                if (auction.trySellToBidder(bidder)) {
                    totalCommission += calculateCommission(auction.getSoldPrice());
                    emit("Auktion #" + auction.getId() + " SOLD an " + bidder.getName()
                            + " für " + fmt(auction.getSoldPrice())
                            + " | Provision=" + fmt(calculateCommission(auction.getSoldPrice())));
                    soldNow = true;
                    break;
                }
            }

            if (!soldNow) {
                double oldPrice = auction.getCurrentPrice();
                auction.decreasePrice();
                double newPrice = auction.getCurrentPrice();

                if (auction.getStatus() == AuctionStatus.WITHDRAWN) {
                    emit("Auktion #" + auction.getId() + " WITHDRAWN | Preis unter Mindestpreis gefallen (" + fmt(newPrice) + ")");
                } else {
                    emit("Auktion #" + auction.getId() + " Preis reduziert: " + fmt(oldPrice) + " -> " + fmt(newPrice));
                }

                try {
                    Thread.sleep(tickMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    emit("Auktion #" + auction.getId() + " unterbrochen.");
                    break;
                }
            }
        }

        finishedAuctions.add(auction);
    }

    public double calculateCommission(double soldPrice) {
        return soldPrice * commissionRate;
    }

    public boolean removeBidderById(int bidderId) {
        boolean removedFromUsers = users.removeIf(u -> (u instanceof Bidder) && u.getId() == bidderId);
        for (Auction a : auctions) {
            if (a.getStatus() == AuctionStatus.WAITING) {
                a.getBidders().removeIf(b -> b.getId() == bidderId);
            }
        }
        if (removedFromUsers) emit("Bieter mit ID " + bidderId + " entfernt.");
        return removedFromUsers;
    }

    public SimulationReport getReport() {
        SimulationReport report = new SimulationReport();
        report.setTotalAuctions(finishedAuctions.size());

        long sold = finishedAuctions.stream().filter(a -> a.getStatus() == AuctionStatus.SOLD).count();
        long withdrawn = finishedAuctions.stream().filter(a -> a.getStatus() == AuctionStatus.WITHDRAWN).count();

        report.setSoldItems((int) sold);
        report.setWithdrawnItems((int) withdrawn);

        double avgBidders = finishedAuctions.isEmpty() ? 0 :
                finishedAuctions.stream().mapToInt(a -> a.getBidders().size()).average().orElse(0);
        report.setAvgBiddersPerAuction(avgBidders);

        Set<Integer> auctioneerIds = finishedAuctions.stream().map(a -> a.getAuctioneer().getId()).collect(Collectors.toSet());
        report.setUniqueAuctioneers(auctioneerIds.size());

        Set<Integer> bidderIds = finishedAuctions.stream()
                .flatMap(a -> a.getBidders().stream())
                .map(User::getId)
                .collect(Collectors.toSet());
        report.setUniqueBidders(bidderIds.size());

        report.setTotalCommission(totalCommission);
        return report;
    }

    private String fmt(double value) {
        return String.format("%.2f", value);
    }
}