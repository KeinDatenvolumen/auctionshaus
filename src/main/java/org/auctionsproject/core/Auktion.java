package org.auctionsproject.core;

import org.auctionsproject.exceptions.AuktionException;
import org.auctionsproject.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-sichere Repräsentation einer niederländischen Auktion.
 */
public class Auktion {
    private final String name;
    private final Artikel artikel;
    private final Benutzer auktionator;
    private final List<Benutzer> registrierteBieter;
    private final double startPreis;
    private final double mindestPreis;
    private final double preisSchritt;

    private final ReentrantLock lock = new ReentrantLock();

    private volatile AuktionsStatus status = AuktionsStatus.GEPLANT;
    private double aktuellerPreis;
    private Benutzer gewinner;

    private final List<AuktionsEvent> verlauf = new ArrayList<>();

    /**
     * Konstruktor.
     * @param cfg Konfiguration.
     */
    public Auktion(AuktionKonfiguration cfg) {
        this.name = cfg.name();
        this.artikel = cfg.artikel();
        this.auktionator = cfg.auktionator();
        this.registrierteBieter = new ArrayList<>(cfg.bieter());
        this.startPreis = cfg.startPreis();
        this.mindestPreis = cfg.mindestPreis();
        this.preisSchritt = cfg.preisSchritt();
        this.aktuellerPreis = startPreis;

        if (startPreis <= mindestPreis) throw new AuktionException("Startpreis muss größer als Mindestpreis sein.");
        if (preisSchritt <= 0) throw new AuktionException("Preisschritt muss > 0 sein.");
        if (registrierteBieter.isEmpty()) throw new AuktionException("Mindestens ein Bieter muss registriert sein.");
        if (registrierteBieter.contains(auktionator)) throw new AuktionException("Auktionator darf nicht Bieter sein.");
    }

    /**
     * Startet Auktion.
     */
    public void starten() {
        lock.lock();
        try {
            if (status != AuktionsStatus.GEPLANT) return;
            status = AuktionsStatus.LAEUFT;
            event("Auktion gestartet. Artikel: " + artikel.name() + ", Startpreis: " + startPreis
                    + ", Mindestpreis (geheim), Schritt: " + preisSchritt);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Senkt Preis um einen Schritt.
     */
    public void preisSenken() {
        lock.lock();
        try {
            if (status != AuktionsStatus.LAEUFT) return;
            aktuellerPreis -= preisSchritt;
            event("Preis gesenkt auf " + String.format("%.2f", aktuellerPreis) + " €");

            if (aktuellerPreis < mindestPreis) {
                status = AuktionsStatus.ZURUECKGEZOGEN;
                event("Mindestpreis unterschritten. Artikel zurückgezogen.");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Bieter signalisiert Kaufwunsch und versucht Zuschlag.
     * @param bieter Bieter.
     * @param wahrgenommenerPreis Gesehener Preis.
     * @return true bei Zuschlag.
     */
    public boolean kaufversuch(Benutzer bieter, double wahrgenommenerPreis) {
        lock.lock();
        try {
            if (status != AuktionsStatus.LAEUFT) return false;
            if (!registrierteBieter.contains(bieter)) return false;

            event("Bieter " + bieter.getName() + " möchte diesen Artikel kaufen.");

            if (wahrgenommenerPreis >= aktuellerPreis) {
                gewinner = bieter;
                status = AuktionsStatus.VERKAUFT;
                event("Artikel '" + artikel.name() + "' an Bieter " + bieter.getName()
                        + " verkauft für " + String.format("%.2f", aktuellerPreis) + " €");
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void event(String text) {
        verlauf.add(new AuktionsEvent(LocalDateTime.now(), name, text));
    }

    public String getName() { return name; }
    public Artikel getArtikel() { return artikel; }
    public Benutzer getAuktionator() { return auktionator; }
    public List<Benutzer> getRegistrierteBieter() { return List.copyOf(registrierteBieter); }
    public AuktionsStatus getStatus() { return status; }
    public double getAktuellerPreis() { return aktuellerPreis; }
    public Benutzer getGewinner() { return gewinner; }
    public double getVerkaufspreis() { return gewinner == null ? 0.0 : aktuellerPreis; }
    public List<AuktionsEvent> getVerlaufSnapshot() { return List.copyOf(verlauf); }

    /**
     * Startpreis der Auktion.
     * @return Startpreis.
     */
    public double getStartPreis() { return startPreis; }

    /**
     * Mindestpreis der Auktion.
     * @return Mindestpreis.
     */
    public double getMindestPreis() { return mindestPreis; }

    /**
     * Preisschritt der Auktion.
     * @return Schrittweite.
     */
    public double getPreisSchritt() { return preisSchritt; }

    @Override
    public String toString() {
        return name + " | " + artikel.name() + " | Status: " + status;
    }
}