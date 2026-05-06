package org.auctionsproject.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Niederländische Auktion.
 */
public class Auction {
    private int id;
    private Item item;
    private Auctioneer auctioneer;
    private List<Bidder> bidders = new CopyOnWriteArrayList<>();
    private double currentPrice;
    private double decrementStep;
    private AuctionStatus status = AuctionStatus.WAITING;
    private Bidder winner;
    private double soldPrice;

    /**
     * Standardkonstruktor für Serialisierung.
     */
    public Auction() {}

    /**
     * Erstellt eine Auktion mit Startpreis und Bietern.
     *
     * @param id            eindeutige Auktions-ID.
     * @param item          zu versteigerndes Item.
     * @param auctioneer    verantwortlicher Auktionator.
     * @param bidders       teilnehmende Bieter.
     * @param decrementStep Preisreduktionsschritt.
     */
    public Auction(int id, Item item, Auctioneer auctioneer, List<Bidder> bidders, double decrementStep) {
        this.id = id;
        this.item = item;
        this.auctioneer = auctioneer;
        this.bidders = new CopyOnWriteArrayList<>(bidders);
        this.currentPrice = item.getStartPrice();
        this.decrementStep = decrementStep;
    }

    /**
     * Startet die Auktion und setzt den Status auf {@link AuctionStatus#RUNNING}.
     */
    public synchronized void start() {
        status = AuctionStatus.RUNNING;
    }

    /**
     * Versucht, die Auktion an einen Bieter zu verkaufen.
     *
     * @param bidder Bieter, der kaufen möchte.
     * @return {@code true}, wenn der Verkauf erfolgreich war.
     */
    public synchronized boolean trySellToBidder(Bidder bidder) {
        if (status != AuctionStatus.RUNNING) return false;
        if (!bidder.canAfford(currentPrice)) return false;
        if (!bidder.decide(item, currentPrice)) return false;

        bidder.reduceBudget(currentPrice);
        winner = bidder;
        soldPrice = currentPrice;
        status = AuctionStatus.SOLD;
        auctioneer.notifySale(bidder, currentPrice);
        return true;
    }

    /**
     * Reduziert den aktuellen Preis und markiert die Auktion ggf. als zurückgezogen.
     */
    public synchronized void decreasePrice() {
        if (status != AuctionStatus.RUNNING) return;
        currentPrice -= decrementStep;
        if (currentPrice < item.getMinPrice()) {
            status = AuctionStatus.WITHDRAWN;
        }
    }

    /**
     * Gibt an, ob die Auktion abgeschlossen ist.
     *
     * @return {@code true}, wenn die Auktion verkauft oder zurückgezogen ist.
     */
    @JsonIgnore
    public boolean isFinished() {
        return status == AuctionStatus.SOLD || status == AuctionStatus.WITHDRAWN;
    }

    /**
     * Liefert die Auktions-ID.
     *
     * @return Auktions-ID.
     */
    public int getId() { return id; }

    /**
     * Liefert das Item der Auktion.
     *
     * @return Item.
     */
    public Item getItem() { return item; }

    /**
     * Liefert den Auktionator.
     *
     * @return Auktionator.
     */
    public Auctioneer getAuctioneer() { return auctioneer; }

    /**
     * Liefert die aktuellen Bieter.
     *
     * @return Liste der Bieter.
     */
    public List<Bidder> getBidders() { return bidders; }

    /**
     * Liefert den aktuellen Preis.
     *
     * @return aktueller Preis.
     */
    public double getCurrentPrice() { return currentPrice; }

    /**
     * Liefert den Preisreduktionsschritt.
     *
     * @return Reduktionsschritt.
     */
    public double getDecrementStep() { return decrementStep; }

    /**
     * Liefert den Status der Auktion.
     *
     * @return Status.
     */
    public AuctionStatus getStatus() { return status; }

    /**
     * Liefert den Gewinner der Auktion.
     *
     * @return Gewinner oder {@code null}.
     */
    public Bidder getWinner() { return winner; }

    /**
     * Liefert den Verkaufspreis.
     *
     * @return Verkaufspreis.
     */
    public double getSoldPrice() { return soldPrice; }

    /**
     * Setzt die Auktions-ID.
     *
     * @param id neue Auktions-ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Setzt das Item der Auktion.
     *
     * @param item neues Item.
     */
    public void setItem(Item item) { this.item = item; }

    /**
     * Setzt den Auktionator.
     *
     * @param auctioneer neuer Auktionator.
     */
    public void setAuctioneer(Auctioneer auctioneer) { this.auctioneer = auctioneer; }

    /**
     * Setzt die Bieter der Auktion.
     *
     * @param bidders neue Bieter-Liste.
     */
    public void setBidders(List<Bidder> bidders) { this.bidders = new CopyOnWriteArrayList<>(bidders); }

    /**
     * Setzt den aktuellen Preis.
     *
     * @param currentPrice neuer Preis.
     */
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    /**
     * Setzt den Preisreduktionsschritt.
     *
     * @param decrementStep neuer Reduktionsschritt.
     */
    public void setDecrementStep(double decrementStep) { this.decrementStep = decrementStep; }

    /**
     * Setzt den Status der Auktion.
     *
     * @param status neuer Status.
     */
    public void setStatus(AuctionStatus status) { this.status = status; }

    /**
     * Setzt den Gewinner der Auktion.
     *
     * @param winner neuer Gewinner.
     */
    public void setWinner(Bidder winner) { this.winner = winner; }

    /**
     * Setzt den Verkaufspreis.
     *
     * @param soldPrice neuer Verkaufspreis.
     */
    public void setSoldPrice(double soldPrice) { this.soldPrice = soldPrice; }
}
