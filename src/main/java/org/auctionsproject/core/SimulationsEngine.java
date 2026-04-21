package org.auctionsproject.core;

import org.auctionsproject.model.AuktionKonfiguration;
import org.auctionsproject.model.Benutzer;
import org.auctionsproject.model.Kategorie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Führt die Simulation mit konfigurierbarer Parallelität aus.
 */
public class SimulationsEngine {
    private final Auktionshaus haus;
    private final Random random = new Random();

    /**
     * Konstruktor.
     * @param haus Singleton-Instanz.
     */
    public SimulationsEngine(Auktionshaus haus) {
        this.haus = haus;
    }

    /**
     * Plant eine neue Auktion und registriert sie im Auktionshaus.
     * @param cfg Konfiguration.
     */
    public void planeAuktion(AuktionKonfiguration cfg) {
        haus.addAuktion(new Auktion(cfg));
    }

    /**
     * Startet alle geplanten Auktionen unter Einhaltung von maxParallelAuktionen.
     */
    public void starteSimulation() {
        ValidationService.validateSimulationStart(haus.getAuktionen(), haus.getMaxParallelAuktionen());
        List<Auktion> alle = haus.getAuktionen();
        int maxParallel = haus.getMaxParallelAuktionen();

        ExecutorService pool = Executors.newFixedThreadPool(maxParallel);
        CountDownLatch latch = new CountDownLatch(alle.size());

        for (Auktion auktion : alle) {
            pool.submit(() -> {
                try {
                    simuliereAuktion(auktion);
                } finally {
                    latch.countDown();
                }
            });
        }

        pool.shutdown();
        try {
            latch.await(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void simuliereAuktion(Auktion auktion) {
        auktion.starten();

        while (auktion.getStatus().name().equals("LAEUFT")) {
            // Bieterentscheidungen
            for (Benutzer b : auktion.getRegistrierteBieter()) {
                double p = berechneGebotsWahrscheinlichkeit(b, auktion.getArtikel().kategorie());
                if (random.nextDouble() <= p) {
                    boolean gewonnen = auktion.kaufversuch(b, auktion.getAktuellerPreis());
                    if (gewonnen) return;
                }
            }

            // Preis senken
            auktion.preisSenken();

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private double berechneGebotsWahrscheinlichkeit(Benutzer bieter, Kategorie kategorie) {
        double basis = bieter.getBasisGebotsWahrscheinlichkeit();
        double typFaktor = bieter.getBieterTyp().getWahrscheinlichkeitFaktor();
        double interessenFaktor = bieter.interessiertAn(kategorie) ? 1.0 : 0.5; // 50%-Malus
        double gesamt = basis * typFaktor * interessenFaktor;
        return Math.max(0.0, Math.min(1.0, gesamt));
    }

    /**
     * Zufällige Bieterauswahl ohne Auktionator.
     * @param alleBenutzer Gesamtbenutzer.
     * @param auktionator Auktionator.
     * @param anzahl Anzahl gewünschter Bieter.
     * @return Liste Bieter.
     */
    public List<Benutzer> zufaelligeBieter(List<Benutzer> alleBenutzer, Benutzer auktionator, int anzahl) {
        List<Benutzer> kopie = new ArrayList<>(alleBenutzer);
        kopie.remove(auktionator);
        java.util.Collections.shuffle(kopie, random);
        return kopie.subList(0, Math.min(anzahl, kopie.size()));
    }
}