package org.auctionsproject.model;

/**
 * Kategorien für Items und Bieterinteressen.
 */
public enum ItemCategory {
    /** Elektronikartikel. */
    ELEKTRONIK,
    /** Fahrzeuge und Autoteile. */
    AUTO,
    /** Bücher und Medien. */
    BUCH,
    /** Modeartikel. */
    MODE,
    /** Möbelstücke. */
    MOEBEL,
    /** Sonstige Kategorien. */
    SONSTIGES,

    /** Keine Präferenz, nur für Bieter-Interesse. */
    ANY
}
