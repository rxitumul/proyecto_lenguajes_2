package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo raíz del Árbol de Sintaxis Abstracta (AST) para un programa completo en PromptZal.
 * Contiene la jerarquía estructurada de Directivas, Agentes, Ejecuciones y Exportaciones.
 */
public class NodoPrograma extends NodoAST {

    private final List<NodoDirectiva> directivas;
    private final List<NodoAgente> agentes;
    private final List<NodoEjecucion> ejecuciones;
    private NodoExportar exportar;

    public NodoPrograma(int fila, int columna) {
        super(fila, columna);
        this.directivas = new ArrayList<>();
        this.agentes = new ArrayList<>();
        this.ejecuciones = new ArrayList<>();
        this.exportar = null;
    }

    public void agregarDirectiva(NodoDirectiva directiva) {
        if (directiva != null) {
            this.directivas.add(directiva);
        }
    }

    public void agregarAgente(NodoAgente agente) {
        if (agente != null) {
            this.agentes.add(agente);
        }
    }

    public void agregarEjecucion(NodoEjecucion ejecucion) {
        if (ejecucion != null) {
            this.ejecuciones.add(ejecucion);
        }
    }

    public void setExportar(NodoExportar exportar) {
        this.exportar = exportar;
    }

    public List<NodoDirectiva> getDirectivas() {
        return Collections.unmodifiableList(directivas);
    }

    public List<NodoAgente> getAgentes() {
        return Collections.unmodifiableList(agentes);
    }

    public List<NodoEjecucion> getEjecuciones() {
        return Collections.unmodifiableList(ejecuciones);
    }

    public NodoExportar getExportar() {
        return exportar;
    }

    public NodoAgente buscarAgente(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (NodoAgente a : agentes) {
            if (a.getNombre().equals(nombre)) {
                return a;
            }
        }
        return null;
    }
}
