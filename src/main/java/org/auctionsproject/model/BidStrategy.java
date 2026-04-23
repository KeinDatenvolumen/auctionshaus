package org.auctionsproject.model;

/**
 * Strategie-Interface für Bietentscheidungen.
 */
public interface BidStrategy {
    boolean acceptPrice(Item item, double currentPrice, double budget);
    String getName();
}