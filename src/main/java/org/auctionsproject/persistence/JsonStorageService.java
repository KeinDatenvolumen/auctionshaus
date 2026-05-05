package org.auctionsproject.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.auctionsproject.model.Auction;
import org.auctionsproject.model.Auctioneer;
import org.auctionsproject.model.Bidder;
import org.auctionsproject.model.User;
import org.auctionsproject.service.SimulationReport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public void relink(StateSnapshot snapshot) {
        Map<Integer, User> userById = snapshot.getUsers().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        for (Auction a : snapshot.getAuctions()) {
            // Auctioneer referenzieren
            User u = userById.get(a.getAuctioneer().getId());
            if (u instanceof Auctioneer ae) {
                a.setAuctioneer(ae);
            }

            // Bieter referenzieren
            List<Bidder> relinked = a.getBidders().stream()
                    .map(b -> (Bidder) userById.get(b.getId()))
                    .filter(Objects::nonNull)
                    .toList();
            a.setBidders(new ArrayList<>(relinked));

            // Winner referenzieren
            if (a.getWinner() != null) {
                User w = userById.get(a.getWinner().getId());
                if (w instanceof Bidder bw) {
                    a.setWinner(bw);
                }
            }
        }
    }
}