package org.auctionsproject.model;

import java.time.LocalDateTime;

/**
 * Einzelnes Ereignis im Auktionsverlauf.
 * @param timestamp Zeitstempel.
 * @param auktionName Auktionsname.
 * @param nachricht Nachricht.
 */
public record AuktionsEvent(LocalDateTime timestamp, String auktionName, String nachricht) {
}