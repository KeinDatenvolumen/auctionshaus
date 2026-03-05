package org.auctionsproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main-Klasse für die JavaFX Applikation.
 */
public class AuctionApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Uni-Projekt: Niederländisches Auktionshaus");

        // GUI Elemente
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(400);

        Button startBtn = new Button("Simulation Starten");
        startBtn.setMaxWidth(Double.MAX_VALUE);

        // Auktionshaus konfigurieren
        AuctionHouse house = AuctionHouse.getInstance();
        house.setLogArea(logArea);

        // Klick-Logik
        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            logArea.clear();
            startSimulation(house);
        });

        // Layout
        VBox root = new VBox(10, startBtn, logArea);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            house.shutdown(); // Threads beim Schließen killen
            Platform.exit();
        });
        primaryStage.show();
    }

    private void startSimulation(AuctionHouse house) {
        // Konfiguration laut Aufgabenstellung
        User auctioneer1 = new User("Auktionator Alice", 0.0);
        User auctioneer2 = new User("Auktionator Bob", 0.0);

        // Bieter mit verschiedenen Profilen (aggressiv / konservativ)
        User bidder1 = new User("Bieter Charlie (aggressiv)", 0.4);
        User bidder2 = new User("Bieter Dave (konservativ)", 0.05);
        User bidder3 = new User("Bieter Eve (normal)", 0.15);

        User[] bidders = {bidder1, bidder2, bidder3};

        // Zwei Auktionen parallel starten
        Auction auction1 = new Auction("Antike Vase", auctioneer1, 500.0, 200.0, 25.0);
        Auction auction2 = new Auction("Gaming Laptop", auctioneer2, 1200.0, 700.0, 50.0);

        house.startAuction(auction1, bidders);
        house.startAuction(auction2, bidders);
    }

    public static void main(String[] args) {
        launch(args);
    }
}