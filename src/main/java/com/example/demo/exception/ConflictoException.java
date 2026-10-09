package com.example.demo.exception;

/**
 * Se lanza cuando la operación pedida choca con el estado actual de los datos
 * (por ejemplo, borrar una lista que todavía tiene favoritos). Se traduce a 409.
 */

public class ConflictoException extends RuntimeException {
    public ConflictoException (String mensaje){
        super(mensaje);
    }
}