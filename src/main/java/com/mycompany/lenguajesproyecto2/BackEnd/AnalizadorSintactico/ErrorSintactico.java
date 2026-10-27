package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.TokenEntidad;

/**
 * Representa un error sintáctico detectado durante el análisis descendente recursivo.
 * Almacena la fila y columna exactas del token infractor, junto con los detalles esperados y encontrados.
 */
public class ErrorSintactico {

    private final int fila;
    private final int columna;
    private final TokenEntidad tokenActual;
    private final String tipoEsperado;
    private final String mensaje;

    public ErrorSintactico(int fila, int columna, TokenEntidad tokenActual, String tipoEsperado, String mensaje) {
        this.fila = fila;
        this.columna = columna;
        this.tokenActual = tokenActual;
        this.tipoEsperado = tipoEsperado;
        this.mensaje = mensaje;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public TokenEntidad getTokenActual() {
        return tokenActual;
    }

    public String getTipoEsperado() {
        return tipoEsperado;
    }

    public String getTokenEncontrado() {
        return tokenActual != null ? tokenActual.getTipo() : "EOF";
    }

    public String getLexemaEncontrado() {
        return tokenActual != null ? tokenActual.getDescripcion() : "";
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String toString() {
        return String.format("[Fila: %d, Columna: %d] Error Sintáctico: %s", fila, columna, mensaje);
    }
}
