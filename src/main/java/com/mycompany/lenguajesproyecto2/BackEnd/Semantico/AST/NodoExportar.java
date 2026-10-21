package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo AST que representa la exportación final de variables de los agentes ejecutados.
 * Sintaxis: EXPORTAR id (, id)*
 */
public class NodoExportar extends NodoAST {

    public static class ItemExportado {
        private final String nombreVariable;
        private final int fila;
        private final int columna;

        public ItemExportado(String nombreVariable, int fila, int columna) {
            this.nombreVariable = nombreVariable;
            this.fila = fila;
            this.columna = columna;
        }

        public String getNombreVariable() {
            return nombreVariable;
        }

        public int getFila() {
            return fila;
        }

        public int getColumna() {
            return columna;
        }

        @Override
        public String toString() {
            return nombreVariable;
        }
    }

    private final List<ItemExportado> variablesExportadas;

    public NodoExportar(int fila, int columna) {
        super(fila, columna);
        this.variablesExportadas = new ArrayList<>();
    }

    public void agregarVariable(String variable, int fila, int columna) {
        if (variable != null) {
            this.variablesExportadas.add(new ItemExportado(variable, fila, columna));
        }
    }

    public List<ItemExportado> getVariablesExportadas() {
        return Collections.unmodifiableList(variablesExportadas);
    }

    @Override
    public String toString() {
        return "EXPORTAR " + String.join(", ", variablesExportadas.stream().map(ItemExportado::getNombreVariable).toList());
    }
}
