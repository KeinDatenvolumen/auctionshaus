package org.auctionsproject.model;

/**
 * Verhaltensprofil eines Bieters.
 */
public enum BieterTyp {
    AGGRESSIV(1.25),
    KONSERVATIV(0.75),
    ZUFAELLIG(1.0);

    /**
     * Faktor auf die Basis-Gebotswahrscheinlichkeit.
     */
    private final double wahrscheinlichkeitFaktor;

    /**
     * Konstruktor.
     * @param wahrscheinlichkeitFaktor Multiplikator.
     */
    BieterTyp(double wahrscheinlichkeitFaktor) {
        this.wahrscheinlichkeitFaktor = wahrscheinlichkeitFaktor;
    }

    /**
     * Liefert den Wahrscheinlichkeitsfaktor.
     * @return Faktorwert.
     */
    public double getWahrscheinlichkeitFaktor() {
        return wahrscheinlichkeitFaktor;
    }
}