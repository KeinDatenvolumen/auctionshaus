package org.auctionsproject.model;

/**
 * Status einer Auktion.
 */
public enum AuctionStatus {
    /** Auktion ist angelegt und wartet auf den Start. */
    WAITING,
    /** Auktion läuft und der Preis sinkt. */
    RUNNING,
    /** Auktion wurde verkauft. */
    SOLD,
    /** Auktion wurde unter Mindestpreis zurückgezogen. */
    WITHDRAWN
}
