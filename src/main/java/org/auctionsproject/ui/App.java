package org.auctionsproject.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Startklasse.
 */
public class App extends Application {
    /**
     * Erstellt die Hauptszene und zeigt das Fenster an.
     *
     * @param stage primäres JavaFX-Fenster.
     */
    @Override
    public void start(Stage stage) {
        MainView view = new MainView();
        Scene scene = new Scene(view.getRoot(), 1200, 750);
        stage.setTitle("Auktionshaus Simulator");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Startet die JavaFX-Runtime.
     *
     * @param args Kommandozeilenargumente.
     */
    public static void main(String[] args) {
        launch();
    }
}
