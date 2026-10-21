package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

/**
 * Nodo AST que representa una directiva global (ej. @modelo "gpt-4", @rol "analista").
 */
public class NodoDirectiva extends NodoAST {

    private final String nombre;
    private final String valor;

    public NodoDirectiva(String nombre, String valor, int fila, int columna) {
        super(fila, columna);
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "NodoDirectiva{" + nombre + " = " + valor + "}";
    }
}
