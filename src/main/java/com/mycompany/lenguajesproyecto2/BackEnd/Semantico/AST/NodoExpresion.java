package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo AST que representa una expresión compuesta por uno o varios términos unidos por '+'.
 */
public class NodoExpresion extends NodoAST {

    private final List<NodoTermino> terminos;

    public NodoExpresion(int fila, int columna) {
        super(fila, columna);
        this.terminos = new ArrayList<>();
    }

    public NodoExpresion(List<NodoTermino> terminos, int fila, int columna) {
        super(fila, columna);
        this.terminos = terminos != null ? new ArrayList<>(terminos) : new ArrayList<>();
    }

    public void agregarTermino(NodoTermino termino) {
        if (termino != null) {
            this.terminos.add(termino);
        }
    }

    public List<NodoTermino> getTerminos() {
        return Collections.unmodifiableList(terminos);
    }

    /**
     * Retorna todos los identificadores referenciados como variables en esta expresión.
     * Excluye expresamente las unidades numéricas (ej. 'palabras' en '250 palabras').
     */
    public List<NodoTermino> obtenerIdentificadoresReferenciados() {
        List<NodoTermino> ids = new ArrayList<>();
        for (NodoTermino t : terminos) {
            if (t.esIdentificador()) {
                ids.add(t);
            }
        }
        return ids;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terminos.size(); i++) {
            if (i > 0) {
                sb.append(" + ");
            }
            sb.append(terminos.get(i).toString());
        }
        return sb.toString();
    }
}
