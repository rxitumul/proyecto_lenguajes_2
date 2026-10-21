package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

/**
 * Nodo AST que representa una cláusula conectora dentro de un comando de IA (ej. SOBRE entrada, COMO "json").
 */
public class NodoConector extends NodoAST {

    private final String conector;
    private final NodoExpresion expresion;

    public NodoConector(String conector, NodoExpresion expresion, int fila, int columna) {
        super(fila, columna);
        this.conector = conector;
        this.expresion = expresion;
    }

    public String getConector() {
        return conector;
    }

    public NodoExpresion getExpresion() {
        return expresion;
    }

    @Override
    public String toString() {
        return conector + " " + (expresion != null ? expresion.toString() : "");
    }
}
