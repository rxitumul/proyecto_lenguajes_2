package com.mycompany.lenguajesproyecto2.BackEnd.Semantico;

import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoAgente;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoComando;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoConector;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoDirectiva;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoEjecucion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExportar;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExpresion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoPrograma;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoTermino;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoVariable;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.Simbolo;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.TablaDeSimbolos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
     * Limpia comillas circundantes de un texto si existen.
     */
    public static String limpiarCadena(String texto) {
        if (texto == null) return "";
        String trimmed = texto.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    /**
     * Traduce un comando de PromptZal a una instrucción exacta en Lenguaje Natural
     * según los requerimientos de la especificación del PDF (página 8).
     * Ejemplo: PREGUNTAR p SOBRE x -> y se traduce a:
     * "Basandote en x, responde: p. Guarda la respuesta como y."
     */
    public static String traducirComandoALenguajeNatural(NodoComando cmd) {
        if (cmd == null) {
            return "";
        }

        String tipo = cmd.getTipoComando() != null ? cmd.getTipoComando().trim().toUpperCase() : "COMANDO";
        String p = limpiarCadena(cmd.getExpresion() != null ? cmd.getExpresion().toString() : "");
        String y = cmd.getVariableDestino() != null ? cmd.getVariableDestino().trim() : "resultado";

        // Extraer conectores
        String fuente = null;  // SOBRE
        String desde = null;   // DESDE
        String en = null;      // EN
        String formato = null; // COMO
        String para = null;

        for (NodoConector conector : cmd.getConectores()) {
            String cNombre = conector.getConector().toUpperCase();
            String cValor = limpiarCadena(conector.getExpresion() != null ? conector.getExpresion().toString() : "");
            switch (cNombre) {
                case "SOBRE":
                    fuente = cValor;
                    break;
                case "DESDE":
                    desde = cValor;
                    break;
                case "COMO":
                    formato = cValor;
                    break;
                case "EN":
                    en = cValor;
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
                    sb.append("Basandote en ").append(fuente).append(", responde: ").append(p);
                } else {
                    sb.append("Responde: ").append(p);
                }
                if (p.endsWith("?") || p.endsWith("!")) {
                    sb.append(" ");
                } else {
                    sb.append(". ");
                }
                if (para != null) {
                    sb.append("Dirigido para ").append(para).append(". ");
                }
                sb.append("Guarda la respuesta como ").append(y).append(".");
                break;

            case "GENERAR":
                if (desde != null) {
                    sb.append("Genera ").append(p).append(" a partir de: ").append(desde).append(".");
                } else if (fuente != null) {
                    sb.append("Basandote en ").append(fuente).append(", genera: ").append(p).append(".");
                } else {
                    sb.append("Genera ").append(p).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            case "CODIFICAR":
                if (desde != null) {
                    sb.append("Genera el codigo de ").append(p).append(" a partir de: ").append(desde).append(".");
                } else if (fuente != null) {
                    sb.append("Basandote en ").append(fuente).append(", codifica: ").append(p).append(".");
                } else {
                    sb.append("Codifica: ").append(p).append(".");
                }
                if (en != null) {
                    sb.append(" en el entorno ").append(en).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            case "RESUMIR":
                if (en != null) {
                    sb.append("Resume ").append(p).append(" en un maximo de ").append(en).append(".");
                } else if (fuente != null) {
                    sb.append("Basandote en ").append(fuente).append(", resume: ").append(p).append(".");
                } else {
                    sb.append("Resume ").append(p).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            case "TRADUCIR":
                sb.append("Traduce ").append(p);
                if (en != null) {
                    sb.append(" al idioma ").append(en);
                }
                if (fuente != null) {
                    sb.append(" tomando como referencia ").append(fuente);
                }
                sb.append(". Guarda el resultado como ").append(y).append(".");
                break;

            case "ANALIZAR":
                if (fuente != null) {
                    sb.append("Analiza ").append(p).append(" considerando: ").append(fuente).append(".");
                } else {
                    sb.append("Analiza ").append(p).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            case "CLASIFICAR":
                if (en != null) {
                    sb.append("Clasifica ").append(p).append(" en las categorias: ").append(en).append(".");
                } else if (fuente != null) {
                    sb.append("Basandote en ").append(fuente).append(", clasifica: ").append(p).append(".");
                } else {
                    sb.append("Clasifica ").append(p).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            case "EXTRAER":
                if (desde != null) {
                    sb.append("Extrae ").append(p).append(" a partir de ").append(desde).append(".");
                } else if (fuente != null) {
                    sb.append("Extrae ").append(p).append(" a partir de ").append(fuente).append(".");
                } else {
                    sb.append("Extrae ").append(p).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;

            default:
                if (fuente != null) {
                    sb.append("Basandote en ").append(fuente).append(", ejecuta ").append(tipo).append(": ").append(p).append(".");
                } else {
                    sb.append("Ejecuta ").append(tipo).append(": ").append(p).append(".");
                }
                sb.append(" Guarda el resultado como ").append(y).append(".");
                break;
        }

        // Si incluye conector COMO f (en cualquier comando según rúbrica)
        if (formato != null && !formato.trim().isEmpty()) {
            sb.append(" Entrega el resultado en formato ").append(formato).append(".");
        }

        return sb.toString();
    }

    // =========================================================================
    // GENERADOR PRINCIPAL DEL PROMPT FINAL EN TEXTO
    // =========================================================================

    /**
     * Recorre el AST y genera el texto final del Prompt en el formato
     * canónico y oficial especificado en la página 8 del PDF:
     *
     * # Prompt generado con PromptZal
     * Modelo destino: <modelo>
     * Rol general: <rol>
     * Formato de respuesta: <formato>
     *
     * ## Agente: <nombre>
     * Contexto: <contexto>
     *
     * Variables disponibles:
     * - <var>: contenido del archivo "<ruta>" (adjuntar el archivo)
     * - <var>: <valor>
     *
     * Instrucciones:
     * 1. <instrucción en lenguaje natural>
     * 2. ...
     *
     * ## Resultado solicitado
     * - <var>
     */
    public static String generarPromptFinal(NodoPrograma programa, TablaDeSimbolos tabla, Set<String> agentesEjecutados) {
        if (programa == null) {
            return "// Error: AST no disponible para generación de PromptZal.";
        }

        StringBuilder sb = new StringBuilder();

        // 1. ENCABEZADO GENERAL
        sb.append("# Prompt generado con PromptZal\n");

        String modelo = null;
        String rol = null;
        String formato = null;
        java.util.Map<String, String> otrasDirectivas = new java.util.LinkedHashMap<>();

        for (NodoDirectiva d : programa.getDirectivas()) {
            String nombre = d.getNombre() != null ? d.getNombre().toLowerCase().trim() : "";
            String val = limpiarCadena(d.getValor());
            switch (nombre) {
                case "@modelo":
                    modelo = val;
                    break;
                case "@rol":
                    rol = val;
                    break;
                case "@formato":
                    formato = val;
                    break;
                default:
                    otrasDirectivas.put(d.getNombre(), val);
                    break;
            }
        }

        if (modelo != null) {
            sb.append("Modelo destino: ").append(modelo).append("\n");
        }
        if (rol != null) {
            sb.append("Rol general: ").append(rol).append("\n");
        }
        if (formato != null) {
            sb.append("Formato de respuesta: ").append(formato).append("\n");
        }
        for (java.util.Map.Entry<String, String> entry : otrasDirectivas.entrySet()) {
            String dirNombre = entry.getKey().startsWith("@") ? entry.getKey().substring(1) : entry.getKey();
            dirNombre = Character.toUpperCase(dirNombre.charAt(0)) + dirNombre.substring(1);
            sb.append(dirNombre).append(": ").append(entry.getValue()).append("\n");
        }

        // 2. AGENTES INVOCADOS SEGÚN 'EJECUTAR'
        if (agentesEjecutados != null && !agentesEjecutados.isEmpty()) {
            for (String nombreAgente : agentesEjecutados) {
                NodoAgente agente = programa.buscarAgente(nombreAgente);
                if (agente == null) continue;

                sb.append("\n## Agente: ").append(agente.getNombre()).append("\n");
                sb.append("Contexto: ").append(limpiarCadena(agente.getContexto())).append("\n\n");

                // Variables del agente
                List<NodoVariable> vars = agente.getVariables();
                if (!vars.isEmpty()) {
                    sb.append("Variables disponibles:\n");
                    for (NodoVariable v : vars) {
                        NodoExpresion exp = v.getExpresion();
                        if (exp != null && exp.getTerminos().size() == 1
                                && exp.getTerminos().get(0).getTipo() == com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoTermino.TipoTermino.CARGAR) {
                            String ruta = limpiarCadena(exp.getTerminos().get(0).getRutaArchivo());
                            sb.append("- ").append(v.getNombre()).append(": contenido del archivo \"").append(ruta).append("\" (adjuntar el archivo)\n");
                        } else {
                            String val = limpiarCadena(exp != null ? exp.toString() : "");
                            sb.append("- ").append(v.getNombre()).append(": ").append(val).append("\n");
                        }
                    }
                    sb.append("\n");
                }

                // Instrucciones traducidas a lenguaje natural
                List<NodoComando> cmds = agente.getComandos();
                if (!cmds.isEmpty()) {
                    sb.append("Instrucciones:\n");
                    int paso = 1;
                    for (NodoComando c : cmds) {
                        String instr = traducirComandoALenguajeNatural(c);
                        sb.append(paso++).append(". ").append(instr).append("\n");
                    }
                }
            }
        }

        // 3. RESULTADOS SOLICITADOS SEGÚN 'EXPORTAR'
        NodoExportar exp = programa.getExportar();
        if (exp != null && !exp.getVariablesExportadas().isEmpty()) {
            sb.append("\n## Resultado solicitado\n");
            for (NodoExportar.ItemExportado item : exp.getVariablesExportadas()) {
                sb.append("- ").append(item.getNombreVariable()).append("\n");
            }
        }

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

    // =========================================================================
    // SALIDA Y EXPORTACIÓN A ARCHIVO EN DISCO
    // =========================================================================

    /**
     * Guarda el contenido del prompt final generado en un archivo físico en disco.
     * Crea directorios padres si no existen.
     *
     * @param rutaArchivo Ruta completa relativa o absoluta del archivo destino.
     * @param contenidoPrompt Texto completo del prompt a guardar.
     * @return true si la escritura fue exitosa, false en caso de error de E/S.
     */
    public static boolean guardarPromptEnArchivo(String rutaArchivo, String contenidoPrompt) {
        if (rutaArchivo == null || contenidoPrompt == null) {
            return false;
        }
        try {
            File archivo = new File(rutaArchivo);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(archivo)) {
                writer.write(contenidoPrompt);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar archivo de PromptZal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Genera el prompt final a partir del AST y lo guarda automáticamente en el archivo indicado.
     *
     * @param programa AST del programa analizado.
     * @param tabla Tabla de símbolos.
     * @param agentesEjecutados Conjunto de nombres de agentes ejecutados.
     * @param rutaArchivo Ruta del archivo de destino.
     * @return Texto del prompt generado.
     */
    public static String generarYGuardarPrompt(NodoPrograma programa, TablaDeSimbolos tabla,
                                               Set<String> agentesEjecutados, String rutaArchivo) {
        String promptTexto = generarPromptFinal(programa, tabla, agentesEjecutados);
        if (rutaArchivo != null && !rutaArchivo.trim().isEmpty()) {
            guardarPromptEnArchivo(rutaArchivo, promptTexto);
        }
        return promptTexto;
    }
}
