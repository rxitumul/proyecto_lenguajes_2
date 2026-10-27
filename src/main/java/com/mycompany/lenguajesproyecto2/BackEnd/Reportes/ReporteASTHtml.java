package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoAgente;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoComando;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoConector;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoDirectiva;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoEjecucion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExportar;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoPrograma;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoVariable;

/**
 * Generador del Reporte del Árbol de Sintaxis Abstracta (AST).
 * Cumple con el requerimiento de la rúbrica:
 * "Reporte del AST: representación legible del árbol construido (HTML, texto con sangrías o Graphviz)."
 * Ofrece simultáneamente las 3 modalidades:
 * 1. Texto con sangrías jerárquicas legibles (ASCII/Unicode Tree).
 * 2. Código Graphviz DOT exportable y graficable.
 * 3. Documento interactivo HTML con estilos modernos.
 */
public class ReporteASTHtml extends ReportesHtml {

    // =========================================================================
    // 1. REPRESENTACIÓN EN TEXTO CON SANGRÍAS (TREE VIEW)
    // =========================================================================

    public static String generarArbolTexto(NodoPrograma prog) {
        if (prog == null) {
            return "(AST vacío o nulo)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("NodoPrograma [Fila: ").append(prog.getFila()).append(", Col: ").append(prog.getColumna()).append("]\n");

        // 1. Directivas
        if (!prog.getDirectivas().isEmpty()) {
            sb.append("├── Directivas (").append(prog.getDirectivas().size()).append(")\n");
            for (int i = 0; i < prog.getDirectivas().size(); i++) {
                NodoDirectiva d = prog.getDirectivas().get(i);
                boolean esUltima = (i == prog.getDirectivas().size() - 1) && prog.getAgentes().isEmpty()
                        && prog.getEjecuciones().isEmpty() && prog.getExportar() == null;
                String rama = (i == prog.getDirectivas().size() - 1) ? "└── " : "├── ";
                sb.append("│   ").append(rama).append(d.getNombre()).append(" = ").append(d.getValor())
                        .append(" [Fila: ").append(d.getFila()).append(", Col: ").append(d.getColumna()).append("]\n");
            }
        }

        // 2. Agentes
        if (!prog.getAgentes().isEmpty()) {
            sb.append("├── Agentes (").append(prog.getAgentes().size()).append(")\n");
            for (int i = 0; i < prog.getAgentes().size(); i++) {
                NodoAgente a = prog.getAgentes().get(i);
                boolean esUltimoAgente = (i == prog.getAgentes().size() - 1);
                String ramaAgente = esUltimoAgente ? "└── " : "├── ";
                String prefijoSub = esUltimoAgente ? "    " : "│   ";

                sb.append("│   ").append(ramaAgente).append("AGENTE: ").append(a.getNombre())
                        .append(" [Fila: ").append(a.getFila()).append(", Col: ").append(a.getColumna()).append("]\n");
                sb.append("│   ").append(prefijoSub).append("├── Contexto = ").append(a.getContexto()).append("\n");

                // Variables
                if (!a.getVariables().isEmpty()) {
                    sb.append("│   ").append(prefijoSub).append("├── Variables (").append(a.getVariables().size()).append(")\n");
                    for (int j = 0; j < a.getVariables().size(); j++) {
                        NodoVariable v = a.getVariables().get(j);
                        String ramaVar = (j == a.getVariables().size() - 1) ? "└── " : "├── ";
                        sb.append("│   ").append(prefijoSub).append("│   ").append(ramaVar)
                                .append(v.getNombre()).append(" = ").append(v.getExpresion() != null ? v.getExpresion().toString() : "")
                                .append(" [Fila: ").append(v.getFila()).append(", Col: ").append(v.getColumna()).append("]\n");
                    }
                }

                // Comandos
                if (!a.getComandos().isEmpty()) {
                    sb.append("│   ").append(prefijoSub).append("└── Comandos (").append(a.getComandos().size()).append(")\n");
                    for (int k = 0; k < a.getComandos().size(); k++) {
                        NodoComando c = a.getComandos().get(k);
                        String ramaCmd = (k == a.getComandos().size() - 1) ? "└── " : "├── ";
                        sb.append("│   ").append(prefijoSub).append("    ").append(ramaCmd)
                                .append(c.toString())
                                .append(" [Fila: ").append(c.getFila()).append(", Col: ").append(c.getColumna()).append("]\n");
                    }
                }
            }
        }

        // 3. Ejecuciones
        if (!prog.getEjecuciones().isEmpty()) {
            boolean tieneExportar = prog.getExportar() != null;
            String ramaEjec = tieneExportar ? "├── " : "└── ";
            sb.append(ramaEjec).append("Ejecuciones (").append(prog.getEjecuciones().size()).append(")\n");
            for (int i = 0; i < prog.getEjecuciones().size(); i++) {
                NodoEjecucion e = prog.getEjecuciones().get(i);
                String ramaItem = (i == prog.getEjecuciones().size() - 1) ? "└── " : "├── ";
                String pref = tieneExportar ? "│   " : "    ";
                sb.append(pref).append(ramaItem).append("EJECUTAR ").append(e.getNombreAgente())
                        .append(" [Fila: ").append(e.getFila()).append(", Col: ").append(e.getColumna()).append("]\n");
            }
        }

        // 4. Exportar
        if (prog.getExportar() != null) {
            sb.append("└── Exportar [Fila: ").append(prog.getExportar().getFila())
                    .append(", Col: ").append(prog.getExportar().getColumna()).append("]\n");
            for (int i = 0; i < prog.getExportar().getVariablesExportadas().size(); i++) {
                NodoExportar.ItemExportado item = prog.getExportar().getVariablesExportadas().get(i);
                String ramaItem = (i == prog.getExportar().getVariablesExportadas().size() - 1) ? "└── " : "├── ";
                sb.append("    ").append(ramaItem).append(item.getNombreVariable())
                        .append(" [Fila: ").append(item.getFila()).append(", Col: ").append(item.getColumna()).append("]\n");
            }
        }

        return sb.toString();
    }

    // =========================================================================
    // 2. REPRESENTACIÓN GRAPHVIZ DOT
    // =========================================================================

    public static String generarGraphvizDot(NodoPrograma prog) {
        if (prog == null) {
            return "digraph AST {\n  node [shape=box];\n  vacio [label=\"AST Vacío\"];\n}\n";
        }

        StringBuilder dot = new StringBuilder();
        dot.append("digraph AST_PromptZal {\n");
        dot.append("  rankdir=TB;\n");
        dot.append("  bgcolor=\"#0d1117\";\n");
        dot.append("  node [shape=box, style=\"filled,rounded\", fontname=\"Segoe UI\", fontsize=11, fontcolor=\"#ffffff\"];\n");
        dot.append("  edge [color=\"#58a6ff\", arrowhead=vee];\n\n");

        int[] contador = new int[]{1};

        String idRaiz = "node_" + (contador[0]++);
        dot.append("  ").append(idRaiz).append(" [label=\"Programa\\nPromptZal\", fillcolor=\"#1f6feb\", shape=ellipse];\n");

        // Directivas
        if (!prog.getDirectivas().isEmpty()) {
            String idDirPadre = "node_" + (contador[0]++);
            dot.append("  ").append(idDirPadre).append(" [label=\"Directivas\", fillcolor=\"#238636\"];\n");
            dot.append("  ").append(idRaiz).append(" -> ").append(idDirPadre).append(";\n");

            for (NodoDirectiva d : prog.getDirectivas()) {
                String idD = "node_" + (contador[0]++);
                String lbl = escapeDot(d.getNombre() + "\\n" + d.getValor());
                dot.append("  ").append(idD).append(" [label=\"").append(lbl).append("\", fillcolor=\"#2ea043\"];\n");
                dot.append("  ").append(idDirPadre).append(" -> ").append(idD).append(";\n");
            }
        }

        // Agentes
        if (!prog.getAgentes().isEmpty()) {
            String idAgentesPadre = "node_" + (contador[0]++);
            dot.append("  ").append(idAgentesPadre).append(" [label=\"Agentes\", fillcolor=\"#8957e5\"];\n");
            dot.append("  ").append(idRaiz).append(" -> ").append(idAgentesPadre).append(";\n");

            for (NodoAgente a : prog.getAgentes()) {
                String idA = "node_" + (contador[0]++);
                dot.append("  ").append(idA).append(" [label=\"AGENTE: ").append(escapeDot(a.getNombre()))
                        .append("\", fillcolor=\"#a371f7\"];\n");
                dot.append("  ").append(idAgentesPadre).append(" -> ").append(idA).append(";\n");

                // Contexto
                String idCtx = "node_" + (contador[0]++);
                dot.append("  ").append(idCtx).append(" [label=\"contexto = \\\"").append(escapeDot(a.getContexto()))
                        .append("\\\"\", fillcolor=\"#30363d\", fontcolor=\"#c9d1d9\"];\n");
                dot.append("  ").append(idA).append(" -> ").append(idCtx).append(";\n");

                // Variables
                if (!a.getVariables().isEmpty()) {
                    String idVarsPadre = "node_" + (contador[0]++);
                    dot.append("  ").append(idVarsPadre).append(" [label=\"Variables\", fillcolor=\"#238636\"];\n");
                    dot.append("  ").append(idA).append(" -> ").append(idVarsPadre).append(";\n");

                    for (NodoVariable v : a.getVariables()) {
                        String idV = "node_" + (contador[0]++);
                        String descVar = v.getNombre() + " = " + (v.getExpresion() != null ? v.getExpresion().toString() : "");
                        dot.append("  ").append(idV).append(" [label=\"").append(escapeDot(descVar))
                                .append("\", fillcolor=\"#2ea043\"];\n");
                        dot.append("  ").append(idVarsPadre).append(" -> ").append(idV).append(";\n");
                    }
                }

                // Comandos
                if (!a.getComandos().isEmpty()) {
                    String idCmdsPadre = "node_" + (contador[0]++);
                    dot.append("  ").append(idCmdsPadre).append(" [label=\"Comandos IA\", fillcolor=\"#d29922\"];\n");
                    dot.append("  ").append(idA).append(" -> ").append(idCmdsPadre).append(";\n");

                    for (NodoComando c : a.getComandos()) {
                        String idC = "node_" + (contador[0]++);
                        dot.append("  ").append(idC).append(" [label=\"").append(escapeDot(c.toString()))
                                .append("\", fillcolor=\"#e3b341\", fontcolor=\"#000000\"];\n");
                        dot.append("  ").append(idCmdsPadre).append(" -> ").append(idC).append(";\n");
                    }
                }
            }
        }

        // Ejecuciones
        if (!prog.getEjecuciones().isEmpty()) {
            String idEjecsPadre = "node_" + (contador[0]++);
            dot.append("  ").append(idEjecsPadre).append(" [label=\"Ejecuciones\", fillcolor=\"#da3633\"];\n");
            dot.append("  ").append(idRaiz).append(" -> ").append(idEjecsPadre).append(";\n");

            for (NodoEjecucion e : prog.getEjecuciones()) {
                String idE = "node_" + (contador[0]++);
                dot.append("  ").append(idE).append(" [label=\"EJECUTAR ").append(escapeDot(e.getNombreAgente()))
                        .append("\", fillcolor=\"#f85149\"];\n");
                dot.append("  ").append(idEjecsPadre).append(" -> ").append(idE).append(";\n");
            }
        }

        // Exportar
        if (prog.getExportar() != null) {
            String idExpPadre = "node_" + (contador[0]++);
            dot.append("  ").append(idExpPadre).append(" [label=\"Exportar\", fillcolor=\"#1f6feb\"];\n");
            dot.append("  ").append(idRaiz).append(" -> ").append(idExpPadre).append(";\n");

            for (NodoExportar.ItemExportado item : prog.getExportar().getVariablesExportadas()) {
                String idItem = "node_" + (contador[0]++);
                dot.append("  ").append(idItem).append(" [label=\"").append(escapeDot(item.getNombreVariable()))
                        .append("\", fillcolor=\"#388bfd\"];\n");
                dot.append("  ").append(idExpPadre).append(" -> ").append(idItem).append(";\n");
            }
        }

        dot.append("}\n");
        return dot.toString();
    }

    // =========================================================================
    // 3. GENERADOR DE REPORTE HTML COMPLETO
    // =========================================================================

    public StringBuilder generarHTML(NodoPrograma prog) {
        StringBuilder html = new StringBuilder();

        String textoArbol = generarArbolTexto(prog);
        String dotCodigo = generarGraphvizDot(prog);

        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>PromptZal - Reporte del Árbol de Sintaxis Abstracta (AST)</title>\n");
        html.append("  <style>\n");
        html.append("    :root {\n");
        html.append("      --bg-dark: #0d1117;\n");
        html.append("      --bg-card: #161b22;\n");
        html.append("      --border-color: #30363d;\n");
        html.append("      --text-main: #c9d1d9;\n");
        html.append("      --text-muted: #8b949e;\n");
        html.append("      --accent-blue: #58a6ff;\n");
        html.append("      --accent-green: #3fb950;\n");
        html.append("      --accent-purple: #bc8cff;\n");
        html.append("    }\n");
        html.append("    body {\n");
        html.append("      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\n");
        html.append("      background-color: var(--bg-dark);\n");
        html.append("      color: var(--text-main);\n");
        html.append("      margin: 0;\n");
        html.append("      padding: 30px 20px;\n");
        html.append("    }\n");
        html.append("    .container {\n");
        html.append("      max-width: 1200px;\n");
        html.append("      margin: 0 auto;\n");
        html.append("      background: var(--bg-card);\n");
        html.append("      border: 1px solid var(--border-color);\n");
        html.append("      border-radius: 12px;\n");
        html.append("      padding: 30px;\n");
        html.append("      box-shadow: 0 10px 30px rgba(0,0,0,0.5);\n");
        html.append("    }\n");
        html.append("    .header {\n");
        html.append("      border-bottom: 2px solid var(--border-color);\n");
        html.append("      padding-bottom: 20px;\n");
        html.append("      margin-bottom: 25px;\n");
        html.append("    }\n");
        html.append("    h1 {\n");
        html.append("      color: var(--accent-blue);\n");
        html.append("      margin: 0 0 10px 0;\n");
        html.append("      font-size: 26px;\n");
        html.append("    }\n");
        html.append("    p.sub {\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      margin: 0;\n");
        html.append("      font-size: 14px;\n");
        html.append("    }\n");
        html.append("    .section-title {\n");
        html.append("      color: #f0f6fc;\n");
        html.append("      font-size: 18px;\n");
        html.append("      margin: 30px 0 12px 0;\n");
        html.append("      display: flex;\n");
        html.append("      align-items: center;\n");
        html.append("      gap: 10px;\n");
        html.append("    }\n");
        html.append("    .code-block {\n");
        html.append("      background: #0d1117;\n");
        html.append("      border: 1px solid var(--border-color);\n");
        html.append("      border-radius: 8px;\n");
        html.append("      padding: 20px;\n");
        html.append("      font-family: 'Consolas', 'Courier New', monospace;\n");
        html.append("      font-size: 13px;\n");
        html.append("      line-height: 1.6;\n");
        html.append("      color: #7ee787;\n");
        html.append("      white-space: pre;\n");
        html.append("      overflow-x: auto;\n");
        html.append("    }\n");
        html.append("    .dot-block {\n");
        html.append("      color: #a5d6ff;\n");
        html.append("    }\n");
        html.append("    .footer {\n");
        html.append("      margin-top: 30px;\n");
        html.append("      padding-top: 15px;\n");
        html.append("      border-top: 1px solid var(--border-color);\n");
        html.append("      font-size: 12px;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      text-align: right;\n");
        html.append("    }\n");
        html.append("  </style>\n");
        html.append("</head>\n<body>\n");
        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>Reporte del Árbol de Sintaxis Abstracta (AST)</h1>\n");
        html.append("    <p class=\"sub\">Estructura jerárquica del programa PromptZal construida durante el análisis sintáctico.</p>\n");
        html.append("  </div>\n");

        html.append("  <div class=\"section-title\"><span>🌳</span> Representación en Texto con Sangrías Jerárquicas</div>\n");
        html.append("  <div class=\"code-block\">").append(escapeHtml(textoArbol)).append("</div>\n");

        html.append("  <div class=\"section-title\"><span>📊</span> Definición en Graphviz DOT (Graficable)</div>\n");
        html.append("  <div class=\"code-block dot-block\">").append(escapeHtml(dotCodigo)).append("</div>\n");

        html.append("  <div class=\"footer\">\n");
        html.append("    Generado automáticamente por el compilador PromptZal • ")
            .append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()))
            .append("\n");
        html.append("  </div>\n");
        html.append("</div>\n");
        html.append("</body>\n</html>");

        return html;
    }

    public void crearReporte(String rutaArchivo, NodoPrograma prog) {
        creadorDeArchivos(rutaArchivo, generarHTML(prog));
    }

    public static boolean guardarDotEnArchivo(String rutaDot, NodoPrograma prog) {
        if (rutaDot == null || prog == null) return false;
        try {
            File f = new File(rutaDot);
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(f)) {
                w.write(generarGraphvizDot(prog));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar archivo DOT: " + e.getMessage());
            return false;
        }
    }

    private static String escapeDot(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
