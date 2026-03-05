package org.auctionsproject;

/**
 * Thread des Auktionators (Producer). Senkt den Preis in regelmäßigen Abständen.
 */
public class AuctioneerThread implements Runnable {
    private final Auction auction;
    private final AuctionHouse house;

    public AuctioneerThread(Auction auction) {
        this.auction = auction;
        this.house = AuctionHouse.getInstance();
    }

    @Override
    public void run() {
        house.log(auction.getAuctioneer().getName() + " startet Auktion für " + auction.getItemName() + " bei " + auction.getCurrentPrice() + "€");

        while (auction.isActive()) {
            try {
                Thread.sleep(1000); // Wartet 1 Sekunde zwischen Preisnachlässen
                auction.dropPrice();

                if (auction.isActive()) {
                    house.log("Neuer Preis für " + auction.getItemName() + ": " + auction.getCurrentPrice() + "€");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Abschlussbericht für diese Auktion
        if (auction.getWinner() == null) {
            house.log("Auktion beendet: " + auction.getItemName() + " wurde nicht verkauft (Mindestpreis unterschritten).");
        }
        house.checkSimulationEnd();
    }
}