package org.auctionsproject.exception;

/**
 * Wird geworfen, wenn ein Bieter nicht genug Budget hat.
 */
public class InsufficientBudgetException extends RuntimeException {
    /**
     * Erstellt die Exception mit Nachricht.
     *
     * @param message Fehlerbeschreibung.
     */
    public InsufficientBudgetException(String message) {
        super(message);
    }
}
