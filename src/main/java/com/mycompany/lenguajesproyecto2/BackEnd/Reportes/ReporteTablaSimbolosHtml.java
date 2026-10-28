package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.Simbolo;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.TablaDeSimbolos;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.TablaSimbolos.TipoSimbolo;

/**
 * Generador del Reporte de la Tabla de Símbolos en formato HTML.
 * Cumple con el requerimiento oficial de la rúbrica:
 * "Reporte de tabla de símbolos (HTML): detalla agentes y variables
 * con su tipo, ámbito y línea de declaración."
 */
public class ReporteTablaSimbolosHtml extends ReportesHtml {

    public StringBuilder generarHTML(TablaDeSimbolos tabla) {
        StringBuilder html = new StringBuilder();

        List<Simbolo> simbolos = tabla != null ? tabla.getTodosLosSimbolos() : List.of();

        long cantAgentes = simbolos.stream().filter(s -> s.getTipo() == TipoSimbolo.AGENTE).count();
        long cantVariables = simbolos.stream().filter(s -> s.getTipo() == TipoSimbolo.VARIABLE).count();
        long cantDirectivas = simbolos.stream().filter(s -> s.getTipo() == TipoSimbolo.DIRECTIVA).count();

        appendHead(html, "PromptZal - Reporte de Tabla de Símbolos", true);
        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>Reporte de la Tabla de Símbolos</h1>\n");
        html.append("    <p class=\"sub\">Detalle de Agentes, Variables locales y Directivas registradas con su tipo, ámbito y línea de declaración.</p>\n");
        html.append("  </div>\n");

        // Tarjetas
        html.append("  <div class=\"stats-container\">\n");
        html.append("    <div class=\"stat-card stat-total\"><div class=\"val\">").append(simbolos.size()).append("</div><div class=\"lbl\">Total de Símbolos</div></div>\n");
        html.append("    <div class=\"stat-card stat-agentes\"><div class=\"val\">").append(cantAgentes).append("</div><div class=\"lbl\">Agentes Declarados</div></div>\n");
        html.append("    <div class=\"stat-card stat-vars\"><div class=\"val\">").append(cantVariables).append("</div><div class=\"lbl\">Variables Locales</div></div>\n");
        html.append("    <div class=\"stat-card stat-dirs\"><div class=\"val\">").append(cantDirectivas).append("</div><div class=\"lbl\">Directivas Globales</div></div>\n");
        html.append("  </div>\n");

        // ── GRÁFICAS ──────────────────────────────────────────────────────────
        long cantGlobales = simbolos.stream().filter(Simbolo::esGlobal).count();
        long cantLocales = simbolos.size() - cantGlobales;

        html.append("  <div class=\"charts-row\">\n");
        // Dona: distribución por tipo
        html.append("    <div class=\"chart-card\">\n");
        html.append("      <h3>Distribución por tipo de símbolo</h3>\n");
        html.append("      <div class=\"chart-wrapper\"><canvas id=\"chartDona\"></canvas></div>\n");
        html.append("    </div>\n");
        // Barras horizontales: global vs local
        html.append("    <div class=\"chart-card\">\n");
        html.append("      <h3>Símbolos por ámbito</h3>\n");
        html.append("      <div class=\"chart-wrapper\"><canvas id=\"chartBarras\"></canvas></div>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");

        if (simbolos.isEmpty()) {
            html.append("  <div class=\"empty-box\">\n");
            html.append("    No se registraron símbolos en la tabla. Verifique que el programa se haya compilado correctamente.\n");
            html.append("  </div>\n");
        } else {
            html.append("  <table>\n");
            html.append("    <thead>\n");
            html.append("      <tr>\n");
            html.append("        <th>#</th>\n");
            html.append("        <th>Identificador</th>\n");
            html.append("        <th>Tipo de Símbolo</th>\n");
            html.append("        <th>Ámbito (Scope)</th>\n");
            html.append("        <th>Línea (Fila)</th>\n");
            html.append("        <th>Columna</th>\n");
            html.append("        <th>Valor Inicial / Contexto</th>\n");
            html.append("      </tr>\n");
            html.append("    </thead>\n");
            html.append("    <tbody>\n");

            int contador = 1;
            for (Simbolo s : simbolos) {
                String badgeTipo;
                switch (s.getTipo()) {
                    case AGENTE:
                        badgeTipo = "<span class=\"badge badge-agente\">AGENTE</span>";
                        break;
                    case VARIABLE:
                        badgeTipo = "<span class=\"badge badge-variable\">VARIABLE</span>";
                        break;
                    case DIRECTIVA:
                        badgeTipo = "<span class=\"badge badge-directiva\">DIRECTIVA</span>";
                        break;
                    default:
                        badgeTipo = "<span class=\"badge\">" + s.getTipo() + "</span>";
                        break;
                }

                String badgeAmbito = s.esGlobal()
                        ? "<span class=\"badge-global\">GLOBAL</span>"
                        : "<span class=\"badge-local\">Agente: " + escapeHtml(s.getAmbito()) + "</span>";

                String valor = s.getValorInicial() != null ? escapeHtml(s.getValorInicial()) : "-";

                html.append("      <tr>\n")
                    .append("        <td>").append(contador++).append("</td>\n")
                    .append("        <td><span class=\"code-id\">").append(escapeHtml(s.getIdentificador())).append("</span></td>\n")
                    .append("        <td>").append(badgeTipo).append("</td>\n")
                    .append("        <td>").append(badgeAmbito).append("</td>\n")
                    .append("        <td><strong>").append(s.getLinea()).append("</strong></td>\n")
                    .append("        <td>").append(s.getColumna()).append("</td>\n")
                    .append("        <td class=\"val-cell\" title=\"").append(valor).append("\">").append(valor).append("</td>\n")
                    .append("      </tr>\n");
            }

            html.append("    </tbody>\n");
            html.append("  </table>\n");
        }

        html.append("  <div class=\"footer\">\n");
        html.append("    Generado automáticamente por el compilador PromptZal • ")
            .append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()))
            .append("\n");
        html.append("  </div>\n");
        html.append("</div>\n");

        // ── SCRIPTS CHART.JS ─────────────────────────────────────────────────
        html.append("<script>\n");
        html.append("(function() {\n");
        html.append("  Chart.defaults.color = '#8b949e';\n");
        html.append("  Chart.defaults.borderColor = '#30363d';\n");
        html.append("  const dAgentes = " + cantAgentes + ";\n");
        html.append("  const dVars = " + cantVariables + ";\n");
        html.append("  const dDirs = " + cantDirectivas + ";\n");
        html.append("  const dGlobales = " + cantGlobales + ";\n");
        html.append("  const dLocales = " + cantLocales + ";\n");
        html.append("  const total = dAgentes + dVars + dDirs;\n");

        // Dona
        html.append("  new Chart(document.getElementById('chartDona'), {\n");
        html.append("    type: 'doughnut',\n");
        html.append("    data: {\n");
        html.append("      labels: ['Agentes', 'Variables', 'Directivas'],\n");
        html.append("      datasets: [{\n");
        html.append("        data: [dAgentes, dVars, dDirs],\n");
        html.append("        backgroundColor: ['rgba(188,140,255,0.8)','rgba(63,185,80,0.8)','rgba(57,197,207,0.8)'],\n");
        html.append("        borderColor: ['#bc8cff','#3fb950','#39c5cf'],\n");
        html.append("        borderWidth: 2, hoverOffset: 8\n");
        html.append("      }]\n");
        html.append("    },\n");
        html.append("    options: {\n");
        html.append("      responsive: true, maintainAspectRatio: false,\n");
        html.append("      plugins: {\n");
        html.append("        legend: { position: 'bottom', labels: { color: '#c9d1d9', padding: 14, font: { size: 12 } } },\n");
        html.append("        tooltip: { callbacks: { label: ctx => ` ${ctx.label}: ${ctx.raw} (${total > 0 ? Math.round(ctx.raw*100/total) : 0}%)` } }\n");
        html.append("      }\n");
        html.append("    }\n");
        html.append("  });\n");

        // Barras horizontales: Global vs Local
        html.append("  new Chart(document.getElementById('chartBarras'), {\n");
        html.append("    type: 'bar',\n");
        html.append("    data: {\n");
        html.append("      labels: ['Global', 'Local (por agente)'],\n");
        html.append("      datasets: [{\n");
        html.append("        label: 'Cantidad',\n");
        html.append("        data: [dGlobales, dLocales],\n");
        html.append("        backgroundColor: ['rgba(88,166,255,0.5)','rgba(210,153,34,0.5)'],\n");
        html.append("        borderColor: ['#58a6ff','#d29922'],\n");
        html.append("        borderWidth: 2, borderRadius: 6\n");
        html.append("      }]\n");
        html.append("    },\n");
        html.append("    options: {\n");
        html.append("      indexAxis: 'y',\n");
        html.append("      responsive: true, maintainAspectRatio: false,\n");
        html.append("      plugins: { legend: { display: false } },\n");
        html.append("      scales: {\n");
        html.append("        x: { beginAtZero: true, ticks: { precision: 0, color: '#8b949e' }, grid: { color: '#21262d' } },\n");
        html.append("        y: { ticks: { color: '#c9d1d9', font: { size: 13 } }, grid: { color: '#21262d' } }\n");
        html.append("      }\n");
        html.append("    }\n");
        html.append("  });\n");

        html.append("})();\n");
        html.append("</script>\n");
        html.append("</body>\n</html>");

        return html;
    }

    public void crearReporte(String rutaArchivo, TablaDeSimbolos tabla) {
        creadorDeArchivos(rutaArchivo, generarHTML(tabla));
    }

    public static DefaultTableModel generarModeloSwing(TablaDeSimbolos tabla) {
        String[] columnas = {"#", "Identificador", "Tipo", "Ámbito", "Línea", "Columna", "Valor / Contexto"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (tabla != null) {
            int contador = 1;
            for (Simbolo s : tabla.getTodosLosSimbolos()) {
                modelo.addRow(new Object[]{
                    contador++,
                    s.getIdentificador(),
                    s.getTipo().toString(),
                    s.getAmbito(),
                    s.getLinea(),
                    s.getColumna(),
                    s.getValorInicial() != null ? s.getValorInicial() : "-"
                });
            }
        }
        return modelo;
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
