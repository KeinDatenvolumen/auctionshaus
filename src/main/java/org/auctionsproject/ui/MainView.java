package org.auctionsproject.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.auctionsproject.model.*;
import org.auctionsproject.persistence.JsonStorageService;
import org.auctionsproject.persistence.StateSnapshot;
import org.auctionsproject.service.AuctionHouse;
import org.auctionsproject.service.SimulationReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hauptoberfläche (ohne FXML, bewusst einfach).
 */
public class MainView {
    private final BorderPane root = new BorderPane();

    private final AuctionHouse auctionHouse = new AuctionHouse("WI25A-House");
    private final JsonStorageService storageService = new JsonStorageService();

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Auction> auctions = FXCollections.observableArrayList();

    private final TableView<User> usersTable = new TableView<>();
    private final TableView<Auction> auctionsTable = new TableView<>();
    private final TextArea output = new TextArea();

    /**
     * Erstellt die Hauptansicht und initialisiert UI, Demo-Daten und Listener.
     */
    public MainView() {
        buildUI();

        auctionHouse.setEventListener(msg -> Platform.runLater(() -> {
            output.appendText(msg + "\n");
            auctionsTable.refresh(); // Live-Aktualisierung Tabelle
        }));

        seedDemoUsers();
        refreshTables();
    }

    /**
     * Liefert den Wurzelknoten der Ansicht.
     *
     * @return Root-Node für die Szene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Baut die Gesamtlayout-Struktur mit Tabs und Log-Ausgabe.
     */
    private void buildUI() {
        root.setPadding(new Insets(10));

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Nutzer", buildUsersPane()));
        tabs.getTabs().add(new Tab("Auktionen", buildAuctionsPane()));
        tabs.getTabs().add(new Tab("Simulation & Report", buildSimulationPane()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        root.setCenter(tabs);
        output.setEditable(false);
        output.setPrefRowCount(8);
        root.setBottom(output);
    }

    /**
     * Baut den Tab für die Nutzerverwaltung.
     *
     * @return UI-Knoten für den Nutzer-Tab.
     */
    private Parent buildUsersPane() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));

