package com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas;

public class ErrorLexico {
    private String lexemaLocal;
    private String descripcionLocal;
    private int filaLocal;
    private int columnaLocal;

    public ErrorLexico(String lexema, String descripcion, int fila, int columna) {
        this.lexemaLocal = lexema;
        this.descripcionLocal = descripcion;
        this.filaLocal = fila;
        this.columnaLocal = columna;
    }

    public String getLexema() {
        return lexemaLocal;
    }

    public String getDescripcion() {
        return descripcionLocal;
    }

    public int getFila() {
        return filaLocal;
    }

    public int getColumna() {
        return columnaLocal;
    }

    @Override
    public String toString() {
        return "ErrorLexico [Lexema: '" + lexemaLocal + "', Descripcion: '" + descripcionLocal + "', Fila: " + filaLocal + ", Columna: " + columnaLocal + "]";
    }
}
