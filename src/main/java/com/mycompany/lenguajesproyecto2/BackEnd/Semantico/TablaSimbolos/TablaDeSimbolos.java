package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabla de Símbolos para el lenguaje PromptZal.
 * Implementa el almacenamiento estructurado y el control estricto de ámbitos (Scope):
 * las variables locales de un agente pertenecen exclusivamente a dicho agente
 * y no son visibles ni accesibles desde otros agentes ni desde el ámbito global.
 */
public class TablaDeSimbolos {

    // Clave compuesta: "AMBITO:IDENTIFICADOR" para garantizar unicidad por ámbito
    private final Map<String, Simbolo> tabla;
    private final List<Simbolo> ordenDeDeclaracion;

    public TablaDeSimbolos() {
        this.tabla = new LinkedHashMap<>();
        this.ordenDeDeclaracion = new ArrayList<>();
    }

    private String generarClave(String identificador, String ambito) {
        String amb = (ambito != null && !ambito.trim().isEmpty()) ? ambito.trim() : Simbolo.AMBITO_GLOBAL;
        String id = identificador != null ? identificador.trim() : "";
        return amb + ":" + id;
    }

    /**
     * Inserta un nuevo símbolo en la tabla.
     * Retorna false si el símbolo ya existe dentro del MISMO ámbito.
     */
    public boolean insertar(Simbolo simbolo) {
        if (simbolo == null) {
            return false;
        }
        String clave = generarClave(simbolo.getIdentificador(), simbolo.getAmbito());
        if (tabla.containsKey(clave)) {
            return false; // Ya declarado en este mismo ámbito
        }
        tabla.put(clave, simbolo);
        ordenDeDeclaracion.add(simbolo);
        return true;
    }

    /**
     * Busca un símbolo en un ámbito específico.
     * Garantiza el aislamiento: no busca en otros agentes.
     */
    public Simbolo buscar(String identificador, String ambito) {
        String clave = generarClave(identificador, ambito);
        return tabla.get(clave);
    }

    /**
     * Comprueba si una variable existe dentro del ámbito local del agente especificado.
     * Si no existe en el agente, NO es accesible.
     */
    public boolean existeVariableEnAgente(String nombreVariable, String nombreAgente) {
        Simbolo s = buscar(nombreVariable, nombreAgente);
        return s != null && s.getTipo() == TipoSimbolo.VARIABLE;
    }

    /**
     * Busca la definición de una variable dentro de un agente específico.
     */
    public Simbolo buscarVariableEnAgente(String nombreVariable, String nombreAgente) {
        Simbolo s = buscar(nombreVariable, nombreAgente);
        return (s != null && s.getTipo() == TipoSimbolo.VARIABLE) ? s : null;
    }

    /**
     * Comprueba si un agente fue declarado en el ámbito global.
     */
    public boolean existeAgente(String nombreAgente) {
        Simbolo s = buscar(nombreAgente, Simbolo.AMBITO_GLOBAL);
        return s != null && s.getTipo() == TipoSimbolo.AGENTE;
    }

    /**
     * Busca el símbolo correspondiente a un agente global.
     */
    public Simbolo buscarAgente(String nombreAgente) {
        Simbolo s = buscar(nombreAgente, Simbolo.AMBITO_GLOBAL);
        return (s != null && s.getTipo() == TipoSimbolo.AGENTE) ? s : null;
    }

    /**
     * Retorna todas las variables declaradas dentro de un agente determinado.
     */
    public List<Simbolo> obtenerVariablesDeAgente(String nombreAgente) {
        List<Simbolo> vars = new ArrayList<>();
        for (Simbolo s : ordenDeDeclaracion) {
            if (s.getTipo() == TipoSimbolo.VARIABLE && s.getAmbito().equals(nombreAgente)) {
                vars.add(s);
            }
        }
        return Collections.unmodifiableList(vars);
    }

    /**
     * Retorna todos los agentes declarados en el programa.
     */
    public List<Simbolo> obtenerTodosLosAgentes() {
        List<Simbolo> agentes = new ArrayList<>();
        for (Simbolo s : ordenDeDeclaracion) {
            if (s.getTipo() == TipoSimbolo.AGENTE) {
                agentes.add(s);
            }
        }
        return Collections.unmodifiableList(agentes);
    }

    /**
     * Retorna la lista secuencial inmutable de todos los símbolos registrados.
     */
    public List<Simbolo> getTodosLosSimbolos() {
        return Collections.unmodifiableList(ordenDeDeclaracion);
    }

    /**
     * Reinicia la tabla de símbolos.
     */
    public void limpiar() {
        tabla.clear();
        ordenDeDeclaracion.clear();
    }
}
