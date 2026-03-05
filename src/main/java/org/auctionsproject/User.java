package org.auctionsproject;

/**
 * Repräsentiert einen Nutzer (Bieter oder Auktionator) der Plattform.
 */
public class User {
    private final String name;
    private final double bidProbability; // Wahrscheinlichkeit, dass der Bieter zuschlägt (0.0 bis 1.0)

    public User(String name, double bidProbability) {
        this.name = name;
        this.bidProbability = bidProbability;
    }

    public String getName() { return name; }
    public double getBidProbability() { return bidProbability; }
}