package com.mycompany.lenguajesproyecto2.BackEnd.Semantico;

import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoAgente;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoComando;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoConector;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoDirectiva;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoEjecucion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExpresion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExportar;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoPrograma;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoTermino;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoVariable;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.Simbolo;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.TablaDeSimbolos;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.TipoSimbolo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Analizador Semántico para el lenguaje PromptZal.
 * Realiza las validaciones de tipos, ámbitos, unicidad y dependencias sobre el AST.
 * Recopila todos los errores semánticos sin detener abruptamente la compilación.
 */
public class AnalizadorSemantico {

    private final TablaDeSimbolos tablaDeSimbolos;
    private final List<ErrorSemantico> errores;
    private final Set<String> agentesEjecutados;

    public AnalizadorSemantico() {
        this.tablaDeSimbolos = new TablaDeSimbolos();
        this.errores = new ArrayList<>();
        this.agentesEjecutados = new LinkedHashSet<>();
    }

    /**
     * Ejecuta el análisis semántico completo sobre el árbol de sintaxis abstracta.
     *
     * @param programa Nodo raíz del AST.
     * @return true si el programa es semánticamente válido (sin errores), false en caso contrario.
     */
    public boolean analizar(NodoPrograma programa) {
        tablaDeSimbolos.limpiar();
        errores.clear();
        agentesEjecutados.clear();

        if (programa == null) {
            agregarError(1, 1, "PROGRAMA", "El AST del programa no fue generado o es nulo.");
            return false;
        }

        // 1. Registrar y validar Directivas Globales
        analizarDirectivas(programa.getDirectivas());

        // 2. Registrar Agentes y Validar Unicidad de nombres de Agentes
        registrarDeclaracionesDeAgentes(programa.getAgentes());

        // 3. Validar cuerpo interno de cada Agente (Ámbito local y Variables declaradas antes de su uso)
        analizarCuerpoDeAgentes(programa.getAgentes());

        // 4. Validar sentencias EJECUTAR (existencia previa de los agentes)
        analizarEjecuciones(programa.getEjecuciones());

        // 5. Validar sentencia EXPORTAR (variables válidas de agentes previamente ejecutados)
        analizarExportaciones(programa.getExportar(), programa.getAgentes());

        return errores.isEmpty();
    }

    /**
     * 1. Registra las directivas en el ámbito global de la tabla de símbolos.
     */
    private void analizarDirectivas(List<NodoDirectiva> directivas) {
        if (directivas == null) return;
        for (NodoDirectiva d : directivas) {
            Simbolo s = new Simbolo(d.getNombre(), TipoSimbolo.DIRECTIVA, Simbolo.AMBITO_GLOBAL,
                    d.getFila(), d.getColumna(), d.getValor());
            tablaDeSimbolos.insertar(s);
        }
    }

    /**
     * 2. Valida la unicidad de los agentes y los registra en la tabla de símbolos en el ámbito global.
     */
    private void registrarDeclaracionesDeAgentes(List<NodoAgente> agentes) {
        if (agentes == null) return;
        for (NodoAgente a : agentes) {
            String nombre = a.getNombre();
            if (tablaDeSimbolos.existeAgente(nombre)) {
                Simbolo previo = tablaDeSimbolos.buscarAgente(nombre);
                int lineaPrevia = previo != null ? previo.getLinea() : a.getFila();
                agregarError(a.getFila(), a.getColumna(), nombre,
                        String.format("El agente '%s' ya ha sido declarado previamente en la línea %d.", nombre, lineaPrevia));
            } else {
                Simbolo s = new Simbolo(nombre, TipoSimbolo.AGENTE, Simbolo.AMBITO_GLOBAL,
                        a.getFila(), a.getColumna(), a.getContexto());
                tablaDeSimbolos.insertar(s);
            }
        }
    }

