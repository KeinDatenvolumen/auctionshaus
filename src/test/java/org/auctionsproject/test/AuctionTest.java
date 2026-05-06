package org.auctionsproject.test;

import org.auctionsproject.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für die {@link org.auctionsproject.model.Auction}-Logik.
 */
class AuctionTest {

    /**
     * Verifiziert, dass eine Auktion verkauft wird, wenn ein Bieter akzeptiert.
     */
    @Test
    void auctionSoldWhenBidderAccepts() {
        Item item = new Item(1, "Phone", ItemCategory.ELEKTRONIK, 500, 200);
        Auctioneer a = new Auctioneer(1, "Auc");

        // Garantiert true, solange Budget reicht
        BidStrategy alwaysBuy = new BidStrategy() {
            @Override
            public boolean acceptPrice(Item item, double currentPrice, double budget) {
                return currentPrice <= budget;
            }

            @Override
            public String getName() {
                return "AlwaysBuy";
            }
        };

        Bidder b = new Bidder(2, "Bid", 600, alwaysBuy, ItemCategory.ANY);

        Auction auction = new Auction(1, item, a, List.of(b), 20);
        auction.start();

        boolean sold = auction.trySellToBidder(b);
        assertTrue(sold);
        assertEquals(AuctionStatus.SOLD, auction.getStatus());
        assertNotNull(auction.getWinner());
    }

    /**
     * Verifiziert, dass eine Auktion zurückgezogen wird, wenn der Preis unter das Minimum fällt.
     */
    @Test
    void auctionWithdrawnWhenBelowMinPrice() {
        Item item = new Item(1, "Book", ItemCategory.BUCH, 100, 80);
        Auctioneer a = new Auctioneer(1, "A");
        Bidder b = new Bidder(2, "B", 10, new ConservativeStrategy(), ItemCategory.ANY);

        Auction auction = new Auction(1, item, a, List.of(b), 15);
        auction.start();

        while (!auction.isFinished()) {
            auction.decreasePrice();
        }

        assertEquals(AuctionStatus.WITHDRAWN, auction.getStatus());
    }
}
