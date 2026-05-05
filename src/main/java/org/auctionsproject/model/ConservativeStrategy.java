package org.auctionsproject.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Konservativ: wartet länger.
 */
public class ConservativeStrategy implements BidStrategy {
    @Override
    public boolean acceptPrice(Item item, double currentPrice, double budget) {
        if (currentPrice > budget) return false;
        double ratio = currentPrice / item.getStartPrice();
        double randomValue = ThreadLocalRandom.current().nextDouble(0.4, 0.75);
        return ratio <= randomValue;
    }

    @Override
    public String getName() {
        return "Conservative";
    }
}