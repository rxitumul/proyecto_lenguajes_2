package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico;

/**
 * Excepción interna lanzada al detectar una discrepancia con la GLC oficial
 * para detener el análisis descendente recursivo de forma limpia y controlada.
 */
public class ErrorSintacticoException extends RuntimeException {

    private final ErrorSintactico error;

    public ErrorSintacticoException(ErrorSintactico error) {
        super(error.getMensaje());
        this.error = error;
    }

    public ErrorSintactico getError() {
        return error;
    }
}
