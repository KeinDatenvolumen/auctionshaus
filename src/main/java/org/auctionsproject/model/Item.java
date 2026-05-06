package org.auctionsproject.model;

import org.auctionsproject.exception.InvalidPriceException;

/**
 * Repräsentiert ein zu versteigerndes Objekt.
 */
public class Item {
    private int id;
    private String name;
    private ItemCategory category;
    private double startPrice;
    private double minPrice;

    /**
     * Standardkonstruktor für Serialisierung.
     */
    public Item() {}

    /**
     * Erstellt ein Item mit validierten Start- und Mindestpreisen.
     *
     * @param id         eindeutige Item-ID.
     * @param name       Artikelname.
     * @param category   Artikelkategorie.
     * @param startPrice Startpreis.
     * @param minPrice   Mindestpreis.
     * @throws InvalidPriceException wenn die Preise ungültig sind.
     */
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

    /**
     * Liefert die Item-ID.
     *
     * @return Item-ID.
     */
    public int getId() { return id; }

    /**
     * Liefert den Namen des Items.
     *
     * @return Itemname.
     */
    public String getName() { return name; }

    /**
     * Liefert die Kategorie des Items.
     *
     * @return Itemkategorie.
     */
    public ItemCategory getCategory() { return category; }

    /**
     * Liefert den Startpreis.
     *
     * @return Startpreis.
     */
    public double getStartPrice() { return startPrice; }

    /**
     * Liefert den Mindestpreis.
     *
     * @return Mindestpreis.
     */
    public double getMinPrice() { return minPrice; }

    /**
     * Setzt die Item-ID.
     *
     * @param id neue Item-ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Setzt den Namen des Items.
     *
     * @param name neuer Itemname.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Setzt die Kategorie des Items.
     *
     * @param category neue Kategorie.
     */
    public void setCategory(ItemCategory category) { this.category = category; }

    /**
     * Setzt den Startpreis.
     *
     * @param startPrice neuer Startpreis.
     */
    public void setStartPrice(double startPrice) { this.startPrice = startPrice; }

    /**
     * Setzt den Mindestpreis.
     *
     * @param minPrice neuer Mindestpreis.
     */
    public void setMinPrice(double minPrice) { this.minPrice = minPrice; }
}
