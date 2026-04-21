package org.auctionsproject.exceptions;

/**
 * Ausnahme für Validierungsfehler bei Eingaben/Konfiguration.
 */
public class ValidierungsException extends RuntimeException {
    /**
     * Konstruktor.
     * @param message Fehlertext.
     */
    public ValidierungsException(String message) {
        super(message);
    }
}