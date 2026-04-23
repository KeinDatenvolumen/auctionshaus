package org.auctionsproject.test;

import org.auctionsproject.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuctionTest {

    @Test
    void auctionSoldWhenBidderAccepts() {
        Item item = new Item(1, "Phone", "Electronics", 500, 200);
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

        Bidder b = new Bidder(2, "Bid", 600, alwaysBuy);

        Auction auction = new Auction(1, item, a, List.of(b), 20);
        auction.start();

        boolean sold = auction.trySellToBidder(b);
        assertTrue(sold);
        assertEquals(AuctionStatus.SOLD, auction.getStatus());
        assertNotNull(auction.getWinner());
    }

    @Test
    void auctionWithdrawnWhenBelowMinPrice() {
        Item item = new Item(1, "Book", "Media", 100, 80);
        Auctioneer a = new Auctioneer(1, "A");
        Bidder b = new Bidder(2, "B", 10, new ConservativeStrategy());

        Auction auction = new Auction(1, item, a, List.of(b), 15);
        auction.start();

        while (!auction.isFinished()) {
            auction.decreasePrice();
        }

        assertEquals(AuctionStatus.WITHDRAWN, auction.getStatus());
    }
}