package com.Alexander.eventflow.exception;

public class InsufficientTicketsException extends RuntimeException {

    public InsufficientTicketsException(int requested, int available) {
        super("No hay suficientes tickets. Solicitados: " + requested + ", disponibles: " + available);
    }
}