package org.auctionsproject.service;

/**
 * Ergebnisdaten der Simulation.
 */
public class SimulationReport {
    private int totalAuctions;
    private int soldItems;
    private int withdrawnItems;
    private double avgBiddersPerAuction;
    private double totalCommission;
    private int uniqueAuctioneers;
    private int uniqueBidders;

    /**
     * Liefert die Gesamtzahl der abgeschlossenen Auktionen.
     *
     * @return Anzahl der Auktionen.
     */
    public int getTotalAuctions() { return totalAuctions; }

    /**
     * Liefert die Anzahl verkaufter Artikel.
     *
     * @return Anzahl der Verkäufe.
     */
    public int getSoldItems() { return soldItems; }

    /**
     * Liefert die Anzahl zurückgezogener Auktionen.
     *
     * @return Anzahl der zurückgezogenen Auktionen.
     */
    public int getWithdrawnItems() { return withdrawnItems; }

    /**
     * Liefert die durchschnittliche Bieteranzahl pro Auktion.
     *
     * @return Durchschnittswert.
     */
    public double getAvgBiddersPerAuction() { return avgBiddersPerAuction; }

    /**
     * Liefert die aufgelaufene Gesamtprovision.
     *
     * @return Provision.
     */
    public double getTotalCommission() { return totalCommission; }

    /**
     * Liefert die Anzahl eindeutiger Auktionatoren.
     *
     * @return Anzahl eindeutiger Auktionatoren.
     */
    public int getUniqueAuctioneers() { return uniqueAuctioneers; }

    /**
     * Liefert die Anzahl eindeutiger Bieter.
     *
     * @return Anzahl eindeutiger Bieter.
     */
    public int getUniqueBidders() { return uniqueBidders; }

    /**
     * Setzt die Gesamtzahl der Auktionen.
     *
     * @param totalAuctions Gesamtanzahl.
     */
    public void setTotalAuctions(int totalAuctions) { this.totalAuctions = totalAuctions; }

    /**
     * Setzt die Anzahl verkaufter Artikel.
     *
     * @param soldItems Anzahl verkaufter Artikel.
     */
    public void setSoldItems(int soldItems) { this.soldItems = soldItems; }

    /**
     * Setzt die Anzahl zurückgezogener Auktionen.
     *
     * @param withdrawnItems Anzahl zurückgezogener Auktionen.
     */
    public void setWithdrawnItems(int withdrawnItems) { this.withdrawnItems = withdrawnItems; }

    /**
     * Setzt den Durchschnitt der Bieter pro Auktion.
     *
     * @param avgBiddersPerAuction Durchschnittswert.
     */
    public void setAvgBiddersPerAuction(double avgBiddersPerAuction) { this.avgBiddersPerAuction = avgBiddersPerAuction; }

    /**
     * Setzt die Gesamtprovision.
     *
     * @param totalCommission Gesamtprovision.
     */
    public void setTotalCommission(double totalCommission) { this.totalCommission = totalCommission; }

    /**
     * Setzt die Anzahl eindeutiger Auktionatoren.
     *
     * @param uniqueAuctioneers Anzahl eindeutiger Auktionatoren.
     */
    public void setUniqueAuctioneers(int uniqueAuctioneers) { this.uniqueAuctioneers = uniqueAuctioneers; }

    /**
     * Setzt die Anzahl eindeutiger Bieter.
     *
     * @param uniqueBidders Anzahl eindeutiger Bieter.
     */
    public void setUniqueBidders(int uniqueBidders) { this.uniqueBidders = uniqueBidders; }

    /**
     * Liefert eine formatierte Textdarstellung des Reports.
     *
     * @return formatierter Bericht.
     */
    public String toPrettyString() {
        return """
                ===== SIMULATIONSBERICHT =====
                Gesamtauktionen: %d
                Verkaufte Artikel: %d
                Zurückgezogene Artikel: %d
                Durchschnitt Bieter/Auktion: %.2f
                Einzigartige Auktionatoren: %d
                Einzigartige Bieter: %d
                Gesamtprovision (1%%): %.2f
                ==============================
                """.formatted(totalAuctions, soldItems, withdrawnItems, avgBiddersPerAuction,
                uniqueAuctioneers, uniqueBidders, totalCommission);
    }
}
