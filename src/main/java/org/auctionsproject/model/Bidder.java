package org.auctionsproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.auctionsproject.exception.InsufficientBudgetException;

/**
 * Bieter mit Budget und Strategie.
 */
public class Bidder extends User {
    private double budget;
    private String strategyName = "Random";

    public Bidder() {}

    public Bidder(int id, String name, double budget, BidStrategy strategy) {
        super(id, name);
        this.budget = budget;
        setStrategy(strategy);
    }

    public double getBudget() { return budget; }

    public void setBudget(double budget) { this.budget = budget; }

    public boolean canAfford(double price) {
        return budget >= price;
    }

    public void reduceBudget(double amount) {
        if (amount > budget) {
            throw new InsufficientBudgetException("Budget nicht ausreichend: " + getName());
        }
        budget -= amount;
    }

    public boolean decide(Item item, double currentPrice) {
        return getStrategy().acceptPrice(item, currentPrice, budget);
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

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
}