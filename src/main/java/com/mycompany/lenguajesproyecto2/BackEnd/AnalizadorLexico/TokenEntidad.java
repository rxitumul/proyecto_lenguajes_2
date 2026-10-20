package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico;

public class TokenEntidad {
    private String tipo;
    private String descripcion;
    private int columna;
    private int fila;

    // Constructor completo incluyendo fila y columna
    public TokenEntidad(String tipo, String descripcion, int fila, int columna) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getColumna() {
        return columna;
    }

    public int getFila() {
        return fila;
    }

    public String getLexema() {
        return descripcion;
    }

    @Override
    public String toString() {
        return String.format("<%s, \"%s\", Fila: %d, Col: %d>", tipo, descripcion, fila, columna);
    }
}
