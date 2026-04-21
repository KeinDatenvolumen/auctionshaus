package org.auctionsproject.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Benutzer der Plattform.
 * Kann als Auktionator oder Bieter auftreten.
 */
public class Benutzer {
    private final String name;
    private final BieterTyp bieterTyp;
    private final Set<Kategorie> interessen;
    private final double basisGebotsWahrscheinlichkeit;

    /**
     * Konstruktor.
     * @param name Anzeigename.
     * @param bieterTyp Profiltyp.
     * @param interessen Interessenkategorien.
     * @param basisGebotsWahrscheinlichkeit Basiswahrscheinlichkeit [0..1].
     */
    public Benutzer(String name,
                    BieterTyp bieterTyp,
                    Set<Kategorie> interessen,
                    double basisGebotsWahrscheinlichkeit) {
        this.name = Objects.requireNonNull(name);
        this.bieterTyp = Objects.requireNonNull(bieterTyp);
        this.interessen = new HashSet<>(Objects.requireNonNull(interessen));
        this.basisGebotsWahrscheinlichkeit = Math.max(0, Math.min(1, basisGebotsWahrscheinlichkeit));
    }

    public String getName() { return name; }
    public BieterTyp getBieterTyp() { return bieterTyp; }
    public Set<Kategorie> getInteressen() { return Collections.unmodifiableSet(interessen); }
    public double getBasisGebotsWahrscheinlichkeit() { return basisGebotsWahrscheinlichkeit; }

    /**
     * Prüft, ob Kategorie in den Interessen liegt.
     * @param k Kategorie.
     * @return true, wenn interessiert.
     */
    public boolean interessiertAn(Kategorie k) {
        return interessen.contains(k);
    }

    @Override
    public String toString() {
        return name + " [" + bieterTyp + "]";
    }
}