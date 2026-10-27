package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import java.util.List;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico.ErrorSintactico;
import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas.ErrorLexico;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.ErrorSemantico;

/**
 * Generador del Reporte de Errores en formato HTML.
 * Cumple estrictamente con la rúbrica oficial:
 * "Reporte de errores (HTML): detalla todos los errores de compilación
 * (léxicos, sintácticos y semánticos), descripción, fila, columna y,
 * en los sintácticos, token encontrado y esperado."
 */
public class ReporteErroresHtml extends ReportesHtml {

    public StringBuilder generarHTML(List<ErrorLexico> erroresLexicos,
                                     List<ErrorSintactico> erroresSintacticos,
                                     List<ErrorSemantico> erroresSemanticos) {
        StringBuilder html = new StringBuilder();

        int cantLexicos = erroresLexicos != null ? erroresLexicos.size() : 0;
        int cantSintacticos = erroresSintacticos != null ? erroresSintacticos.size() : 0;
        int cantSemanticos = erroresSemanticos != null ? erroresSemanticos.size() : 0;
        int totalErrores = cantLexicos + cantSintacticos + cantSemanticos;

        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>PromptZal - Reporte de Errores de Compilación</title>\n");
        html.append("  <script src=\"https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js\"></script>\n");
        html.append("  <style>\n");
        html.append("    :root {\n");
        html.append("      --bg-dark: #0d1117;\n");
        html.append("      --bg-card: #161b22;\n");
        html.append("      --bg-hover: #1f242c;\n");
        html.append("      --border-color: #30363d;\n");
        html.append("      --text-main: #c9d1d9;\n");
        html.append("      --text-muted: #8b949e;\n");
        html.append("      --accent-blue: #58a6ff;\n");
        html.append("      --accent-red: #f85149;\n");
        html.append("      --accent-orange: #d29922;\n");
        html.append("      --accent-purple: #bc8cff;\n");
        html.append("      --accent-green: #3fb950;\n");
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
        html.append("    .stats-container {\n");
        html.append("      display: grid;\n");
        html.append("      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n");
        html.append("      gap: 15px;\n");
        html.append("      margin-bottom: 25px;\n");
        html.append("    }\n");
        html.append("    .stat-card {\n");
        html.append("      background: #0d1117;\n");
        html.append("      border: 1px solid var(--border-color);\n");
        html.append("      border-radius: 8px;\n");
        html.append("      padding: 15px 20px;\n");
        html.append("    }\n");
        html.append("    .stat-card .val {\n");
        html.append("      font-size: 24px;\n");
        html.append("      font-weight: 700;\n");
        html.append("      margin-bottom: 4px;\n");
        html.append("    }\n");
        html.append("    .stat-card .lbl {\n");
        html.append("      font-size: 12px;\n");
        html.append("      text-transform: uppercase;\n");
        html.append("      letter-spacing: 0.5px;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("    }\n");
        html.append("    .stat-total .val { color: var(--accent-red); }\n");
        html.append("    .stat-lex .val { color: var(--accent-orange); }\n");
        html.append("    .stat-sin .val { color: var(--accent-purple); }\n");
        html.append("    .stat-sem .val { color: #f0883e; }\n");
        html.append("    .badge {\n");
        html.append("      display: inline-block;\n");
        html.append("      padding: 4px 10px;\n");
        html.append("      border-radius: 20px;\n");
        html.append("      font-size: 12px;\n");
        html.append("      font-weight: 600;\n");
        html.append("    }\n");
        html.append("    .badge-lex { background: rgba(210, 153, 34, 0.2); color: var(--accent-orange); border: 1px solid rgba(210, 153, 34, 0.4); }\n");
        html.append("    .badge-sin { background: rgba(188, 140, 255, 0.2); color: var(--accent-purple); border: 1px solid rgba(188, 140, 255, 0.4); }\n");
        html.append("    .badge-sem { background: rgba(240, 136, 62, 0.2); color: #f0883e; border: 1px solid rgba(240, 136, 62, 0.4); }\n");
        html.append("    table {\n");
        html.append("      width: 100%;\n");
        html.append("      border-collapse: collapse;\n");
        html.append("      font-size: 13px;\n");
        html.append("      margin-top: 15px;\n");
        html.append("    }\n");
        html.append("    th, td {\n");
        html.append("      padding: 12px 14px;\n");
        html.append("      text-align: left;\n");
        html.append("      border-bottom: 1px solid var(--border-color);\n");
        html.append("    }\n");
        html.append("    th {\n");
        html.append("      background: #21262d;\n");
        html.append("      color: #f0f6fc;\n");
        html.append("      text-transform: uppercase;\n");
        html.append("      font-size: 11px;\n");
        html.append("      letter-spacing: 0.5px;\n");
        html.append("    }\n");
        html.append("    tr:hover {\n");
        html.append("      background: var(--bg-hover);\n");
        html.append("    }\n");
        html.append("    .code-pill {\n");
        html.append("      font-family: 'Consolas', 'Courier New', monospace;\n");
        html.append("      background: #0d1117;\n");
        html.append("      border: 1px solid var(--border-color);\n");
        html.append("      padding: 2px 6px;\n");
        html.append("      border-radius: 4px;\n");
        html.append("      font-size: 12px;\n");
        html.append("    }\n");
        html.append("    .success-box {\n");
        html.append("      background: rgba(63, 185, 80, 0.1);\n");
        html.append("      border: 1px solid var(--accent-green);\n");
        html.append("      border-radius: 8px;\n");
        html.append("      padding: 25px;\n");
        html.append("      text-align: center;\n");
        html.append("      color: var(--accent-green);\n");
        html.append("      font-size: 16px;\n");
        html.append("      font-weight: 500;\n");
        html.append("      margin-top: 20px;\n");
        html.append("    }\n");
        html.append("    .footer {\n");
        html.append("      margin-top: 30px;\n");
        html.append("      padding-top: 15px;\n");
        html.append("      border-top: 1px solid var(--border-color);\n");
        html.append("      font-size: 12px;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("      text-align: right;\n");
        html.append("    }\n");
        html.append("    .charts-row {\n");
        html.append("      display: grid;\n");
        html.append("      grid-template-columns: 1fr 1fr;\n");
        html.append("      gap: 20px;\n");
        html.append("      margin: 25px 0;\n");
        html.append("    }\n");
        html.append("    .chart-card {\n");
        html.append("      background: #0d1117;\n");
        html.append("      border: 1px solid var(--border-color);\n");
        html.append("      border-radius: 10px;\n");
        html.append("      padding: 20px;\n");
        html.append("    }\n");
        html.append("    .chart-card h3 {\n");
        html.append("      margin: 0 0 16px 0;\n");
        html.append("      font-size: 13px;\n");
        html.append("      text-transform: uppercase;\n");
        html.append("      letter-spacing: 0.5px;\n");
        html.append("      color: var(--text-muted);\n");
        html.append("    }\n");
        html.append("    .chart-wrapper { position: relative; height: 220px; }\n");
        html.append("  </style>\n");
        html.append("</head>\n<body>\n");
        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>Reporte Consolidado de Errores de Compilación</h1>\n");
        html.append("    <p class=\"sub\">Detalle de errores léxicos, sintácticos y semánticos detectados en el código PromptZal.</p>\n");
        html.append("  </div>\n");

        // Tarjetas de estadísticas
        html.append("  <div class=\"stats-container\">\n");
        html.append("    <div class=\"stat-card stat-total\"><div class=\"val\">").append(totalErrores).append("</div><div class=\"lbl\">Total de Errores</div></div>\n");
        html.append("    <div class=\"stat-card stat-lex\"><div class=\"val\">").append(cantLexicos).append("</div><div class=\"lbl\">Errores Léxicos</div></div>\n");
        html.append("    <div class=\"stat-card stat-sin\"><div class=\"val\">").append(cantSintacticos).append("</div><div class=\"lbl\">Errores Sintácticos</div></div>\n");
        html.append("    <div class=\"stat-card stat-sem\"><div class=\"val\">").append(cantSemanticos).append("</div><div class=\"lbl\">Errores Semánticos</div></div>\n");
        html.append("  </div>\n");

        // ── GRÁFICAS ──────────────────────────────────────────────────────────
        html.append("  <div class=\"charts-row\">\n");

        // Dona: distribución por tipo
        html.append("    <div class=\"chart-card\">\n");
        html.append("      <h3>Distribución de errores por tipo</h3>\n");
        html.append("      <div class=\"chart-wrapper\"><canvas id=\"chartDona\"></canvas></div>\n");
        html.append("    </div>\n");

        // Barras horizontales: cantidad por fase
        html.append("    <div class=\"chart-card\">\n");
        html.append("      <h3>Errores por fase de análisis</h3>\n");
        html.append("      <div class=\"chart-wrapper\"><canvas id=\"chartBarras\"></canvas></div>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");

        if (totalErrores == 0) {
            html.append("  <div class=\"success-box\">\n");
            html.append("    ✓ ¡Compilación exitosa! No se detectaron errores léxicos, sintácticos ni semánticos en el programa.\n");
            html.append("  </div>\n");
        } else {
            html.append("  <table>\n");
            html.append("    <thead>\n");
            html.append("      <tr>\n");
            html.append("        <th>#</th>\n");
            html.append("        <th>Tipo</th>\n");
            html.append("        <th>Fila</th>\n");
            html.append("        <th>Columna</th>\n");
            html.append("        <th>Token / Lexema Encontrado</th>\n");
            html.append("        <th>Token Esperado</th>\n");
            html.append("        <th>Descripción del Error</th>\n");
            html.append("      </tr>\n");
            html.append("    </thead>\n");
            html.append("    <tbody>\n");

            int contador = 1;

            // 1. Errores Léxicos
            if (erroresLexicos != null) {
                for (ErrorLexico err : erroresLexicos) {
                    html.append("      <tr>\n")
                        .append("        <td>").append(contador++).append("</td>\n")
                        .append("        <td><span class=\"badge badge-lex\">Léxico</span></td>\n")
                        .append("        <td>").append(err.getFila()).append("</td>\n")
                        .append("        <td>").append(err.getColumna()).append("</td>\n")
                        .append("        <td><code class=\"code-pill\">").append(escapeHtml(err.getLexema())).append("</code></td>\n")
                        .append("        <td><span style=\"color: var(--text-muted);\">N/A (Símbolo no reconocido)</span></td>\n")
                        .append("        <td>").append(escapeHtml(err.getDescripcion())).append("</td>\n")
                        .append("      </tr>\n");
                }
            }

            // 2. Errores Sintácticos (con token encontrado y esperado)
            if (erroresSintacticos != null) {
                for (ErrorSintactico err : erroresSintacticos) {
                    String encontrado = err.getTokenEncontrado();
                    String lexema = err.getLexemaEncontrado();
                    String esperado = err.getTipoEsperado();

                    String encontradoStr = encontrado != null ? encontrado : "DESCONOCIDO";
                    if (lexema != null && !lexema.isEmpty() && !lexema.equals(encontradoStr)) {
                        encontradoStr += " ('" + lexema + "')";
                    }

                    html.append("      <tr>\n")
                        .append("        <td>").append(contador++).append("</td>\n")
                        .append("        <td><span class=\"badge badge-sin\">Sintáctico</span></td>\n")
                        .append("        <td>").append(err.getFila()).append("</td>\n")
                        .append("        <td>").append(err.getColumna()).append("</td>\n")
                        .append("        <td><code class=\"code-pill\">").append(escapeHtml(encontradoStr)).append("</code></td>\n")
                        .append("        <td><strong style=\"color: var(--accent-blue);\">").append(escapeHtml(esperado)).append("</strong></td>\n")
                        .append("        <td>").append(escapeHtml(err.getMensaje())).append("</td>\n")
                        .append("      </tr>\n");
                }
            }

            // 3. Errores Semánticos
            if (erroresSemanticos != null) {
                for (ErrorSemantico err : erroresSemanticos) {
                    html.append("      <tr>\n")
                        .append("        <td>").append(contador++).append("</td>\n")
                        .append("        <td><span class=\"badge badge-sem\">Semántico</span></td>\n")
                        .append("        <td>").append(err.getFila()).append("</td>\n")
                        .append("        <td>").append(err.getColumna()).append("</td>\n")
                        .append("        <td><code class=\"code-pill\">").append(escapeHtml(err.getIdentificador())).append("</code></td>\n")
                        .append("        <td><span style=\"color: var(--text-muted);\">N/A (Regla Semántica)</span></td>\n")
                        .append("        <td>").append(escapeHtml(err.getDescripcion())).append("</td>\n")
                        .append("      </tr>\n");
                }
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
        html.append("  const cL = " + cantLexicos + ";\n");
        html.append("  const cS = " + cantSintacticos + ";\n");
        html.append("  const cSem = " + cantSemanticos + ";\n");
        html.append("  const total = cL + cS + cSem;\n");
        html.append("  const chartDefaults = { color: '#c9d1d9' };\n");
        html.append("  Chart.defaults.color = '#8b949e';\n");
        html.append("  Chart.defaults.borderColor = '#30363d';\n");

        // Dona
        html.append("  new Chart(document.getElementById('chartDona'), {\n");
        html.append("    type: 'doughnut',\n");
        html.append("    data: {\n");
        html.append("      labels: ['Léxicos', 'Sintácticos', 'Semánticos'],\n");
        html.append("      datasets: [{\n");
        html.append("        data: [cL, cS, cSem],\n");
        html.append("        backgroundColor: ['rgba(210,153,34,0.8)','rgba(188,140,255,0.8)','rgba(240,136,62,0.8)'],\n");
        html.append("        borderColor: ['#d29922','#bc8cff','#f0883e'],\n");
        html.append("        borderWidth: 2,\n");
        html.append("        hoverOffset: 8\n");
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

        // Barras horizontales
        html.append("  new Chart(document.getElementById('chartBarras'), {\n");
        html.append("    type: 'bar',\n");
        html.append("    data: {\n");
        html.append("      labels: ['Léxicos', 'Sintácticos', 'Semánticos'],\n");
        html.append("      datasets: [{\n");
        html.append("        label: 'Cantidad de errores',\n");
        html.append("        data: [cL, cS, cSem],\n");
        html.append("        backgroundColor: ['rgba(210,153,34,0.5)','rgba(188,140,255,0.5)','rgba(240,136,62,0.5)'],\n");
        html.append("        borderColor: ['#d29922','#bc8cff','#f0883e'],\n");
        html.append("        borderWidth: 2,\n");
        html.append("        borderRadius: 6\n");
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

    public void crearReporte(String rutaArchivo, List<ErrorLexico> lex,
                             List<ErrorSintactico> sin, List<ErrorSemantico> sem) {
        creadorDeArchivos(rutaArchivo, generarHTML(lex, sin, sem));
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
