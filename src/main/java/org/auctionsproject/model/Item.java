package org.auctionsproject.model;

import org.auctionsproject.exception.InvalidPriceException;

public class Item {
    private int id;
    private String name;
    private ItemCategory category;
    private double startPrice;
    private double minPrice;

    public Item() {}

    public Item(int id, String name, ItemCategory category, double startPrice, double minPrice) {
        if (startPrice <= 0 || minPrice <= 0 || minPrice > startPrice) {
            throw new InvalidPriceException("Ungültige Preise für Item " + name);
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.startPrice = startPrice;
        this.minPrice = minPrice;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public ItemCategory getCategory() { return category; }
    public double getStartPrice() { return startPrice; }
    public double getMinPrice() { return minPrice; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(ItemCategory category) { this.category = category; }
    public void setStartPrice(double startPrice) { this.startPrice = startPrice; }
    public void setMinPrice(double minPrice) { this.minPrice = minPrice; }
}