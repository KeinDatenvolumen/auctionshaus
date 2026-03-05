package org.auctionsproject;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton-Klasse zur Verwaltung des Auktionshauses.
 */
public class AuctionHouse {
    private static AuctionHouse instance;
    private TextArea logArea; // Referenz auf das GUI
    private ExecutorService threadPool;

    private double totalCommission = 0.0;
    private AtomicInteger activeAuctionsCount = new AtomicInteger(0);

    private AuctionHouse() {
        // Cached Thread Pool skaliert automatisch für viele Auktionen
        threadPool = Executors.newCachedThreadPool();
    }

    public static synchronized AuctionHouse getInstance() {
        if (instance == null) {
            instance = new AuctionHouse();
        }
        return instance;
    }

    public void setLogArea(TextArea logArea) {
        this.logArea = logArea;
    }

    /**
     * Schreibt thread-sicher in das JavaFX GUI.
     */
    public void log(String message) {
        if (logArea != null) {
            Platform.runLater(() -> logArea.appendText(message + "\n"));
        } else {
            System.out.println(message);
        }
    }

    public synchronized void addCommission(double amount) {
        totalCommission += amount;
    }

    /**
     * Startet eine neue Auktion mit einem Auktionator und einer Liste von Bietern.
     */
    public void startAuction(Auction auction, User[] bidders) {
        activeAuctionsCount.incrementAndGet();

        // Starte Auktionator-Thread
        threadPool.execute(new AuctioneerThread(auction));

        // Starte Bieter-Threads
        for (User bidder : bidders) {
            threadPool.execute(new BidderThread(bidder, auction));
        }
    }

    public void checkSimulationEnd() {
        if (activeAuctionsCount.decrementAndGet() == 0) {
            log("\n=== SIMULATION BEENDET ===");
            log("Gesamte Provision für das Haus: " + String.format("%.2f", totalCommission) + "€");
            // Hier könnte man noch detailliertere Statistiken ausgeben
        }
    }

    public void shutdown() {
        threadPool.shutdownNow();
    }
}