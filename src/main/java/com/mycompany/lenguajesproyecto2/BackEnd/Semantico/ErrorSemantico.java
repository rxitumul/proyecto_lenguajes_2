package com.mycompany.lenguajesproyecto2.BackEnd.Semantico;

/**
 * Representa un error semántico detectado durante el análisis del AST de PromptZal.
 * Incluye descripción detallada, identificador involucrado y ubicación exacta (línea y columna).
 */
public class ErrorSemantico {

    private final int fila;
    private final int columna;
    private final String identificador;
    private final String descripcion;

    public ErrorSemantico(int fila, int columna, String identificador, String descripcion) {
        this.fila = fila;
        this.columna = columna;
        this.identificador = identificador != null ? identificador : "";
        this.descripcion = descripcion != null ? descripcion : "";
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return String.format("[Línea: %d, Columna: %d] Error Semántico: %s (Identificador: '%s')",
                fila, columna, descripcion, identificador);
    }
}
