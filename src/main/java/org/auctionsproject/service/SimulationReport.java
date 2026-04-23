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

    public int getTotalAuctions() { return totalAuctions; }
    public int getSoldItems() { return soldItems; }
    public int getWithdrawnItems() { return withdrawnItems; }
    public double getAvgBiddersPerAuction() { return avgBiddersPerAuction; }
    public double getTotalCommission() { return totalCommission; }
    public int getUniqueAuctioneers() { return uniqueAuctioneers; }
    public int getUniqueBidders() { return uniqueBidders; }

    public void setTotalAuctions(int totalAuctions) { this.totalAuctions = totalAuctions; }
    public void setSoldItems(int soldItems) { this.soldItems = soldItems; }
    public void setWithdrawnItems(int withdrawnItems) { this.withdrawnItems = withdrawnItems; }
    public void setAvgBiddersPerAuction(double avgBiddersPerAuction) { this.avgBiddersPerAuction = avgBiddersPerAuction; }
    public void setTotalCommission(double totalCommission) { this.totalCommission = totalCommission; }
    public void setUniqueAuctioneers(int uniqueAuctioneers) { this.uniqueAuctioneers = uniqueAuctioneers; }
    public void setUniqueBidders(int uniqueBidders) { this.uniqueBidders = uniqueBidders; }

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