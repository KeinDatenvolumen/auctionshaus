package org.auctionsproject.core;

import org.auctionsproject.exceptions.ValidierungsException;
import org.auctionsproject.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Zentrale Validierung aller Benutzereingaben und Konfigurationsobjekte.
 */
public final class ValidationService {

    private ValidationService() { }

    public static void validateBenutzer(String name, double basis, Set<Kategorie> interessen) {
        if (name == null || name.isBlank()) {
            throw new ValidierungsException("Benutzername darf nicht leer sein.");
        }
        if (basis < 0.0 || basis > 1.0) {
            throw new ValidierungsException("Basis-Gebotswahrscheinlichkeit muss zwischen 0 und 1 liegen.");
        }
        if (interessen == null || interessen.isEmpty()) {
            throw new ValidierungsException("Mindestens eine Interessenkategorie muss ausgewählt sein.");
        }
    }

    public static void validateArtikel(String name, Kategorie kategorie, double wert) {
        if (name == null || name.isBlank()) {
            throw new ValidierungsException("Artikelname darf nicht leer sein.");
        }
        if (kategorie == null) {
            throw new ValidierungsException("Kategorie muss gesetzt sein.");
        }
        if (wert < 0) {
            throw new ValidierungsException("Geschätzter Wert darf nicht negativ sein.");
        }
    }

    public static void validateAuktionKonfiguration(AuktionKonfiguration cfg) {
        if (cfg == null) throw new ValidierungsException("Auktionskonfiguration fehlt.");
        if (cfg.name() == null || cfg.name().isBlank()) {
            throw new ValidierungsException("Auktionsname darf nicht leer sein.");
        }
        if (cfg.artikel() == null) throw new ValidierungsException("Artikel muss gewählt sein.");
        if (cfg.auktionator() == null) throw new ValidierungsException("Auktionator muss gewählt sein.");
        if (cfg.bieter() == null || cfg.bieter().isEmpty()) {
            throw new ValidierungsException("Mindestens ein Bieter muss registriert sein.");
        }
        if (cfg.startPreis() <= 0) throw new ValidierungsException("Startpreis muss > 0 sein.");
        if (cfg.mindestPreis() < 0) throw new ValidierungsException("Mindestpreis darf nicht negativ sein.");
        if (cfg.startPreis() <= cfg.mindestPreis()) {
            throw new ValidierungsException("Startpreis muss größer als Mindestpreis sein.");
        }
        if (cfg.preisSchritt() <= 0) throw new ValidierungsException("Preisschritt muss > 0 sein.");

        Set<Benutzer> unique = new HashSet<>(cfg.bieter());
        if (unique.size() != cfg.bieter().size()) {
            throw new ValidierungsException("Bieterliste enthält Duplikate.");
        }
        if (cfg.bieter().contains(cfg.auktionator())) {
            throw new ValidierungsException("Auktionator darf nicht gleichzeitig Bieter sein.");
        }
    }

    public static void validateSimulationStart(List<Auktion> auktionen, int maxParallel) {
        if (maxParallel < 1) {
            throw new ValidierungsException("Maximale Parallelität muss mindestens 1 sein.");
        }
        if (auktionen == null || auktionen.isEmpty()) {
            throw new ValidierungsException("Es ist keine Auktion geplant.");
        }
    }
}