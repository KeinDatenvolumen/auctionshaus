package org.auctionsproject.model;

/**
 * Immutable Artikelmodell.
 * @param name Artikelname.
 * @param kategorie Kategorie.
 * @param geschaetzterWert Optionaler Schätzwert.
 */
public record Artikel(String name, Kategorie kategorie, double geschaetzterWert) {

    @Override
    public String toString() {
        return name + " (" + kategorie + ", Wert: " + String.format("%.2f", geschaetzterWert) + " €)";
    }
}