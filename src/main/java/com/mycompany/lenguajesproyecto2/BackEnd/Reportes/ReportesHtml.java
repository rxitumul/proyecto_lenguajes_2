package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.lenguajesproyecto2.frontEnd.Configuraciones;

public class ReportesHtml {
    /**
     * Devuelve el bloque de estilos CSS global compartido por todos los reportes HTML.
     */
    public static String getEstilosGlobales() {
        return """
          :root {
            --bg-dark: #0d1117;
            --bg-card: #161b22;
            --bg-hover: #1f242c;
            --border-color: #30363d;
            --text-main: #c9d1d9;
            --text-muted: #8b949e;
            --accent-blue: #58a6ff;
            --accent-green: #3fb950;
            --accent-purple: #bc8cff;
            --accent-orange: #d29922;
            --accent-red: #f85149;
            --accent-cyan: #39c5cf;
          }
          * { box-sizing: border-box; }
          body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background-color: var(--bg-dark);
            color: var(--text-main);
            margin: 0;
            padding: 30px 20px;
          }
          .container {
            max-width: 1200px;
            margin: 0 auto;
            background: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
          }
          .header {
            border-bottom: 2px solid var(--border-color);
            padding-bottom: 20px;
            margin-bottom: 25px;
          }
          h1 {
            color: var(--accent-blue);
            margin: 0 0 10px 0;
            font-size: 26px;
          }
          p.sub {
            color: var(--text-muted);
            margin: 0;
            font-size: 14px;
          }
          .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 25px;
          }
          .stat-card {
            background: #0d1117;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 15px 20px;
          }
          .stat-card .val {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 4px;
          }
          .stat-card .lbl {
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--text-muted);
          }
          .stat-total .val { color: var(--accent-blue); }
          .stat-error .val { color: var(--accent-red); }
          .stat-agentes .val { color: var(--accent-purple); }
          .stat-vars .val { color: var(--accent-green); }
          .stat-dirs .val { color: var(--accent-cyan); }
          .stat-lex .val { color: var(--accent-orange); }
          .stat-sin .val { color: var(--accent-purple); }
          .stat-sem .val { color: #f0883e; }

          .charts-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin: 25px 0;
          }
          @media (max-width: 800px) {
            .charts-row { grid-template-columns: 1fr; }
          }
          .chart-card {
            background: #0d1117;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 20px;
          }
          .chart-card h3 {
            margin: 0 0 15px 0;
            font-size: 14px;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.5px;
          }
          .chart-wrapper {
            position: relative;
            height: 230px;
          }

          table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
            margin-top: 15px;
          }
          th, td {
            padding: 12px 14px;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
          }
          th {
            background: #21262d;
            color: #f0f6fc;
            text-transform: uppercase;
            font-size: 11px;
            letter-spacing: 0.5px;
          }
          tr:hover {
            background: var(--bg-hover);
          }

          .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
          }
          .badge-lexico, .badge-lex { background: rgba(210, 153, 34, 0.2); color: var(--accent-orange); border: 1px solid rgba(210, 153, 34, 0.4); }
          .badge-sintactico, .badge-sin { background: rgba(188, 140, 255, 0.2); color: var(--accent-purple); border: 1px solid rgba(188, 140, 255, 0.4); }
          .badge-semantico, .badge-sem { background: rgba(240, 136, 62, 0.2); color: #f0883e; border: 1px solid rgba(240, 136, 62, 0.4); }
          .badge-agente { background: rgba(188, 140, 255, 0.2); color: var(--accent-purple); border: 1px solid rgba(188, 140, 255, 0.4); }
          .badge-variable { background: rgba(63, 185, 80, 0.2); color: var(--accent-green); border: 1px solid rgba(63, 185, 80, 0.4); }
          .badge-directiva { background: rgba(57, 197, 207, 0.2); color: var(--accent-cyan); border: 1px solid rgba(57, 197, 207, 0.4); }
          .badge-token { background: rgba(88, 166, 255, 0.2); color: var(--accent-blue); border: 1px solid rgba(88, 166, 255, 0.4); }
          .badge-global { background: rgba(88, 166, 255, 0.15); color: var(--accent-blue); padding: 3px 8px; border-radius: 4px; font-size: 11px; }
          .badge-local { background: rgba(210, 153, 34, 0.15); color: var(--accent-orange); padding: 3px 8px; border-radius: 4px; font-size: 11px; }

          .code-id, .code-pill {
            font-family: 'Consolas', 'Courier New', monospace;
            background: #0d1117;
            border: 1px solid var(--border-color);
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 12px;
          }
          .val-cell {
            font-family: 'Consolas', 'Courier New', monospace;
            color: var(--text-muted);
            max-width: 350px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
          .empty-box {
            background: #0d1117;
            border: 1px dashed var(--border-color);
            border-radius: 8px;
            padding: 30px;
            text-align: center;
            color: var(--text-muted);
          }
          .success-box {
            background: rgba(63, 185, 80, 0.1);
            border: 1px solid var(--accent-green);
            border-radius: 8px;
            padding: 25px;
            text-align: center;
            color: var(--accent-green);
            font-size: 16px;
            font-weight: 500;
            margin-top: 20px;
          }
          .section-title {
            color: #f0f6fc;
            font-size: 18px;
            margin: 30px 0 12px 0;
            display: flex;
            align-items: center;
            gap: 10px;
          }
          .code-block {
            background: #0d1117;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 20px;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.6;
            color: #7ee787;
            white-space: pre;
            overflow-x: auto;
          }
          .dot-block { color: #a5d6ff; }

          .no-data, .no-errors {
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            margin: 20px 0;
            background: rgba(63, 185, 80, 0.1);
            border: 1px solid rgba(63, 185, 80, 0.3);
            color: var(--accent-green);
          }
          .no-errors h2, .no-data h2 {
            margin: 0 0 5px 0;
            font-size: 18px;
          }

          .footer {
            margin-top: 30px;
            padding-top: 15px;
            border-top: 1px solid var(--border-color);
            font-size: 12px;
            color: var(--text-muted);
            text-align: right;
          }
        """;
    }

    /**
     * Genera el encabezado HTML estándar con los estilos globales y opcionalmente Chart.js.
     */
    public static void appendHead(StringBuilder html, String titulo, boolean incluirChartJs) {
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>").append(titulo).append("</title>\n");
        if (incluirChartJs) {
            html.append("  <script src=\"https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js\"></script>\n");
        }
        html.append("  <style>\n");
        html.append(getEstilosGlobales());
        html.append("  </style>\n");
        html.append("</head>\n<body>\n");
    }

    /**
     * Genera el pie de página HTML estándar.
     */
    public static void appendFooter(StringBuilder html, String nombreReporte) {
        html.append("  <div class=\"footer\">\n");
        html.append("    <span>Compilador PromptZal &bull; ").append(nombreReporte).append("</span>\n");
        html.append("  </div>\n");
        html.append("</div>\n</body>\n</html>\n");
    }

    public void creadorDeArchivos(String phatCarpeta, StringBuilder html) {
        File destFile = new File(phatCarpeta);
        if (destFile.getParentFile() != null) {
            destFile.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(destFile)) {
            writer.write(html.toString());
        } catch (IOException e) {
            Configuraciones.pantallaDeError("HTML", "Error al escribir el reporte HTML: ");
        }
    }
}
