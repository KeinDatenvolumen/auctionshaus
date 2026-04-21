package org.auctionsproject.core;

import org.auctionsproject.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton-Verwaltung für Nutzer, Artikel, Auktionen und Statistiken.
 */
public class Auktionshaus {
    private static Auktionshaus instance;

    private final List<Benutzer> benutzer = new CopyOnWriteArrayList<>();
    private final List<Artikel> artikel = new CopyOnWriteArrayList<>();
    private final List<Auktion> auktionen = new CopyOnWriteArrayList<>();

    private int maxParallelAuktionen = 3;
    private final double provisionsSatz = 0.01;

    private Auktionshaus() { }

    public static synchronized Auktionshaus getInstance() {
        if (instance == null) instance = new Auktionshaus();
        return instance;
    }

    public void resetAll() {
        benutzer.clear();
        artikel.clear();
        auktionen.clear();
        maxParallelAuktionen = 3;
    }

    public List<Benutzer> getBenutzer() { return List.copyOf(benutzer); }
    public List<Artikel> getArtikel() { return List.copyOf(artikel); }
    public List<Auktion> getAuktionen() { return List.copyOf(auktionen); }

    public void addBenutzer(Benutzer b) { benutzer.add(b); }
    public void addArtikel(Artikel a) { artikel.add(a); }
    public void addAuktion(Auktion a) { auktionen.add(a); }

    public int getMaxParallelAuktionen() { return maxParallelAuktionen; }
    public void setMaxParallelAuktionen(int maxParallelAuktionen) {
        this.maxParallelAuktionen = Math.max(1, maxParallelAuktionen);
    }

    public double getProvisionsSatz() { return provisionsSatz; }

    public String generiereBericht() {
        int anzahlAuktionen = auktionen.size();
        int verkauft = 0;
        int zurueckgezogen = 0;
        int einsatzAuktionator = 0;
        int einsatzBieter = 0;
        int summeBieterProAuktion = 0;
        double provision = 0.0;

        for (Auktion a : auktionen) {
            if (a.getStatus() == AuktionsStatus.VERKAUFT) {
                verkauft++;
                provision += a.getVerkaufspreis() * provisionsSatz;
            }
            if (a.getStatus() == AuktionsStatus.ZURUECKGEZOGEN) {
                zurueckgezogen++;
            }
            einsatzAuktionator++;
            einsatzBieter += a.getRegistrierteBieter().size();
            summeBieterProAuktion += a.getRegistrierteBieter().size();
        }

        double avgBieter = anzahlAuktionen == 0 ? 0 : (double) summeBieterProAuktion / anzahlAuktionen;

        return """
                === Auktionshaus Abschlussbericht ===
                Durchgeführte Auktionen: %d
                Verkaufte Artikel: %d
                Zurückgezogene Artikel: %d
                Nutzer gesamt: %d
                Einsätze als Auktionator: %d
                Einsätze als Bieter: %d
                Durchschnittliche Bieter pro Auktion: %.2f
                Gesamtprovision (1%%): %.2f €
                =====================================
                """.formatted(anzahlAuktionen, verkauft, zurueckgezogen, benutzer.size(), einsatzAuktionator,
                einsatzBieter, avgBieter, provision);
    }

    public List<AuktionsEvent> gesamterVerlauf() {
        List<AuktionsEvent> all = new ArrayList<>();
        for (Auktion a : auktionen) {
            all.addAll(a.getVerlaufSnapshot());
        }
        all.sort((x, y) -> x.timestamp().compareTo(y.timestamp()));
        return all;
    }

    /**
     * Entfernt einen Benutzer.
     * @param b Benutzer.
     */
    public void removeBenutzer(Benutzer b) { benutzer.remove(b); }

    /**
     * Entfernt einen Artikel.
     * @param a Artikel.
     */
    public void removeArtikel(Artikel a) { artikel.remove(a); }

    /**
     * Entfernt eine Auktion.
     * @param a Auktion.
     */
    public void removeAuktion(Auktion a) { auktionen.remove(a); }
}