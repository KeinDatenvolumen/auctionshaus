package org.auctionsproject.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Niederländische Auktion.
 */
public class Auction {
    private int id;
    private Item item;
    private Auctioneer auctioneer;
    private List<Bidder> bidders = new CopyOnWriteArrayList<>();
    private double currentPrice;
    private double decrementStep;
    private AuctionStatus status = AuctionStatus.WAITING;
    private Bidder winner;
    private double soldPrice;

    public Auction() {}

    public Auction(int id, Item item, Auctioneer auctioneer, List<Bidder> bidders, double decrementStep) {
        this.id = id;
        this.item = item;
        this.auctioneer = auctioneer;
        this.bidders = new CopyOnWriteArrayList<>(bidders);
        this.currentPrice = item.getStartPrice();
        this.decrementStep = decrementStep;
    }

    public synchronized void start() {
        status = AuctionStatus.RUNNING;
    }

    public synchronized boolean trySellToBidder(Bidder bidder) {
        if (status != AuctionStatus.RUNNING) return false;
        if (!bidder.canAfford(currentPrice)) return false;
        if (!bidder.decide(item, currentPrice)) return false;

        bidder.reduceBudget(currentPrice);
        winner = bidder;
        soldPrice = currentPrice;
        status = AuctionStatus.SOLD;
        auctioneer.notifySale(bidder, currentPrice);
        return true;
    }

    public synchronized void decreasePrice() {
        if (status != AuctionStatus.RUNNING) return;
        currentPrice -= decrementStep;
        if (currentPrice < item.getMinPrice()) {
            status = AuctionStatus.WITHDRAWN;
        }
    }

    @JsonIgnore
    public boolean isFinished() {
        return status == AuctionStatus.SOLD || status == AuctionStatus.WITHDRAWN;
    }

    public int getId() { return id; }
    public Item getItem() { return item; }
    public Auctioneer getAuctioneer() { return auctioneer; }
    public List<Bidder> getBidders() { return bidders; }
    public double getCurrentPrice() { return currentPrice; }
    public double getDecrementStep() { return decrementStep; }
    public AuctionStatus getStatus() { return status; }
    public Bidder getWinner() { return winner; }
    public double getSoldPrice() { return soldPrice; }

    public void setId(int id) { this.id = id; }
    public void setItem(Item item) { this.item = item; }
    public void setAuctioneer(Auctioneer auctioneer) { this.auctioneer = auctioneer; }
    public void setBidders(List<Bidder> bidders) { this.bidders = new CopyOnWriteArrayList<>(bidders); }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public void setDecrementStep(double decrementStep) { this.decrementStep = decrementStep; }
    public void setStatus(AuctionStatus status) { this.status = status; }
    public void setWinner(Bidder winner) { this.winner = winner; }
    public void setSoldPrice(double soldPrice) { this.soldPrice = soldPrice; }
}