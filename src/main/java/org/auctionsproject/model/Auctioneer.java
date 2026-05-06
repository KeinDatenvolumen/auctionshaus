package org.auctionsproject.model;

/**
 * Auktionator.
 */
public class Auctioneer extends User {
    /**
     * Standardkonstruktor für Serialisierung.
     */
    public Auctioneer() {}

    /**
     * Erstellt einen Auktionator.
     *
     * @param id eindeutige ID.
     * @param name Anzeigename.
     */
    public Auctioneer(int id, String name) {
        super(id, name);
    }

    /**
     * Benachrichtigt den Auktionator über einen Verkauf.
     *
     * @param bidder Käufer.
     * @param price Verkaufspreis.
     */
    public void notifySale(Bidder bidder, double price) {
        // absichtlich leer; Logging übernimmt AuctionHouse per UI-Event
    }
}
