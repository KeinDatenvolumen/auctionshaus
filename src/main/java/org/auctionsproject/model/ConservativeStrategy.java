package org.auctionsproject.model;

/**
 * Konservativ: wartet länger.
 */
public class ConservativeStrategy implements BidStrategy {
    @Override
    public boolean acceptPrice(Item item, double currentPrice, double budget) {
        if (currentPrice > budget) return false;
        double ratio = currentPrice / item.getStartPrice();
        return ratio <= 0.75;
    }

    @Override
    public String getName() {
        return "Conservative";
    }
}