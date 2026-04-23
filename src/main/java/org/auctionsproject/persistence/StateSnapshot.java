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

    public List<User> getUsers() { return users; }
    public List<Auction> getAuctions() { return auctions; }
    public SimulationReport getReport() { return report; }

    public void setUsers(List<User> users) { this.users = users; }
    public void setAuctions(List<Auction> auctions) { this.auctions = auctions; }
    public void setReport(SimulationReport report) { this.report = report; }
}