package org.auctionsproject.persistence;

import org.auctionsproject.model.Auction;
import org.auctionsproject.model.User;
import org.auctionsproject.service.SimulationReport;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO für komplettes Speichern/Laden.
 */
public class StateSnapshot {
    private List<User> users = new ArrayList<>();
    private List<Auction> auctions = new ArrayList<>();
    private SimulationReport report = new SimulationReport();

    /**
     * Liefert die Nutzerliste.
     *
     * @return Nutzer.
     */
    public List<User> getUsers() { return users; }

    /**
     * Liefert die Auktionenliste.
     *
     * @return Auktionen.
     */
    public List<Auction> getAuctions() { return auctions; }

    /**
     * Liefert den gespeicherten Report.
     *
     * @return Report.
     */
    public SimulationReport getReport() { return report; }

    /**
     * Setzt die Nutzerliste.
     *
     * @param users Nutzer.
     */
    public void setUsers(List<User> users) { this.users = users; }

    /**
     * Setzt die Auktionenliste.
     *
     * @param auctions Auktionen.
     */
    public void setAuctions(List<Auction> auctions) { this.auctions = auctions; }

    /**
     * Setzt den Report.
     *
     * @param report Report.
     */
    public void setReport(SimulationReport report) { this.report = report; }
}
