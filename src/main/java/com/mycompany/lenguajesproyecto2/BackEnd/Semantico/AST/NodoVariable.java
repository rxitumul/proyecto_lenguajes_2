package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

/**
 * Nodo AST que representa una declaración de variable dentro de un agente.
 * Sintaxis: variable id = expresion
 */
public class NodoVariable extends NodoAST {

    private final String nombre;
    private final NodoExpresion expresion;

    public NodoVariable(String nombre, NodoExpresion expresion, int fila, int columna) {
        super(fila, columna);
        this.nombre = nombre;
        this.expresion = expresion;
    }

    public String getNombre() {
        return nombre;
    }

    public NodoExpresion getExpresion() {
        return expresion;
    }

    @Override
    public String toString() {
        return "variable " + nombre + " = " + (expresion != null ? expresion.toString() : "");
    }
}
