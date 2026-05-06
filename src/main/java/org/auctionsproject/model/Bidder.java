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

    /**
     * Standardkonstruktor für Serialisierung.
     */
    public Bidder() {}

    /**
     * Erstellt einen Bieter mit Budget, Strategie und Interessen.
     *
     * @param id                eindeutige ID.
     * @param name              Anzeigename.
     * @param budget            verfügbares Budget.
     * @param strategy          Bietstrategie.
     * @param preferredCategory bevorzugte Kategorie.
     */
    public Bidder(int id, String name, double budget, BidStrategy strategy, ItemCategory preferredCategory) {
        super(id, name);
        this.budget = budget;
        setStrategy(strategy);
        this.preferredCategory = preferredCategory;
    }

    /**
     * Liefert das aktuelle Budget.
     *
     * @return Budget.
     */
    public double getBudget() { return budget; }

    /**
     * Setzt das Budget.
     *
     * @param budget neues Budget.
     */
    public void setBudget(double budget) { this.budget = budget; }

    /**
     * Liefert die bevorzugte Kategorie.
     *
     * @return bevorzugte Kategorie.
     */
    public ItemCategory getPreferredCategory() { return preferredCategory; }

    /**
     * Setzt die bevorzugte Kategorie.
     *
     * @param preferredCategory neue bevorzugte Kategorie.
     */
    public void setPreferredCategory(ItemCategory preferredCategory) { this.preferredCategory = preferredCategory; }

    /**
     * Prüft, ob der Bieter einen Preis bezahlen kann.
     *
     * @param price zu prüfender Preis.
     * @return {@code true}, wenn das Budget ausreicht.
     */
    public boolean canAfford(double price) { return budget >= price; }

    /**
     * Reduziert das Budget um den angegebenen Betrag.
     *
     * @param amount abzuziehender Betrag.
     * @throws InsufficientBudgetException wenn der Betrag das Budget überschreitet.
     */
    public void reduceBudget(double amount) {
        if (amount > budget) {
            throw new InsufficientBudgetException("Budget nicht ausreichend: " + getName());
        }
        budget -= amount;
    }

    /**
     * Trifft eine Kaufentscheidung basierend auf Strategie, Preis und Kategorie.
     *
     * @param item         angebotenes Item.
     * @param currentPrice aktueller Preis.
     * @return {@code true}, wenn der Bieter kaufen möchte.
     */
    public boolean decide(Item item, double currentPrice) {
        boolean accept = getStrategy().acceptPrice(item, currentPrice, budget);
        if (!accept) return false;

        // 50% Malus, wenn Kategorie nicht passt
        if (preferredCategory != ItemCategory.ANY && item.getCategory() != preferredCategory) {
            return ThreadLocalRandom.current().nextDouble() < 0.5;
        }
        return true;
    }

    /**
     * Liefert die effektive Strategieinstanz basierend auf dem gespeicherten Namen.
     *
     * @return Strategieinstanz.
     */
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

    /**
     * Setzt die Strategie und speichert deren Namen.
     *
     * @param strategy neue Strategie.
     */
    public void setStrategy(BidStrategy strategy) {
        this.strategyName = strategy.getName();
    }

    /**
     * Liefert den Namen der Strategie.
     *
     * @return Strategiename.
     */
    public String getStrategyName() { return strategyName; }

    /**
     * Setzt den Namen der Strategie.
     *
     * @param strategyName Strategiename.
     */
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }
}
