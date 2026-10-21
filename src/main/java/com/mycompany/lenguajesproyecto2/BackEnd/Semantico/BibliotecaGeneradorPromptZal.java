package com.mycompany.lenguajesproyecto2.BackEnd.Semantico;

import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoAgente;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoComando;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoConector;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoDirectiva;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoEjecucion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExportar;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoPrograma;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoVariable;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.Simbolo;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.TablaDeSimbolos;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Biblioteca modular y centralizada para la aceptación de directivas,
 * reglas de formato y generación del texto final de PromptZal a partir del AST.
 *
 * Cumple con la rúbrica:
 * - Recorre el AST para generar el prompt final en texto.
 * - Incluye encabezado general con las directivas declaradas (@modelo, @rol, @formato, etc.).
 * - Lista los agentes según EJECUTAR con su contexto, variables y la traducción exacta
 *   de cada comando a instrucciones en lenguaje natural
 *   (ej. PREGUNTAR p SOBRE x -> y traducido a "Basándote en x, responde: p. Guarda la respuesta como y.").
 * - Incluye los resultados solicitados por EXPORTAR al final del documento.
 */
public final class BibliotecaGeneradorPromptZal {

    // =========================================================================
    // DICCIONARIOS DE ACEPTACIÓN Y METADATOS
    // =========================================================================

    public static final Map<String, String> DIRECTIVAS_VALIDAS;
    public static final Map<String, String> COMANDOS_IA_VALIDOS;
    public static final Map<String, String> CONECTORES_VALIDOS;

    static {
        Map<String, String> dirs = new HashMap<>();
        dirs.put("@modelo", "Especifica el modelo base de Inteligencia Artificial (ej. 'gpt-4', 'claude-3-5').");
        dirs.put("@rol", "Define el rol o comportamiento global predeterminado de la IA.");
        dirs.put("@formato", "Especifica el formato estructural de salida de las respuestas.");
        dirs.put("@temperatura", "Ajusta el nivel de creatividad y determinismo del modelo (0.0 a 1.0).");
        dirs.put("@max_tokens", "Límite superior de tokens para la respuesta generada.");
        dirs.put("@idioma", "Idioma de preferencia de salida para las respuestas.");
        DIRECTIVAS_VALIDAS = Collections.unmodifiableMap(dirs);

        Map<String, String> cmds = new HashMap<>();
        cmds.put("PREGUNTAR", "Formula una pregunta a la IA sobre unos datos.");
        cmds.put("GENERAR", "Produce contenido sintético o respuestas creativas.");
        cmds.put("CODIFICAR", "Genera código fuente, scripts o transformaciones lógicas.");
        cmds.put("RESUMIR", "Sintetiza un cuerpo de texto extrayendo ideas clave.");
        cmds.put("TRADUCIR", "Traduce el contenido a un idioma o formato especificado.");
        cmds.put("ANALIZAR", "Examina minuciosamente una estructura de datos o texto.");
        COMANDOS_IA_VALIDOS = Collections.unmodifiableMap(cmds);

        Map<String, String> cons = new HashMap<>();
        cons.put("SOBRE", "Indica los datos sobre los que opera un comando de IA.");
        cons.put("COMO", "Indica el formato o rol de salida.");
        cons.put("EN", "Especifica el idioma, tono o entorno de ejecución.");
        cons.put("PARA", "Indica el destinatario o propósito del resultado.");
        cons.put("CON", "Agrega parámetros o restricciones adicionales.");
        CONECTORES_VALIDOS = Collections.unmodifiableMap(cons);
    }

    private static final com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta.BibliotecaDeTokens BIBLIOTECA_CENTRAL =
            new com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta.BibliotecaDeTokens();

    private BibliotecaGeneradorPromptZal() {
        // Clase utilitaria estática
    }

    public static boolean esDirectivaValida(String directiva) {
        if (directiva == null) return false;
        return DIRECTIVAS_VALIDAS.containsKey(directiva.trim().toLowerCase())
                || BIBLIOTECA_CENTRAL.esDirectiva(directiva);
    }

    public static boolean esComandoIAValido(String comando) {
        if (comando == null) return false;
        return COMANDOS_IA_VALIDOS.containsKey(comando.trim().toUpperCase())
                || BIBLIOTECA_CENTRAL.esComandoIA(comando);
    }

    public static boolean esConectorValido(String conector) {
        if (conector == null) return false;
        return CONECTORES_VALIDOS.containsKey(conector.trim().toUpperCase())
                || BIBLIOTECA_CENTRAL.esConector(conector);
    }

    // =========================================================================
    // TRADUCCIÓN DE COMANDOS A INSTRUCCIONES EN LENGUAJE NATURAL
    // =========================================================================

