package org.auctionsproject;

/**
 * Thread der Bieter (Consumer). Prüft den Preis und entscheidet anhand der Wahrscheinlichkeit.
 */
public class BidderThread implements Runnable {
    private final User bidder;
    private final Auction auction;
    private final AuctionHouse house;

    public BidderThread(User bidder, Auction auction) {
        this.bidder = bidder;
        this.auction = auction;
        this.house = AuctionHouse.getInstance();
    }

    @Override
    public void run() {
        while (auction.isActive()) {
            try {
                Thread.sleep(800); // Bieter schaut alle 0,8 Sekunden auf den Preis

                // Entscheidungsprozess simulieren
                if (Math.random() < bidder.getBidProbability()) {
                    if (auction.tryBuy(bidder)) {
                        house.log("ZUSCHLAG! " + bidder.getName() + " kauft " + auction.getItemName() + " für " + auction.getCurrentPrice() + "€");
                        house.addCommission(auction.getCurrentPrice() * 0.01); // 1% Provision
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}