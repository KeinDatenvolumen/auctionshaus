package org.auctionsproject.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Zufälliges Verhalten mit Grundwahrscheinlichkeit.
 */
public class RandomStrategy implements BidStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean acceptPrice(Item item, double currentPrice, double budget) {
        if (currentPrice > budget) return false;
        double ratio = currentPrice / item.getStartPrice();
        double chance = Math.max(0.1, 1.0 - ratio); // je günstiger, desto wahrscheinlicher
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Random";
    }
}
