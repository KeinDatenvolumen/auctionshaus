package org.auctionsproject.core;

import org.auctionsproject.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Einfache JSON-Persistenz ohne externe Bibliothek.
 * Speichert/liest Benutzer, Artikel, Auktionen und Einstellungen.
 */
public class PersistenceService {

    /**
     * Speichert den vollständigen Zustand in eine JSON-Datei.
     * @param haus Auktionshaus-Instanz.
     * @param file Zielpfad.
     * @throws IOException bei IO-Fehler.
     */
    public void save(Auktionshaus haus, Path file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"maxParallelAuktionen\": ").append(haus.getMaxParallelAuktionen()).append(",\n");

        // Benutzer
        sb.append("  \"benutzer\": [\n");
        List<Benutzer> benutzer = haus.getBenutzer();
        for (int i = 0; i < benutzer.size(); i++) {
            Benutzer b = benutzer.get(i);
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(esc(b.getName())).append("\",\n");
            sb.append("      \"bieterTyp\": \"").append(b.getBieterTyp().name()).append("\",\n");
            sb.append("      \"basis\": ").append(b.getBasisGebotsWahrscheinlichkeit()).append(",\n");
            sb.append("      \"interessen\": [");
            int c = 0;
            for (Kategorie k : b.getInteressen()) {
                if (c++ > 0) sb.append(", ");
                sb.append("\"").append(k.name()).append("\"");
            }
            sb.append("]\n");
            sb.append("    }").append(i < benutzer.size() - 1 ? "," : "").append("\n");
        }
        sb.append("  ],\n");

        // Artikel
        sb.append("  \"artikel\": [\n");
        List<Artikel> artikel = haus.getArtikel();
        for (int i = 0; i < artikel.size(); i++) {
            Artikel a = artikel.get(i);
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(esc(a.name())).append("\",\n");
            sb.append("      \"kategorie\": \"").append(a.kategorie().name()).append("\",\n");
            sb.append("      \"wert\": ").append(a.geschaetzterWert()).append("\n");
            sb.append("    }").append(i < artikel.size() - 1 ? "," : "").append("\n");
        }
        sb.append("  ],\n");

        // Auktionen (nur geplante Struktur)
        sb.append("  \"auktionen\": [\n");
        List<Auktion> auktionen = haus.getAuktionen();
        for (int i = 0; i < auktionen.size(); i++) {
            Auktion a = auktionen.get(i);
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(esc(a.getName())).append("\",\n");
            sb.append("      \"artikelName\": \"").append(esc(a.getArtikel().name())).append("\",\n");
            sb.append("      \"auktionatorName\": \"").append(esc(a.getAuktionator().getName())).append("\",\n");
            sb.append("      \"startPreis\": ").append(a.getStartPreis()).append(",\n");
            sb.append("      \"mindestPreis\": ").append(a.getMindestPreis()).append(",\n");
            sb.append("      \"preisSchritt\": ").append(a.getPreisSchritt()).append(",\n");
            sb.append("      \"bieterNamen\": [");
            List<Benutzer> bieter = a.getRegistrierteBieter();
            for (int j = 0; j < bieter.size(); j++) {
                if (j > 0) sb.append(", ");
                sb.append("\"").append(esc(bieter.get(j).getName())).append("\"");
            }
            sb.append("]\n");
            sb.append("    }").append(i < auktionen.size() - 1 ? "," : "").append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");

        Files.writeString(file, sb.toString());
    }

    /**
     * Lädt den Zustand aus JSON. Vorhandene Daten im Haus werden verworfen.
     * @param haus Auktionshaus.
     * @param file Quelldatei.
     * @throws IOException bei IO-Fehler.
     */
    public void load(Auktionshaus haus, Path file) throws IOException {
        String json = Files.readString(file);

        haus.resetAll();

        Integer max = intField(json, "maxParallelAuktionen");
        if (max != null) haus.setMaxParallelAuktionen(max);

        Map<String, Benutzer> benutzerByName = new HashMap<>();
        Map<String, Artikel> artikelByName = new HashMap<>();

        // Benutzer
        for (String block : arrayObjects(json, "benutzer")) {
            String name = strField(block, "name");
            String typ = strField(block, "bieterTyp");
            Double basis = dblField(block, "basis");
            Set<Kategorie> ints = enumArray(block, "interessen", Kategorie.class);

            Benutzer b = new Benutzer(name, BieterTyp.valueOf(typ), ints, basis == null ? 0.5 : basis);
            haus.addBenutzer(b);
            benutzerByName.put(name, b);
        }

        // Artikel
        for (String block : arrayObjects(json, "artikel")) {
            String name = strField(block, "name");
            String kat = strField(block, "kategorie");
            Double wert = dblField(block, "wert");

            Artikel a = new Artikel(name, Kategorie.valueOf(kat), wert == null ? 0.0 : wert);
            haus.addArtikel(a);
            artikelByName.put(name, a);
        }

        // Auktionen
        for (String block : arrayObjects(json, "auktionen")) {
            String name = strField(block, "name");
            String artikelName = strField(block, "artikelName");
            String auktionatorName = strField(block, "auktionatorName");
            Double start = dblField(block, "startPreis");
            Double mind = dblField(block, "mindestPreis");
            Double step = dblField(block, "preisSchritt");
            List<String> bieterNamen = strArray(block, "bieterNamen");

            Benutzer auktionator = benutzerByName.get(auktionatorName);
            Artikel artikel = artikelByName.get(artikelName);
            List<Benutzer> bieter = new ArrayList<>();
            for (String bn : bieterNamen) {
                Benutzer b = benutzerByName.get(bn);
                if (b != null) bieter.add(b);
            }

            if (auktionator != null && artikel != null && !bieter.isEmpty()) {
                AuktionKonfiguration cfg = new AuktionKonfiguration(
                        name, artikel, auktionator, bieter,
                        start == null ? 1000 : start,
                        mind == null ? 300 : mind,
                        step == null ? 100 : step
                );
                haus.addAuktion(new Auktion(cfg));
            }
        }
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static Integer intField(String src, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)").matcher(src);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    private static Double dblField(String src, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)").matcher(src);
        return m.find() ? Double.parseDouble(m.group(1)) : null;
    }

    private static String strField(String src, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"").matcher(src);
        return m.find() ? m.group(1).replace("\\\"", "\"").replace("\\\\", "\\") : null;
    }

    private static <E extends Enum<E>> Set<E> enumArray(String src, String key, Class<E> enumType) {
        Set<E> out = new HashSet<>();
        for (String s : strArray(src, key)) out.add(Enum.valueOf(enumType, s));
        return out;
    }

    private static List<String> strArray(String src, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL).matcher(src);
        List<String> out = new ArrayList<>();
        if (m.find()) {
            Matcher s = Pattern.compile("\"((?:\\\\.|[^\"])*)\"").matcher(m.group(1));
            while (s.find()) out.add(s.group(1).replace("\\\"", "\"").replace("\\\\", "\\"));
        }
        return out;
    }

    private static List<String> arrayObjects(String src, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)]\\s*(,|\\})", Pattern.DOTALL).matcher(src);
        if (!m.find()) return List.of();
        String body = m.group(1);

        List<String> objects = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < body.length(); i++) {
            char ch = body.charAt(i);
            if (ch == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(body.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }
}