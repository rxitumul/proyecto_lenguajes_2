package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

/**
 * Clase base para todos los nodos del Árbol de Sintaxis Abstracta (AST) de PromptZal.
 */
public abstract class NodoAST {

    protected final int fila;
    protected final int columna;

    public NodoAST(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
