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

    protected User() {}

    protected User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}