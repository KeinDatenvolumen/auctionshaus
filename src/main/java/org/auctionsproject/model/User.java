package org.auctionsproject.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Basisklasse für alle Nutzer.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Auctioneer.class, name = "auctioneer"),
        @JsonSubTypes.Type(value = Bidder.class, name = "bidder")
})
public abstract class User {
    private int id;
    private String name;

    /**
     * Standardkonstruktor für Serialisierung.
     */
    protected User() {}

    /**
     * Erstellt einen Nutzer mit ID und Namen.
     *
     * @param id eindeutige Nutzer-ID.
     * @param name Anzeigename.
     */
    protected User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Liefert die Nutzer-ID.
     *
     * @return Nutzer-ID.
     */
    public int getId() { return id; }

    /**
     * Liefert den Namen des Nutzers.
     *
     * @return Nutzername.
     */
    public String getName() { return name; }

    /**
     * Setzt die Nutzer-ID.
     *
     * @param id neue Nutzer-ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Setzt den Namen des Nutzers.
     *
     * @param name neuer Nutzername.
     */
    public void setName(String name) { this.name = name; }
}
