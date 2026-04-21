package org.auctionsproject.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.auctionsproject.core.*;
import org.auctionsproject.model.*;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hauptfenster der Anwendung mit vollständiger Steuerung über Tabs.
 */
public class MainLauncher extends Application {

    private final Auktionshaus haus = Auktionshaus.getInstance();
    private final SimulationsEngine engine = new SimulationsEngine(haus);
    private final PersistenceService persistence = new PersistenceService();

    private final ListView<Benutzer> benutzerList = new ListView<>();
    private final ListView<Artikel> artikelList = new ListView<>();
    private final ListView<Auktion> auktionList = new ListView<>();

    private final TextArea verlaufArea = new TextArea();
    private final TextArea berichtArea = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Auktionshaus Simulator - Portfolio WI25A (v2.1)");

        TabPane tabs = new TabPane(
                tabBenutzer(),
                tabArtikel(),
                tabAuktionen(),
                tabEinstellungenUndStart(stage),
                tabVerlaufUndBericht(stage)
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabs, 1120, 760);
        stage.setScene(scene);
        stage.show();
    }

    private Tab tabBenutzer() {
        TextField name = new TextField();
        name.setPromptText("Name");

        ComboBox<BieterTyp> typ = new ComboBox<>();
        typ.getItems().setAll(BieterTyp.values());
        typ.setValue(BieterTyp.ZUFAELLIG);

        TextField basis = new TextField("0.5");

        CheckBox e = new CheckBox("ELEKTRONIK");
        CheckBox k = new CheckBox("KUNST");
        CheckBox a = new CheckBox("ANTIQUITAETEN");
        CheckBox f = new CheckBox("FAHRZEUGE");
        CheckBox m = new CheckBox("MOEBEL");
        CheckBox s = new CheckBox("SCHMUCK");

        Button add = new Button("Benutzer anlegen");
        add.setOnAction(ev -> {
            try {
                Set<Kategorie> ints = new HashSet<>();
                if (e.isSelected()) ints.add(Kategorie.ELEKTRONIK);
                if (k.isSelected()) ints.add(Kategorie.KUNST);
                if (a.isSelected()) ints.add(Kategorie.ANTIQUITAETEN);
                if (f.isSelected()) ints.add(Kategorie.FAHRZEUGE);
                if (m.isSelected()) ints.add(Kategorie.MOEBEL);
                if (s.isSelected()) ints.add(Kategorie.SCHMUCK);
                if (ints.isEmpty()) ints.add(Kategorie.KUNST);

                Benutzer b = new Benutzer(name.getText().trim(), typ.getValue(), ints, Double.parseDouble(basis.getText()));
                haus.addBenutzer(b);
                refreshAll();
                name.clear();
            } catch (Exception ex) {
                alert("Fehler Benutzer", ex.getMessage());
            }
        });

        Button del = new Button("Ausgewählten Benutzer löschen");
        del.setOnAction(evn -> {
            Benutzer selected = benutzerList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                haus.removeBenutzer(selected);
                refreshAll();
            }
        });

        // Im tabBenutzer(): nach add/del ergänzen
        Button edit = new Button("Ausgewählten Benutzer bearbeiten");
        edit.setOnAction(ev2 -> {
            Benutzer selected = benutzerList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            try {
                Set<Kategorie> ints = new HashSet<>();
                if (e.isSelected()) ints.add(Kategorie.ELEKTRONIK);
                if (k.isSelected()) ints.add(Kategorie.KUNST);
                if (a.isSelected()) ints.add(Kategorie.ANTIQUITAETEN);
                if (f.isSelected()) ints.add(Kategorie.FAHRZEUGE);
                if (m.isSelected()) ints.add(Kategorie.MOEBEL);
                if (s.isSelected()) ints.add(Kategorie.SCHMUCK);

                double basisVal = Double.parseDouble(basis.getText());
                ValidationService.validateBenutzer(name.getText().trim(), basisVal, ints);

                haus.removeBenutzer(selected);
                haus.addBenutzer(new Benutzer(name.getText().trim(), typ.getValue(), ints, basisVal));
                refreshAll();
            } catch (Exception ex) {
                alert("Fehler Benutzer-Edit", ex.getMessage());
            }
        });

        VBox box = new VBox(10,
                new Label("Benutzerverwaltung"),
                new HBox(10, new Label("Name:"), name),
                new HBox(10, new Label("Bietertyp:"), typ),
                new HBox(10, new Label("Basis-Wahrscheinlichkeit:"), basis),
                new Label("Interessen:"),
                new FlowPane(10, 10, e, k, a, f, m, s),
                new HBox(10, add, edit, del),
                new Label("Benutzerliste:"),
                benutzerList
        );
        box.setPadding(new Insets(15));
        return new Tab("Benutzer", box);
    }

    private Tab tabArtikel() {
        TextField name = new TextField();
        ComboBox<Kategorie> kat = new ComboBox<>();
        kat.getItems().setAll(Kategorie.values());
        kat.setValue(Kategorie.KUNST);
        TextField wert = new TextField("1000");

        Button add = new Button("Artikel anlegen");
        add.setOnAction(ev -> {
            try {
                haus.addArtikel(new Artikel(name.getText().trim(), kat.getValue(), Double.parseDouble(wert.getText())));
                refreshAll();
                name.clear();
            } catch (Exception ex) {
                alert("Fehler Artikel", ex.getMessage());
            }
        });

        Button del = new Button("Ausgewählten Artikel löschen");
        del.setOnAction(evn -> {
            Artikel selected = artikelList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                haus.removeArtikel(selected);
                refreshAll();
            }
        });

        Button edit = new Button("Ausgewählten Artikel bearbeiten");
        edit.setOnAction(ev2 -> {
            Artikel selected = artikelList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            try {
                String n = name.getText().trim();
                Kategorie kk = kat.getValue();
                double w = Double.parseDouble(wert.getText());
                ValidationService.validateArtikel(n, kk, w);

                haus.removeArtikel(selected);
                haus.addArtikel(new Artikel(n, kk, w));
                refreshAll();
            } catch (Exception ex) {
                alert("Fehler Artikel-Edit", ex.getMessage());
            }
        });

        VBox box = new VBox(10,
                new Label("Artikelverwaltung"),
                new HBox(10, new Label("Name:"), name),
                new HBox(10, new Label("Kategorie:"), kat),
                new HBox(10, new Label("Schätzwert:"), wert),
                new HBox(10, add, edit, del),
                new Label("Artikelliste:"),
                artikelList
        );
        box.setPadding(new Insets(15));
        return new Tab("Artikel", box);
    }

    private Tab tabAuktionen() {
        TextField auktionsName = new TextField("Auktion-" + System.currentTimeMillis());

        ComboBox<Benutzer> auktionator = new ComboBox<>();
        ComboBox<Artikel> artikel = new ComboBox<>();

        TextField start = new TextField("1000");
        TextField mindest = new TextField("300");
        TextField step = new TextField("100");

        ListView<Benutzer> bieterAuswahl = new ListView<>();
        bieterAuswahl.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button refresh = new Button("Listen aktualisieren");
        refresh.setOnAction(e -> {
            auktionator.getItems().setAll(haus.getBenutzer());
            artikel.getItems().setAll(haus.getArtikel());
            bieterAuswahl.getItems().setAll(haus.getBenutzer());
        });
        refresh.fire();

        Button randomBieter = new Button("Bieter zufällig auswählen");
        TextField anzahlRandom = new TextField("2");
        randomBieter.setOnAction(ev -> {
            Benutzer aukt = auktionator.getValue();
            if (aukt == null) {
                alert("Hinweis", "Bitte zuerst Auktionator auswählen.");
                return;
            }
            int n = Integer.parseInt(anzahlRandom.getText());
            List<Benutzer> zufall = engine.zufaelligeBieter(haus.getBenutzer(), aukt, n);
            bieterAuswahl.getSelectionModel().clearSelection();
            for (Benutzer b : zufall) bieterAuswahl.getSelectionModel().select(b);
        });

        Button anlegen = new Button("Auktion anlegen");
        anlegen.setOnAction(ev -> {
            try {
                Benutzer aukt = auktionator.getValue();
                Artikel art = artikel.getValue();
                List<Benutzer> bieter = new ArrayList<>(bieterAuswahl.getSelectionModel().getSelectedItems());
                bieter.remove(aukt);

                AuktionKonfiguration cfg = new AuktionKonfiguration(
                        auktionsName.getText().trim(), art, aukt, bieter,
                        Double.parseDouble(start.getText()),
                        Double.parseDouble(mindest.getText()),
                        Double.parseDouble(step.getText())
                );
                engine.planeAuktion(cfg);
                refreshAll();
            } catch (Exception ex) {
                alert("Fehler Auktion", ex.getMessage());
            }
        });

        Button del = new Button("Ausgewählte Auktion löschen");
        del.setOnAction(ev -> {
            Auktion selected = auktionList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                haus.removeAuktion(selected);
                refreshAll();
            }
        });

        Button edit = new Button("Ausgewählte Auktion bearbeiten");
        edit.setOnAction(ev2 -> {
            Auktion selected = auktionList.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            try {
                Benutzer aukt = auktionator.getValue();
                Artikel art = artikel.getValue();
                List<Benutzer> bieter = new ArrayList<>(bieterAuswahl.getSelectionModel().getSelectedItems());
                bieter.remove(aukt);

                AuktionKonfiguration cfg = new AuktionKonfiguration(
                        auktionsName.getText().trim(), art, aukt, bieter,
                        Double.parseDouble(start.getText()),
                        Double.parseDouble(mindest.getText()),
                        Double.parseDouble(step.getText())
                );
                ValidationService.validateAuktionKonfiguration(cfg);

                haus.removeAuktion(selected);
                haus.addAuktion(new Auktion(cfg));
                refreshAll();
            } catch (Exception ex) {
                alert("Fehler Auktion-Edit", ex.getMessage());
            }
        });

        VBox box = new VBox(10,
                new Label("Auktionsplanung"),
                new HBox(10, new Label("Name:"), auktionsName),
                new HBox(10, new Label("Auktionator:"), auktionator),
                new HBox(10, new Label("Artikel:"), artikel),
                new HBox(10, new Label("Startpreis:"), start),
                new HBox(10, new Label("Mindestpreis:"), mindest),
                new HBox(10, new Label("Preisschritt:"), step),
                new Label("Registrierte Bieter:"),
                bieterAuswahl,
                new HBox(10, randomBieter, new Label("Anzahl:"), anzahlRandom),
                new HBox(10, refresh, anlegen, edit, del),
                new Label("Auktionsliste:"),
                auktionList
        );
        box.setPadding(new Insets(15));
        return new Tab("Auktionen", box);
    }

    private Tab tabEinstellungenUndStart(Stage stage) {
        TextField maxParallel = new TextField(String.valueOf(haus.getMaxParallelAuktionen()));
        Button save = new Button("Parallelität speichern");
        Button start = new Button("Simulation starten");
        Button reset = new Button("Alles zurücksetzen");

        Button saveJson = new Button("Konfiguration speichern (JSON)");
        Button loadJson = new Button("Konfiguration laden (JSON)");

        save.setOnAction(e -> {
            try {
                haus.setMaxParallelAuktionen(Integer.parseInt(maxParallel.getText()));
                alert("Info", "Max. parallele Auktionen gesetzt auf: " + haus.getMaxParallelAuktionen());
            } catch (Exception ex) {
                alert("Fehler", ex.getMessage());
            }
        });

        start.setOnAction(e -> {
            start.setDisable(true);
            new Thread(() -> {
                engine.starteSimulation();
                Platform.runLater(() -> {
                    refreshAll();
                    updateVerlaufUndBericht();
                    start.setDisable(false);
                });
            }).start();
        });

        reset.setOnAction(e -> {
            haus.resetAll();
            refreshAll();
            verlaufArea.clear();
            berichtArea.clear();
        });

        saveJson.setOnAction(e -> {
            try {
                FileChooser fc = new FileChooser();
                fc.setTitle("JSON speichern");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
                File f = fc.showSaveDialog(stage);
                if (f != null) {
                    Path p = f.toPath();
                    persistence.save(haus, p);
                    alert("Gespeichert", "Datei gespeichert: " + p);
                }
            } catch (Exception ex) {
                alert("Speicherfehler", ex.getMessage());
            }
        });

        loadJson.setOnAction(e -> {
            try {
                FileChooser fc = new FileChooser();
                fc.setTitle("JSON laden");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
                File f = fc.showOpenDialog(stage);
                if (f != null) {
                    Path p = f.toPath();
                    persistence.load(haus, p);
                    refreshAll();
                    updateVerlaufUndBericht();
                    alert("Geladen", "Datei geladen: " + p);
                }
            } catch (Exception ex) {
                alert("Ladefehler", ex.getMessage());
            }
        });

        VBox box = new VBox(10,
                new Label("Simulationseinstellungen"),
                new HBox(10, new Label("Max. parallele Auktionen:"), maxParallel),
                new HBox(10, save, start, reset),
                new Separator(),
                new Label("Persistenz"),
                new HBox(10, saveJson, loadJson)
        );
        box.setPadding(new Insets(15));
        return new Tab("Einstellungen & Start", box);
    }

    private Tab tabVerlaufUndBericht(Stage stage) {
        verlaufArea.setEditable(false);
        berichtArea.setEditable(false);
        verlaufArea.setPrefHeight(380);
        berichtArea.setPrefHeight(230);

        Button refresh = new Button("Verlauf/Bericht aktualisieren");
        Button export = new Button("Verlauf + Bericht exportieren");

        refresh.setOnAction(e -> updateVerlaufUndBericht());

        export.setOnAction(e -> {
            try {
                FileChooser fc = new FileChooser();
                fc.setTitle("Export Textdatei");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Textdatei", "*.txt"));
                File f = fc.showSaveDialog(stage);
                if (f != null) {
                    String content = "=== Verlauf ===\n" + verlaufArea.getText() + "\n\n=== Bericht ===\n" + berichtArea.getText();
                    java.nio.file.Files.writeString(f.toPath(), content);
                    alert("Export", "Export erfolgreich: " + f.toPath());
                }
            } catch (Exception ex) {
                alert("Exportfehler", ex.getMessage());
            }
        });

        VBox box = new VBox(10,
                new Label("Auktionsverlauf"),
                verlaufArea,
                new Label("Abschlussbericht"),
                berichtArea,
                new HBox(10, refresh, export)
        );
        box.setPadding(new Insets(15));
        return new Tab("Verlauf & Bericht", box);
    }

    private void updateVerlaufUndBericht() {
        String logs = haus.gesamterVerlauf().stream()
                .map(ev -> "[" + ev.timestamp() + "] [" + ev.auktionName() + "] " + ev.nachricht())
                .collect(Collectors.joining("\n"));
        verlaufArea.setText(logs);
        berichtArea.setText(haus.generiereBericht());
    }

    private void refreshAll() {
        benutzerList.getItems().setAll(haus.getBenutzer());
        artikelList.getItems().setAll(haus.getArtikel());
        auktionList.getItems().setAll(haus.getAuktionen());
    }

    private void alert(String titel, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titel);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}