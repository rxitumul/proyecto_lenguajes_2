package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo AST que representa la declaración completa de un Agente en PromptZal.
 * Cumple con el requerimiento estricto: almacena obligatoriamente nombre,
 * contexto, lista estructurada de variables declaradas y lista de comandos ejecutables.
 */
public class NodoAgente extends NodoAST {

    private final String nombre;
    private final String contexto;
    private final List<NodoVariable> variables;
    private final List<NodoComando> comandos;

    public NodoAgente(String nombre, String contexto, List<NodoVariable> variables,
                      List<NodoComando> comandos, int fila, int columna) {
        super(fila, columna);
        this.nombre = nombre;
        this.contexto = contexto;
        this.variables = variables != null ? new ArrayList<>(variables) : new ArrayList<>();
        this.comandos = comandos != null ? new ArrayList<>(comandos) : new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getContexto() {
        return contexto;
    }

    public List<NodoVariable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public List<NodoComando> getComandos() {
        return Collections.unmodifiableList(comandos);
    }

    /**
     * Busca si una variable fue declarada previamente en la sección de variables de este agente.
     */
    public boolean tieneVariable(String nombreVar) {
        if (nombreVar == null) {
            return false;
        }
        for (NodoVariable v : variables) {
            if (v.getNombre().equals(nombreVar)) {
                return true;
            }
        }
        for (NodoComando c : comandos) {
            if (nombreVar.equals(c.getVariableDestino())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "NodoAgente{" + "nombre='" + nombre + '\'' + ", contexto='" + contexto + '\'' +
                ", variables=" + variables.size() + ", comandos=" + comandos.size() + '}';
    }
}
