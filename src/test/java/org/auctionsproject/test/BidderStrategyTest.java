package org.auctionsproject.test;

import org.auctionsproject.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BidderStrategyTest {

    @Test
    void bidderCannotAffordPrice() {
        Bidder b = new Bidder(1, "X", 100, new AggressiveStrategy());
        assertFalse(b.canAfford(150));
    }

    @Test
    void aggressiveAcceptsEarlierThanConservative() {
        Item item = new Item(1, "Tablet", "Elec", 1000, 500);

        BidStrategy aggr = new AggressiveStrategy();
        BidStrategy cons = new ConservativeStrategy();

        boolean aggrResult = aggr.acceptPrice(item, 900, 1000);
        boolean consResult = cons.acceptPrice(item, 900, 1000);

        assertTrue(aggrResult);
        assertFalse(consResult);
    }
}