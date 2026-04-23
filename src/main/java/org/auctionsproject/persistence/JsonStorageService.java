package org.auctionsproject.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.auctionsproject.model.Auction;
import org.auctionsproject.model.User;
import org.auctionsproject.service.SimulationReport;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * JSON Speicherdienst.
 */
public class JsonStorageService {
    private final ObjectMapper mapper;

    public JsonStorageService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveAll(String path, List<User> users, List<Auction> auctions, SimulationReport report) throws IOException {
        StateSnapshot snapshot = new StateSnapshot();
        snapshot.setUsers(users);
        snapshot.setAuctions(auctions);
        snapshot.setReport(report);
        mapper.writeValue(new File(path), snapshot);
    }

    public StateSnapshot loadAll(String path) throws IOException {
        return mapper.readValue(new File(path), StateSnapshot.class);
    }
}