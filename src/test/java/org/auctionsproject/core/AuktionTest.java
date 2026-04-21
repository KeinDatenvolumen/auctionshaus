package org.auctionsproject.core;

import org.auctionsproject.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

/**
 * Unit-Tests für kritische Auktionslogik.
 */
public class AuktionTest {

    @Test
    void artikelWirdNichtDoppeltVerkauft() throws InterruptedException {
        Benutzer auktionator = new Benutzer("A", BieterTyp.ZUFAELLIG, Set.of(Kategorie.KUNST), 0.2);
        Benutzer b1 = new Benutzer("B1", BieterTyp.AGGRESSIV, Set.of(Kategorie.KUNST), 1.0);
        Benutzer b2 = new Benutzer("B2", BieterTyp.AGGRESSIV, Set.of(Kategorie.KUNST), 1.0);
        Artikel artikel = new Artikel("Bild", Kategorie.KUNST, 1000);

        Auktion a = new Auktion(new AuktionKonfiguration("T1", artikel, auktionator, List.of(b1, b2), 1000, 100, 100));
        a.starten();

        Thread t1 = new Thread(() -> a.kaufversuch(b1, a.getAktuellerPreis()));
        Thread t2 = new Thread(() -> a.kaufversuch(b2, a.getAktuellerPreis()));
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        Assertions.assertEquals(AuktionsStatus.VERKAUFT, a.getStatus());
        Assertions.assertNotNull(a.getGewinner());
    }

    @Test
    void auktionWirdBeiMindestpreisUnterschreitungZurueckgezogen() {
        Benutzer auktionator = new Benutzer("A", BieterTyp.ZUFAELLIG, Set.of(Kategorie.KUNST), 0.2);
        Benutzer b1 = new Benutzer("B1", BieterTyp.KONSERVATIV, Set.of(Kategorie.ELEKTRONIK), 0.0);
        Artikel artikel = new Artikel("Bild", Kategorie.KUNST, 1000);

        Auktion a = new Auktion(new AuktionKonfiguration("T2", artikel, auktionator, List.of(b1), 500, 300, 250));
        a.starten();
        a.preisSenken(); // 250 -> unter 300

        Assertions.assertEquals(AuktionsStatus.ZURUECKGEZOGEN, a.getStatus());
    }
}