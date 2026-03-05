package org.auctionsproject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Repräsentiert eine einzelne Auktion.
 * Dient als geteilte Ressource (Shared Resource) für das Multithreading.
 */
public class Auction {
    private final String itemName;
    private final User auctioneer;
    private double currentPrice;
    private final double minPrice;
    private final double priceDropStep;

    private User winner = null;
    private final AtomicBoolean isActive = new AtomicBoolean(true);

    public Auction(String itemName, User auctioneer, double startPrice, double minPrice, double priceDropStep) {
        this.itemName = itemName;
        this.auctioneer = auctioneer;
        this.currentPrice = startPrice;
        this.minPrice = minPrice;
        this.priceDropStep = priceDropStep;
    }

    /**
     * Thread-sichere Methode für den Auktionator, um den Preis zu senken.
     */
    public synchronized void dropPrice() {
        if (!isActive.get()) return;
        currentPrice -= priceDropStep;
        if (currentPrice < minPrice) {
            isActive.set(false); // Auktion gescheitert
        }
    }

    /**
     * Thread-sichere Methode für Bieter (Consumer), um ein Gebot abzugeben.
     * @return true, wenn der Kauf erfolgreich war.
     */
    public synchronized boolean tryBuy(User bidder) {
        if (isActive.get()) {
            this.winner = bidder;
            this.isActive.set(false); // Auktion sofort beenden
            return true;
        }
        return false;
    }

    public boolean isActive() { return isActive.get(); }
    public double getCurrentPrice() { return currentPrice; }
    public String getItemName() { return itemName; }
    public User getAuctioneer() { return auctioneer; }
    public User getWinner() { return winner; }
}