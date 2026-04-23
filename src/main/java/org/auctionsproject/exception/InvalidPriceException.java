package org.auctionsproject.exception;

/**
 * Wird geworfen, wenn Start-/Mindestpreis ungültig sind.
 */
public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String message) {
        super(message);
    }
}