package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

public class RegistroDeTokens {
    private String lexemaLocal;
    private String tipo;
    private String descripcionLocal;
    private int filaLocal;
    private int columnaLocal;
    private String tokenLocal;

    public RegistroDeTokens(String lexema, String tipo, String descripcion, int linea, int columna, String token) {
        this.lexemaLocal = lexema;
        this.descripcionLocal = descripcion;
        this.filaLocal = linea;
        this.columnaLocal = columna;
        this.tokenLocal = token;
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
    public String getToken() {
        return tokenLocal;
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

}
