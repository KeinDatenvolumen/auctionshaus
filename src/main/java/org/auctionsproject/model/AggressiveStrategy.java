package org.auctionsproject.model;

/**
 * Aggressiv: kauft eher früher.
 */
public class AggressiveStrategy implements BidStrategy {
    @Override
    public boolean acceptPrice(Item item, double currentPrice, double budget) {
        if (currentPrice > budget) return false;
        double ratio = currentPrice / item.getStartPrice();
        return ratio <= 0.95; // kauft früh
    }

    @Override
    public String getName() {
        return "Aggressive";
    }
}