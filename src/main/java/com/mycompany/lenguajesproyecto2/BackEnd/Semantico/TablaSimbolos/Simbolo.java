package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos;

import java.util.Objects;

/**
 * Representa una entrada individual dentro de la Tabla de Símbolos.
 * Almacena el identificador, tipo, ámbito exacto ("GLOBAL" o nombre del agente)
 * y la ubicación (número de línea y columna) donde fue declarado.
 */
public class Simbolo {

    public static final String AMBITO_GLOBAL = "GLOBAL";

    private final String identificador;
    private final TipoSimbolo tipo;
    private final String ambito;
    private final int linea;
    private final int columna;
    private String valorInicial;

    public Simbolo(String identificador, TipoSimbolo tipo, String ambito, int linea, int columna) {
        this(identificador, tipo, ambito, linea, columna, null);
    }

    public Simbolo(String identificador, TipoSimbolo tipo, String ambito, int linea, int columna, String valorInicial) {
        this.identificador = identificador;
        this.tipo = tipo;
        this.ambito = (ambito != null && !ambito.trim().isEmpty()) ? ambito : AMBITO_GLOBAL;
        this.linea = linea;
        this.columna = columna;
        this.valorInicial = valorInicial;
    }

    public String getIdentificador() {
        return identificador;
    }

    public TipoSimbolo getTipo() {
        return tipo;
    }

    public String getAmbito() {
        return ambito;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public String getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(String valorInicial) {
        this.valorInicial = valorInicial;
    }

    public boolean esGlobal() {
        return AMBITO_GLOBAL.equalsIgnoreCase(ambito);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Simbolo simbolo = (Simbolo) o;
        return Objects.equals(identificador, simbolo.identificador) &&
               Objects.equals(ambito, simbolo.ambito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador, ambito);
    }

    @Override
    public String toString() {
        return String.format("Simbolo[ID='%s', Tipo=%s, Ambito='%s', Linea=%d, Col=%d]",
                identificador, tipo, ambito, linea, columna);
    }
}