    /**
     * 3. Valida el cuerpo de cada agente:
     *    - Control de ámbito local estricto (aislamiento entre agentes).
     *    - Declaración previa: toda variable referenciada en una expresión debe haber sido declarada antes.
     *    - Registro de variables y comandos destino.
     */
    private void analizarCuerpoDeAgentes(List<NodoAgente> agentes) {
        if (agentes == null) return;
        for (NodoAgente agente : agentes) {
            String ambitoLocal = agente.getNombre();

            // A) Declaración de variables locales
            for (NodoVariable variable : agente.getVariables()) {
                String nombreVar = variable.getNombre();

                // Validar que los identificadores usados en la expresión existan ANTES en este agente
                validarIdentificadoresEnExpresion(variable.getExpresion(), ambitoLocal);

                // Validar si ya fue declarada previamente en este mismo agente
                if (tablaDeSimbolos.existeVariableEnAgente(nombreVar, ambitoLocal)) {
                    Simbolo prev = tablaDeSimbolos.buscarVariableEnAgente(nombreVar, ambitoLocal);
                    int lineaPrev = prev != null ? prev.getLinea() : variable.getFila();
                    agregarError(variable.getFila(), variable.getColumna(), nombreVar,
                            String.format("La variable '%s' ya fue declarada previamente en el agente '%s' (línea %d).",
                                    nombreVar, ambitoLocal, lineaPrev));
                } else {
                    String valorDesc = variable.getExpresion() != null ? variable.getExpresion().toString() : "";
                    Simbolo simVar = new Simbolo(nombreVar, TipoSimbolo.VARIABLE, ambitoLocal,
                            variable.getFila(), variable.getColumna(), valorDesc);
                    tablaDeSimbolos.insertar(simVar);
                }
            }

            // B) Comandos de IA dentro del agente
            for (NodoComando cmd : agente.getComandos()) {
                String tipoCmd = cmd.getTipoComando() != null ? cmd.getTipoComando().trim().toUpperCase() : "";

                // Excepción semántica obligatoria: los argumentos directos de GENERAR no deben ser validados como variables
                if (!"GENERAR".equals(tipoCmd)) {
                    validarIdentificadoresEnExpresion(cmd.getExpresion(), ambitoLocal);
                }

                // Validar identificadores en cada cláusula conectora (ej. SOBRE entrada, COMO "json")
                for (NodoConector conector : cmd.getConectores()) {
                    validarIdentificadoresEnExpresion(conector.getExpresion(), ambitoLocal);
                }

                // La variable destino del comando (-> variableDestino) se registra como variable disponible
                String varDestino = cmd.getVariableDestino();
                if (varDestino != null && !varDestino.trim().isEmpty()) {
                    if (!tablaDeSimbolos.existeVariableEnAgente(varDestino, ambitoLocal)) {
                        Simbolo simDestino = new Simbolo(varDestino, TipoSimbolo.VARIABLE, ambitoLocal,
                                cmd.getFila(), cmd.getColumna(), "Resultado de " + cmd.getTipoComando());
                        tablaDeSimbolos.insertar(simDestino);
                    }
                }
            }
        }
    }

    /**
     * Valida que todos los identificadores (variables) referenciados en una expresión
     * hayan sido declarados antes dentro del agente actual (ámbito local).
     *
     * Manejo de excepciones semánticas:
     * - Las unidades numéricas (ej. 'palabras' en '100 palabras') no son de tipo ID,
     *   por lo que nunca entran aquí como variables no declaradas.
     */
    private void validarIdentificadoresEnExpresion(NodoExpresion expresion, String ambitoLocal) {
        if (expresion == null) return;
        List<NodoTermino> ids = expresion.obtenerIdentificadoresReferenciados();

        for (NodoTermino terminoId : ids) {
            String nombreId = terminoId.getValor();

            // Comprobar si existe en el ámbito local del agente
            if (!tablaDeSimbolos.existeVariableEnAgente(nombreId, ambitoLocal)) {
                // Verificar si existe en otro agente para dar un mensaje explicativo de ámbito local
                boolean existeEnOtroAgente = false;
                String otroAgente = "";
                for (Simbolo s : tablaDeSimbolos.getTodosLosSimbolos()) {
                    if (s.getTipo() == TipoSimbolo.VARIABLE && s.getIdentificador().equals(nombreId)) {
                        existeEnOtroAgente = true;
                        otroAgente = s.getAmbito();
                        break;
                    }
                }

                if (existeEnOtroAgente) {
                    agregarError(terminoId.getFila(), terminoId.getColumna(), nombreId,
                            String.format("La variable '%s' pertenece al agente '%s' y no es accesible desde '%s' (violación de ámbito local).",
                                    nombreId, otroAgente, ambitoLocal));
                } else {
                    agregarError(terminoId.getFila(), terminoId.getColumna(), nombreId,
                            String.format("La variable '%s' no ha sido declarada previamente dentro del agente '%s'.",
                                    nombreId, ambitoLocal));
                }
            }
        }
    }

