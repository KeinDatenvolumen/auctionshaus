package org.auctionsproject.exceptions;

/**
 * Fachliche Laufzeitausnahme für Auktionsfehler.
 */
public class AuktionException extends RuntimeException {
    /**
     * Konstruktor mit Fehlermeldung.
     * @param message Fehlertext.
     */
    public AuktionException(String message) {
        super(message);
    }
}