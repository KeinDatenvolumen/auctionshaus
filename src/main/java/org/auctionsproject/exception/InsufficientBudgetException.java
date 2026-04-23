package org.auctionsproject.exception;

/**
 * Wird geworfen, wenn ein Bieter nicht genug Budget hat.
 */
public class InsufficientBudgetException extends RuntimeException {
    public InsufficientBudgetException(String message) {
        super(message);
    }
}