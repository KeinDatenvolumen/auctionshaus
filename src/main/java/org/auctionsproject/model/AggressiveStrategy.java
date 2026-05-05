package org.auctionsproject.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Aggressiv: kauft eher früher.
 */
public class AggressiveStrategy implements BidStrategy {
    @Override
    public boolean acceptPrice(Item item, double currentPrice, double budget) {
        if (currentPrice > budget) return false;
        double ratio = currentPrice / item.getStartPrice();
        double randomValue = ThreadLocalRandom.current().nextDouble(0.7, 0.95);
        return ratio <= randomValue; // kauft früh
    }

    @Override
    public String getName() {
        return "Aggressive";
    }
}