        TableColumn<User, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<User, String> cType = new TableColumn<>("Typ");
        cType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue() instanceof Auctioneer ? "Auktionator" : "Bieter"));

        TableColumn<User, String> cDetails = new TableColumn<>("Details");
        cDetails.setCellValueFactory(data -> {
            if (data.getValue() instanceof Bidder b) {
                return new javafx.beans.property.SimpleStringProperty(
                        "Budget=" + String.format("%.2f", b.getBudget())
                                + ", Strategie=" + b.getStrategyName()
                                + ", Interesse=" + b.getPreferredCategory()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        usersTable.getColumns().setAll(cName, cType, cDetails);
        usersTable.setItems(users);

        Button addAuctioneer = new Button("Auktionator hinzufügen");
        addAuctioneer.setOnAction(e -> addAuctioneerDialog());

        Button addBidder = new Button("Bieter hinzufügen");
        addBidder.setOnAction(e -> addBidderDialog());

        Button removeBidder = new Button("Bieter entfernen (markiert)");
        removeBidder.setOnAction(e -> removeSelectedBidder());

        HBox buttons = new HBox(8, addAuctioneer, addBidder, removeBidder
        );
        box.getChildren().addAll(buttons, usersTable);
        return box;
    }

    /**
     * Baut den Tab zur Auktionserstellung und -anzeige.
     *
     * @return UI-Knoten für den Auktionen-Tab.
     */
    private Parent buildAuctionsPane() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));

        TableColumn<Auction, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getId())));
        TableColumn<Auction, String> cItem = new TableColumn<>("Artikel");
        cItem.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getItem().getName()));
        TableColumn<Auction, String> cPrice = new TableColumn<>("Akt. Preis");
        cPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", data.getValue().getCurrentPrice())));
        TableColumn<Auction, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));

        auctionsTable.getColumns().setAll(cId, cItem, cPrice, cStatus);
        auctionsTable.setItems(auctions);

        Button addAuction = new Button("Auktion erstellen");
        addAuction.setOnAction(e -> addAuctionDialog());

        box.getChildren().addAll(addAuction, auctionsTable);
        return box;
    }

    /**
     * Baut den Tab zur Simulation und zum Reporting.
     *
     * @return UI-Knoten für den Simulation-Tab.
     */
    private Parent buildSimulationPane() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));

        TextField parallelField = new TextField("3");
        TextField tickField = new TextField("150");

        Button start = new Button("Simulation starten");
        start.setOnAction(e -> {
            int parallel = Integer.parseInt(parallelField.getText().trim());
            long tick = Long.parseLong(tickField.getText().trim());

            Thread t = new Thread(() -> {
                auctionHouse.startSimulation(parallel, tick);
                Platform.runLater(() -> {
                    refreshTables();
                    SimulationReport report = auctionHouse.getReport();
                    log(report.toPrettyString());
                });
            });
            t.setDaemon(true);
            t.start();
        });

        Button showReport = new Button("Report anzeigen");
        showReport.setOnAction(e -> log(auctionHouse.getReport().toPrettyString()));

        Button save = new Button("Als JSON speichern");
        save.setOnAction(e -> {
            try {
                storageService.saveAll("auction_state.json", auctionHouse.getUsers(), auctionHouse.getAuctions(), auctionHouse.getReport());
                log("Gespeichert in auction_state.json");
            } catch (IOException ex) {
                log("Fehler beim Speichern: " + ex.getMessage());
            }
        });

        Button load = new Button("JSON laden");
        load.setOnAction(e -> {
            try {
                StateSnapshot s = storageService.loadAll("auction_state.json");
                storageService.relink(s);
                auctionHouse.getUsers().clear();
                auctionHouse.getUsers().addAll(s.getUsers());

                auctionHouse.getAuctions().clear();
                auctionHouse.getAuctions().addAll(s.getAuctions());

                refreshTables();
                log("State geladen.");
                log(s.getReport().toPrettyString());
            } catch (IOException ex) {
                log("Fehler beim Laden: " + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.addRow(0, new Label("Parallel-Auktionen:"), parallelField);
        grid.addRow(1, new Label("Tick (ms):"), tickField);

        HBox buttons = new HBox(8, start, showReport, save, load);
        box.getChildren().addAll(grid, buttons);
        return box;
    }

    /**
     * Öffnet einen Dialog zum Erstellen eines neuen Auktionators.
     */
    private void addAuctioneerDialog() {
        TextInputDialog d = new TextInputDialog("Auktionator");
        d.setHeaderText("Name Auktionator");
        d.showAndWait().ifPresent(name -> {
            int id = users.size() + 1;
            Auctioneer a = new Auctioneer(id, name);
            auctionHouse.registerUser(a);
            refreshTables();
            log("Auktionator hinzugefügt: " + name);
        });
    }

    /**
     * Öffnet einen Dialog zum Erstellen eines neuen Bieters.
     */
    private void addBidderDialog() {
        Dialog<Bidder> dialog = new Dialog<>();
        dialog.setTitle("Bieter hinzufügen");

        TextField nameField = new TextField("Bieter");
        TextField budgetField = new TextField("500");
        ComboBox<String> strategyBox = new ComboBox<>(FXCollections.observableArrayList("Aggressive", "Conservative", "Random"));
        strategyBox.getSelectionModel().select("Random");

        ComboBox<ItemCategory> interestBox = new ComboBox<>();
        interestBox.getItems().setAll(ItemCategory.values());
        interestBox.getSelectionModel().select(ItemCategory.ANY);

        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        g.addRow(0, new Label("Name:"), nameField);
        g.addRow(1, new Label("Budget:"), budgetField);
        g.addRow(2, new Label("Strategie:"), strategyBox);
        g.addRow(3, new Label("Interesse (Kategorie):"), interestBox);
        dialog.getDialogPane().setContent(g);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                int id = users.size() + 1;
                double budget = Double.parseDouble(budgetField.getText().trim());
                BidStrategy strategy = switch (strategyBox.getValue()) {
                    case "Aggressive" -> new AggressiveStrategy();
                    case "Conservative" -> new ConservativeStrategy();
                    default -> new RandomStrategy();
                };
                return new Bidder(
                        id,
                        nameField.getText().trim(),
                        budget,
                        strategy,
                        interestBox.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(b -> {
            auctionHouse.registerUser(b);
            refreshTables();
            log("Bieter hinzugefügt: " + b.getName());
        });
    }

    /**
     * Öffnet einen Dialog zum Erstellen einer neuen Auktion.
     */
    private void addAuctionDialog() {
        List<Auctioneer> auctioneers = users.stream().filter(u -> u instanceof Auctioneer).map(u -> (Auctioneer) u).toList();
        List<Bidder> bidders = users.stream().filter(u -> u instanceof Bidder).map(u -> (Bidder) u).toList();

        if (auctioneers.isEmpty() || bidders.isEmpty()) {
            log("Bitte erst Nutzer anlegen: mind. 1 Auktionator und 1 Bieter.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Auktion erstellen");

        TextField itemName = new TextField("Laptop");
        ComboBox<ItemCategory> catBox = new ComboBox<>();
        catBox.getItems().setAll(
                ItemCategory.ELEKTRONIK,
                ItemCategory.AUTO,
                ItemCategory.BUCH,
                ItemCategory.MODE,
                ItemCategory.MOEBEL,
                ItemCategory.SONSTIGES
        );
        catBox.getSelectionModel().select(ItemCategory.ELEKTRONIK);

        TextField start = new TextField("1000");
        TextField min = new TextField("400");
        TextField step = new TextField("50");

        ComboBox<Auctioneer> aBox = new ComboBox<>(FXCollections.observableArrayList(auctioneers));
        aBox.setCellFactory(_ -> new ListCell<>() {
            @Override protected void updateItem(Auctioneer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        aBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Auctioneer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        aBox.getSelectionModel().selectFirst();

        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(8);
        g.addRow(0, new Label("Artikel:"), itemName);
        g.addRow(1, new Label("Kategorie:"), catBox);
        g.addRow(2, new Label("Startpreis:"), start);
        g.addRow(3, new Label("Mindestpreis:"), min);
        g.addRow(4, new Label("Schritt:"), step);
        g.addRow(5, new Label("Auktionator:"), aBox);

        dialog.getDialogPane().setContent(g);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            try {
                int itemId = auctions.size() + 1;
                Item item = new Item(
                        itemId,
                        itemName.getText().trim(),
                        catBox.getValue(),
                        Double.parseDouble(start.getText().trim()),
                        Double.parseDouble(min.getText().trim())
                );

                Auctioneer auctioneer = aBox.getValue();
                List<Bidder> randomBidders = randomBidderSubset(bidders);

                Auction auction = auctionHouse.createAuction(
                        item, auctioneer, randomBidders, Double.parseDouble(step.getText().trim())
                );

                refreshTables(); // damit sofort sichtbar
                auctionsTable.refresh();
                log("Auktion erstellt: #" + auction.getId() + " mit " + randomBidders.size() + " Bietern");
            } catch (Exception ex) {
                log("Fehler beim Erstellen: " + ex.getMessage());
            }
        });
    }

    /**
     * Erstellt eine zufällige Teilmenge der verfügbaren Bieter.
     *
     * @param all alle registrierten Bieter.
     * @return zufällige, nicht leere Bieter-Teilmenge.
     */
    private List<Bidder> randomBidderSubset(List<Bidder> all) {
        List<Bidder> copy = new ArrayList<>(all);
        java.util.Collections.shuffle(copy);
        int size = ThreadLocalRandom.current().nextInt(1, copy.size() + 1);
        return copy.subList(0, size);
    }

    /**
     * Legt Demo-Nutzer für den Schnellstart an.
     */
    private void seedDemoUsers() {
        auctionHouse.registerUser(new Auctioneer(1, "Alice"));
        auctionHouse.registerUser(new Auctioneer(2, "Bob"));
        auctionHouse.registerUser(new Bidder(3, "Clara", 900, new AggressiveStrategy(), ItemCategory.ELEKTRONIK));
        auctionHouse.registerUser(new Bidder(4, "David", 650, new ConservativeStrategy(), ItemCategory.BUCH));
        auctionHouse.registerUser(new Bidder(5, "Elias", 500, new RandomStrategy(), ItemCategory.ANY));
    }

    /**
     * Synchronisiert die Tabellen mit dem aktuellen Zustand des AuctionHouse.
     */
    private void refreshTables() {
        users.setAll(auctionHouse.getUsers());
        auctions.setAll(auctionHouse.getAuctions());
    }

    /**
     * Schreibt eine Nachricht in das Log-Feld.
     *
     * @param msg anzuzeigende Nachricht.
     */
    private void log(String msg) {
        output.appendText(msg + "\n");
    }

    /**
     * Entfernt den im UI ausgewählten Bieter aus dem System.
     */
    private void removeSelectedBidder() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log("Bitte zuerst einen Nutzer markieren.");
            return;
        }
        if (!(selected instanceof Bidder bidder)) {
            log("Nur Bieter können mit dieser Aktion entfernt werden.");
            return;
        }

        boolean ok = auctionHouse.removeBidderById(bidder.getId());
        if (ok) {
            refreshTables();
            auctionsTable.refresh();
            log("Bieter entfernt: " + bidder.getName());
        } else {
            log("Bieter konnte nicht entfernt werden.");
        }
    }
}
