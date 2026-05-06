package org.auctionsproject.exception;

/**
 * Wird geworfen, wenn Start-/Mindestpreis ungültig sind.
 */
public class InvalidPriceException extends RuntimeException {
    /**
     * Erstellt die Exception mit Nachricht.
     *
     * @param message Fehlerbeschreibung.
     */
    public InvalidPriceException(String message) {
        super(message);
    }
}
