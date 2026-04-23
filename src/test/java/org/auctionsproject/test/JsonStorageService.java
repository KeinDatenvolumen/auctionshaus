package org.auctionsproject.test;

import org.auctionsproject.model.*;
import org.auctionsproject.persistence.JsonStorageService;
import org.auctionsproject.persistence.StateSnapshot;
import org.auctionsproject.service.SimulationReport;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonStorageServiceTest {

    @Test
    void saveAndLoadState() throws Exception {
        JsonStorageService s = new JsonStorageService();
        Path tmp = Files.createTempFile("auction-state", ".json");

        List<User> users = List.of(
                new Auctioneer(1, "A"),
                new Bidder(2, "B", 500, new RandomStrategy())
        );

        Item item = new Item(1, "Cam", "Tech", 300, 150);
        Auction auction = new Auction(1, item, (Auctioneer) users.get(0), List.of((Bidder) users.get(1)), 10);

        SimulationReport report = new SimulationReport();
        report.setTotalAuctions(1);

        s.saveAll(tmp.toString(), users, List.of(auction), report);

        StateSnapshot loaded = s.loadAll(tmp.toString());
        assertEquals(2, loaded.getUsers().size());
        assertEquals(1, loaded.getAuctions().size());
        assertEquals(1, loaded.getReport().getTotalAuctions());
    }
}