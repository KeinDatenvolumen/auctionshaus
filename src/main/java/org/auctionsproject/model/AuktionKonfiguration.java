package org.auctionsproject.model;

import java.util.List;
import java.util.Objects;

/**
 * Konfigurationsobjekt für eine geplante Auktion.
 * @param name Name der Auktion.
 * @param artikel Artikel.
 * @param auktionator Auktionator.
 * @param bieter Registrierte Bieter.
 * @param startPreis Startpreis.
 * @param mindestPreis Mindestpreis.
 * @param preisSchritt Schrittweite.
 */
public record AuktionKonfiguration(
        String name,
        Artikel artikel,
        Benutzer auktionator,
        List<Benutzer> bieter,
        double startPreis,
        double mindestPreis,
        double preisSchritt
) {
    public AuktionKonfiguration {
        Objects.requireNonNull(name);
        Objects.requireNonNull(artikel);
        Objects.requireNonNull(auktionator);
        Objects.requireNonNull(bieter);
    }
}