    /**
     * 4. Valida las sentencias EJECUTAR.
     * Solo pueden referenciar agentes que existan previamente en el programa.
     */
    private void analizarEjecuciones(List<NodoEjecucion> ejecuciones) {
        if (ejecuciones == null) return;
        for (NodoEjecucion ejec : ejecuciones) {
            String nombreAgente = ejec.getNombreAgente();
            if (!tablaDeSimbolos.existeAgente(nombreAgente)) {
                agregarError(ejec.getFila(), ejec.getColumna(), nombreAgente,
                        String.format("El agente '%s' invocado en EJECUTAR no ha sido declarado en el programa.", nombreAgente));
            } else {
                agentesEjecutados.add(nombreAgente);
            }
        }
    }

    /**
     * 5. Valida la sentencia EXPORTAR.
     * Solo puede hacer referencia a variables válidas que existan en algún agente previamente ejecutado.
     */
    private void analizarExportaciones(NodoExportar exportar, List<NodoAgente> agentes) {
        if (exportar == null) return;

        for (NodoExportar.ItemExportado item : exportar.getVariablesExportadas()) {
            String nombreVariable = item.getNombreVariable();
            boolean encontradaEnEjecutado = false;

            // Verificar si la variable pertenece a alguno de los agentes ejecutados
            for (String nombreAgenteEjecutado : agentesEjecutados) {
                if (tablaDeSimbolos.existeVariableEnAgente(nombreVariable, nombreAgenteEjecutado)) {
                    encontradaEnEjecutado = true;
                    break;
                }
            }

            if (!encontradaEnEjecutado) {
                // Verificar si existe en algún agente que NO fue ejecutado
                boolean existeEnNoEjecutado = false;
                String agenteNoEjecutado = "";
                for (NodoAgente a : agentes) {
                    if (!agentesEjecutados.contains(a.getNombre())) {
                        if (tablaDeSimbolos.existeVariableEnAgente(nombreVariable, a.getNombre())) {
                            existeEnNoEjecutado = true;
                            agenteNoEjecutado = a.getNombre();
                            break;
                        }
                    }
                }

                if (existeEnNoEjecutado) {
                    agregarError(item.getFila(), item.getColumna(), nombreVariable,
                            String.format("La variable '%s' no puede ser exportada porque su agente ('%s') no fue ejecutado con EJECUTAR.",
                                    nombreVariable, agenteNoEjecutado));
                } else {
                    agregarError(item.getFila(), item.getColumna(), nombreVariable,
                            String.format("La variable '%s' no existe en ningún agente previamente ejecutado.", nombreVariable));
                }
            }
        }
    }

    private void agregarError(int fila, int columna, String identificador, String descripcion) {
        errores.add(new ErrorSemantico(fila, columna, identificador, descripcion));
    }

    public List<ErrorSemantico> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public TablaDeSimbolos getTablaDeSimbolos() {
        return tablaDeSimbolos;
    }

    public Set<String> getAgentesEjecutados() {
        return Collections.unmodifiableSet(agentesEjecutados);
    }
}
