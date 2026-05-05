package org.auctionsproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.auctionsproject.exception.InsufficientBudgetException;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Bieter mit Budget und Strategie.
 */
public class Bidder extends User {
    private double budget;
    private String strategyName = "Random";
    private ItemCategory preferredCategory = ItemCategory.ANY;

    public Bidder() {}

    public Bidder(int id, String name, double budget, BidStrategy strategy, ItemCategory preferredCategory) {
        super(id, name);
        this.budget = budget;
        setStrategy(strategy);
        this.preferredCategory = preferredCategory;
    }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public ItemCategory getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(ItemCategory preferredCategory) { this.preferredCategory = preferredCategory; }

    public boolean canAfford(double price) { return budget >= price; }

    public void reduceBudget(double amount) {
        if (amount > budget) {
            throw new InsufficientBudgetException("Budget nicht ausreichend: " + getName());
        }
        budget -= amount;
    }

    public boolean decide(Item item, double currentPrice) {
        boolean accept = getStrategy().acceptPrice(item, currentPrice, budget);
        if (!accept) return false;

        // 50% Malus, wenn Kategorie nicht passt
        if (preferredCategory != ItemCategory.ANY && item.getCategory() != preferredCategory) {
            return ThreadLocalRandom.current().nextDouble() < 0.5;
        }
        return true;
    }

    @JsonIgnore
    public BidStrategy getStrategy() {
        return switch (strategyName) {
            case "Aggressive" -> new AggressiveStrategy();
            case "Conservative" -> new ConservativeStrategy();
            case "AlwaysBuy" -> new BidStrategy() {
                @Override
                public boolean acceptPrice(Item item, double currentPrice, double budget) {
                    return currentPrice <= budget;
                }
                @Override
                public String getName() { return "AlwaysBuy"; }
            };
            default -> new RandomStrategy();
        };
    }

    public void setStrategy(BidStrategy strategy) {
        this.strategyName = strategy.getName();
    }

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }
}