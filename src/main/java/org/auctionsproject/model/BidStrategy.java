package org.auctionsproject.model;

/**
 * Strategie-Interface für Bietentscheidungen.
 */
public interface BidStrategy {
    /**
     * Entscheidet, ob ein Bieter den aktuellen Preis akzeptiert.
     *
     * @param item         angebotenes Item.
     * @param currentPrice aktueller Preis.
     * @param budget       verfügbares Budget.
     * @return {@code true}, wenn der Preis akzeptiert wird.
     */
    boolean acceptPrice(Item item, double currentPrice, double budget);

    /**
     * Liefert den Namen der Strategie.
     *
     * @return Strategiename.
     */
    String getName();
}