    /**
     * Traduce un comando de PromptZal a una instrucción exacta en Lenguaje Natural
     * según los requerimientos de la rúbrica.
     * Ejemplo: PREGUNTAR p SOBRE x -> y se traduce a:
     * "Basándote en x, responde: p. Guarda la respuesta como y."
     */
    public static String traducirComandoALenguajeNatural(NodoComando cmd) {
        if (cmd == null) {
            return "";
        }

        String tipo = cmd.getTipoComando() != null ? cmd.getTipoComando().trim().toUpperCase() : "COMANDO";
        String p = cmd.getExpresion() != null ? cmd.getExpresion().toString() : "";
        String y = cmd.getVariableDestino() != null ? cmd.getVariableDestino().trim() : "resultado";

        // Extraer conectores
        String fuente = null;
        String formato = null;
        String idioma = null;
        String para = null;

        for (NodoConector conector : cmd.getConectores()) {
            String cNombre = conector.getConector().toUpperCase();
            String cValor = conector.getExpresion() != null ? conector.getExpresion().toString() : "";
            switch (cNombre) {
                case "SOBRE":
                    fuente = cValor;
                    break;
                case "COMO":
                    formato = cValor;
                    break;
                case "EN":
                    idioma = cValor;
                    break;
                case "PARA":
                    para = cValor;
                    break;
                default:
                    break;
            }
        }

        StringBuilder sb = new StringBuilder();

        switch (tipo) {
            case "PREGUNTAR":
                if (fuente != null) {
                    sb.append("Basándote en ").append(fuente).append(", responde: ").append(p).append(".");
                } else {
                    sb.append("Responde a la siguiente consulta: ").append(p).append(".");
                }
                if (formato != null) {
                    sb.append(" Proporciona la respuesta en formato ").append(formato).append(".");
                }
                if (idioma != null) {
                    sb.append(" en idioma ").append(idioma).append(".");
                }
                if (para != null) {
                    sb.append(" dirigido para ").append(para).append(".");
                }
                sb.append(" Guarda la respuesta como ").append(y).append(".");
                break;

            case "GENERAR":
                if (fuente != null) {
                    sb.append("Basándote en ").append(fuente).append(", genera: ").append(p).append(".");
                } else {
                    sb.append("Genera el siguiente contenido: ").append(p).append(".");
                }
                if (formato != null) {
                    sb.append(" en formato ").append(formato).append(".");
                }
                if (idioma != null) {
                    sb.append(" en idioma ").append(idioma).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            case "CODIFICAR":
                if (fuente != null) {
                    sb.append("Basándote en ").append(fuente).append(", codifica: ").append(p).append(".");
                } else {
                    sb.append("Genera el código correspondiente para: ").append(p).append(".");
                }
                if (formato != null || idioma != null) {
                    sb.append(" Implementa la solución en ").append(formato != null ? formato : idioma).append(".");
                }
                sb.append(" Guarda el código resultante como ").append(y).append(".");
                break;

            case "RESUMIR":
                if (fuente != null) {
                    sb.append("Basándote en ").append(fuente).append(", sintetiza y resume: ").append(p).append(".");
                } else {
                    sb.append("Sintetiza y resume el siguiente texto: ").append(p).append(".");
                }
                if (formato != null) {
                    sb.append(" en formato ").append(formato).append(".");
                }
                sb.append(" Guarda el resumen como ").append(y).append(".");
                break;

            case "TRADUCIR":
                sb.append("Traduce ").append(p);
                if (idioma != null) {
                    sb.append(" al idioma ").append(idioma);
                }
                if (fuente != null) {
                    sb.append(" tomando como referencia ").append(fuente);
                }
                sb.append(". Guarda la traducción como ").append(y).append(".");
                break;

            case "ANALIZAR":
                if (fuente != null) {
                    sb.append("Basándote en ").append(fuente).append(", analiza detalladamente: ").append(p).append(".");
                } else {
                    sb.append("Analiza detalladamente: ").append(p).append(".");
                }
                if (formato != null) {
                    sb.append(" en formato ").append(formato).append(".");
                }
                sb.append(" Guarda el análisis como ").append(y).append(".");
                break;

            default:
                if (fuente != null) {
                    sb.append("Basándote en ").append(fuente).append(", ejecuta la instrucción ").append(tipo).append(": ").append(p).append(".");
                } else {
                    sb.append("Ejecuta la instrucción ").append(tipo).append(": ").append(p).append(".");
                }
                if (formato != null) {
                    sb.append(" en formato ").append(formato).append(".");
                }
                sb.append(" Guarda la respuesta como ").append(y).append(".");
                break;
        }

        return sb.toString();
    }

    // =========================================================================
    // GENERADOR PRINCIPAL DEL PROMPT FINAL EN TEXTO
    // =========================================================================

    /**
     * Recorre el AST y genera el texto final del Prompt estructurado según la rúbrica:
     * 1. Encabezado general con directivas declaradas (@modelo, @rol, @formato, etc.)
     * 2. Listado de agentes según EJECUTAR con contexto, variables y traducción a lenguaje natural
     * 3. Resultados solicitados por EXPORTAR al final del documento
     */
    public static String generarPromptFinal(NodoPrograma programa, TablaDeSimbolos tabla, Set<String> agentesEjecutados) {
        if (programa == null) {
            return "// Error: AST no disponible para generación de PromptZal.";
        }

        StringBuilder sb = new StringBuilder();
        String separador = "======================================================================\n";
        String separadorSec = "----------------------------------------------------------------------\n";

        sb.append(separador);
        sb.append("                  PROMPTZAL - ESPECIFICACIÓN DEL PROMPT FINAL\n");
        sb.append(separador);

        // 1. ENCABEZADO GENERAL CON DIRECTIVAS DECLARADAS
        sb.append("\n[ENCABEZADO GENERAL - DIRECTIVAS DECLARADAS]\n");
        if (programa.getDirectivas().isEmpty()) {
            sb.append("  (No se declararon directivas explícitas. Se asumen parámetros por defecto)\n");
        } else {
            for (NodoDirectiva d : programa.getDirectivas()) {
                String nombreDir = d.getNombre().toLowerCase();
                String desc = DIRECTIVAS_VALIDAS.getOrDefault(nombreDir, "Directiva del sistema");
                sb.append(String.format("  • %-18s : %-22s // %s\n", d.getNombre(), d.getValor(), desc));
            }
        }

        // 2. LISTADO DE AGENTES SEGÚN EJECUTAR
        sb.append("\n").append(separadorSec);
        sb.append("[LISTADO DE AGENTES SEGÚN INSTRUCCIONES 'EJECUTAR']\n");
        sb.append(separadorSec);

        if (agentesEjecutados == null || agentesEjecutados.isEmpty()) {
            sb.append("  (No se invocó ningún agente mediante la instrucción EJECUTAR)\n");
        } else {
            int orden = 1;
            for (String nombreAgente : agentesEjecutados) {
                NodoAgente agente = programa.buscarAgente(nombreAgente);
                if (agente == null) continue;

                sb.append(String.format("\n%d. AGENTE: %s\n", orden++, agente.getNombre()));
                sb.append("   • Contexto: ").append(agente.getContexto()).append("\n");

                // Variables del agente
                List<NodoVariable> vars = agente.getVariables();
                if (!vars.isEmpty()) {
                    sb.append("   • Variables locales:\n");
                    for (NodoVariable v : vars) {
                        sb.append("       * ").append(v.getNombre()).append(" = ")
                                .append(v.getExpresion() != null ? v.getExpresion().toString() : "").append("\n");
                    }
                }

                // Comandos traducidos a lenguaje natural
                List<NodoComando> cmds = agente.getComandos();
                if (!cmds.isEmpty()) {
                    sb.append("   • Instrucciones en lenguaje natural:\n");
                    int paso = 1;
                    for (NodoComando c : cmds) {
                        String instruccionNatural = traducirComandoALenguajeNatural(c);
                        sb.append(String.format("       Paso %d: %s\n", paso++, instruccionNatural));
                    }
                }
            }
        }

        // 3. RESULTADOS SOLICITADOS POR EXPORTAR AL FINAL DEL DOCUMENTO
        sb.append("\n").append(separadorSec);
        sb.append("[RESULTADOS SOLICITADOS POR 'EXPORTAR']\n");
        sb.append(separadorSec);

        NodoExportar exp = programa.getExportar();
        if (exp != null && !exp.getVariablesExportadas().isEmpty()) {
            sb.append("Al finalizar la ejecución, devuelve y consolida los siguientes resultados:\n");
            for (NodoExportar.ItemExportado item : exp.getVariablesExportadas()) {
                String varName = item.getNombreVariable();
                String origen = "Desconocido";
                if (agentesEjecutados != null && tabla != null) {
                    for (String ag : agentesEjecutados) {
                        Simbolo sim = tabla.buscarVariableEnAgente(varName, ag);
                        if (sim != null) {
                            origen = "Agente '" + ag + "'";
                            break;
                        }
                    }
                }
                sb.append(String.format("  • %-18s (fuente: %s)\n", varName, origen));
            }
        } else {
            sb.append("  (No se definieron variables a exportar en la instrucción EXPORTAR)\n");
        }

        sb.append("\n").append(separador);
        sb.append("                     FIN DEL DOCUMENTO PROMPTZAL\n");
        sb.append(separador);

        return sb.toString();
    }

    /**
     * Pretty Printer canónico del AST a código fuente PromptZal.
     */
    public static String generarCodigoPromptZalFormateado(NodoPrograma programa) {
        if (programa == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (NodoDirectiva d : programa.getDirectivas()) {
            sb.append(d.getNombre()).append(" ").append(d.getValor()).append("\n");
        }
        if (!programa.getDirectivas().isEmpty()) {
            sb.append("\n");
        }

        for (NodoAgente a : programa.getAgentes()) {
            sb.append("AGENTE ").append(a.getNombre()).append(" {\n");
            sb.append("    contexto = ").append(a.getContexto()).append("\n");

            for (NodoVariable v : a.getVariables()) {
                sb.append("    ").append(v.toString()).append("\n");
            }

            for (NodoComando c : a.getComandos()) {
                sb.append("    ").append(c.toString()).append("\n");
            }
            sb.append("}\n\n");
        }

        for (NodoEjecucion e : programa.getEjecuciones()) {
            sb.append(e.toString()).append("\n");
        }

        if (programa.getExportar() != null) {
            sb.append(programa.getExportar().toString()).append("\n");
        }

        return sb.toString();
    }
}
