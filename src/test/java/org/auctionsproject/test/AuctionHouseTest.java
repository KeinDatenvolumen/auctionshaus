package org.auctionsproject.test;

import org.auctionsproject.model.*;
import org.auctionsproject.service.AuctionHouse;
import org.auctionsproject.service.SimulationReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuctionHouseTest {

    @Test
    void commissionCalculatedCorrectly() {
        AuctionHouse house = new AuctionHouse("TestHouse");
        assertEquals(10.0, house.calculateCommission(1000.0), 0.0001);
    }

    @Test
    void reportContainsValues() {
        AuctionHouse house = new AuctionHouse("H");
        Auctioneer a = new Auctioneer(1, "A");
        Bidder b = new Bidder(2, "B", 1000, new AggressiveStrategy());
        house.registerUser(a);
        house.registerUser(b);

        Item item = new Item(1, "PC", "Tech", 800, 300);
        house.createAuction(item, a, List.of(b), 50);
        house.startSimulation(1, 1);

        SimulationReport r = house.getReport();
        assertEquals(1, r.getTotalAuctions());
        assertTrue(r.getSoldItems() + r.getWithdrawnItems() == 1);
    }
}