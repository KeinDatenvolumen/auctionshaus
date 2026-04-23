package org.auctionsproject.model;

/**
 * Auktionator.
 */
public class Auctioneer extends User {
    public Auctioneer() {}

    public Auctioneer(int id, String name) {
        super(id, name);
    }

    public void notifySale(Bidder bidder, double price) {
        // absichtlich leer; Logging übernimmt AuctionHouse per UI-Event
    }
}