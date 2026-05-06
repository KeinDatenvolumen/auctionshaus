package org.auctionsproject.test;

import org.auctionsproject.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für Biet-Strategien und Budget-Checks.
 */
class BidderStrategyTest {

    /**
     * Prüft, dass ein Bieter den Preis nicht bezahlen kann, wenn das Budget zu klein ist.
     */
    @Test
    void bidderCannotAffordPrice() {
        Bidder b = new Bidder(1, "X", 100, new AggressiveStrategy(), ItemCategory.ANY);
        assertFalse(b.canAfford(150));
    }

    /**
     * Verifiziert, dass die aggressive Strategie früher akzeptiert als die konservative.
     */
    @Test
    void aggressiveAcceptsEarlierThanConservative() {
        Item item = new Item(1, "Tablet", ItemCategory.ELEKTRONIK, 1000, 500);

        BidStrategy aggr = new AggressiveStrategy();
        BidStrategy cons = new ConservativeStrategy();

        boolean aggrResult = aggr.acceptPrice(item, 900, 1000);
        boolean consResult = cons.acceptPrice(item, 900, 1000);

        assertTrue(aggrResult);
        assertFalse(consResult);
    }
}
