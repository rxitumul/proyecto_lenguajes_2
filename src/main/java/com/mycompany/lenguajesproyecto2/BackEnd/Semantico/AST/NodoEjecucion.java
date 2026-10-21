package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

/**
 * Nodo AST que representa una invocación de ejecución de un agente.
 * Sintaxis: EJECUTAR id
 */
public class NodoEjecucion extends NodoAST {

    private final String nombreAgente;

    public NodoEjecucion(String nombreAgente, int fila, int columna) {
        super(fila, columna);
        this.nombreAgente = nombreAgente;
    }

    public String getNombreAgente() {
        return nombreAgente;
    }

    @Override
    public String toString() {
        return "EJECUTAR " + nombreAgente;
    }
}
