package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo AST que representa un comando ejecutable de IA dentro de un agente.
 * Sintaxis: COMANDO_IA expresion [conectores] -> variableDestino
 */
public class NodoComando extends NodoAST {

    private final String tipoComando;
    private final NodoExpresion expresion;
    private final List<NodoConector> conectores;
    private final String variableDestino;

    public NodoComando(String tipoComando, NodoExpresion expresion, List<NodoConector> conectores,
                       String variableDestino, int fila, int columna) {
        super(fila, columna);
        this.tipoComando = tipoComando;
        this.expresion = expresion;
        this.conectores = conectores != null ? new ArrayList<>(conectores) : new ArrayList<>();
        this.variableDestino = variableDestino;
    }

    public String getTipoComando() {
        return tipoComando;
    }

    public NodoExpresion getExpresion() {
        return expresion;
    }

    public List<NodoConector> getConectores() {
        return Collections.unmodifiableList(conectores);
    }

    public String getVariableDestino() {
        return variableDestino;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tipoComando).append(" ").append(expresion != null ? expresion.toString() : "");
        for (NodoConector c : conectores) {
            sb.append(" ").append(c.toString());
        }
        sb.append(" -> ").append(variableDestino);
        return sb.toString();
    }
}
