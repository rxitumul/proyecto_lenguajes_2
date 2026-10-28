package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import javax.swing.table.DefaultTableModel;

import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas.Listas;

public class ReporteHTMLTabla extends ReportesHtml {

    private Listas<RegistroDeTokens> listaDeTokens = new Listas<>();

    public int getTamano() {
        return listaDeTokens.getCapacidad();
    }

    // Método para agregar errores durante el análisis
    public void registroDeTokens(RegistroDeTokens registroDeTokens) {
        listaDeTokens.agregarAlFinal(registroDeTokens);
    }

    // Generar el archivo HTML al terminar la lectura del documento
    public StringBuilder generarHTMLDeTokens() {
        StringBuilder html = new StringBuilder();
        appendHead(html, "PromptZal - Reporte de Tokens", false);

        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>Reporte de Tokens</h1>\n");
        html.append("    <p class=\"sub\">Listado de todos los componentes léxicos identificados durante la lectura.</p>\n");
        html.append("  </div>\n");

        int total = listaDeTokens.getCapacidad();
        html.append("  <div class=\"stats-container\">\n");
        html.append("    <div class=\"stat-card stat-total\">\n");
        html.append("      <div class=\"val\">").append(total).append("</div>\n");
        html.append("      <div class=\"lbl\">Total Tokens Procesados</div>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");

        if (listaDeTokens.esVacia()) {
            html.append("  <div class=\"no-data\">\n");
            html.append("    <h2>Sin tokens</h2>\n");
            html.append("    <p>No se encontraron tokens durante el análisis.</p>\n");
            html.append("  </div>\n");
        } else {
            html.append("  <table>\n");
            html.append("    <thead>\n");
            html.append("      <tr><th>#</th><th>Lexema / Carácter</th><th>Tipo</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>\n");
            html.append("    </thead>\n");
            html.append("    <tbody>\n");
            int contador = 1;
            for (int i = 0; i < listaDeTokens.getCapacidad(); i++) {
                try {
                    RegistroDeTokens token = listaDeTokens.obtenerContenido(i);
                    html.append("      <tr>")
                            .append("<td>").append(contador++).append("</td>")
                            .append("<td><span class=\"code-id\">").append(token.getLexema().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")).append("</span></td>")
                            .append("<td><span class=\"badge badge-token\">").append(token.getTipo()).append("</span></td>")
                            .append("<td>").append(token.getDescripcion()).append("</td>")
                            .append("<td>").append(token.getFila()).append("</td>")
                            .append("<td>").append(token.getColumna()).append("</td>")
                            .append("</tr>\n");
                } catch (Exception e) {
                    System.err.println("Error al leer token de la lista: " + e.getMessage());
                }
            }
            html.append("    </tbody>\n");
            html.append("  </table>\n");
        }

        appendFooter(html, "Reporte de Tokens");
        return html;
    }

    public DefaultTableModel tablaDeTokensConsola(DefaultTableModel tabla) {
        for (int i = 0; i < listaDeTokens.getCapacidad(); i++) {
            try {
                RegistroDeTokens token = listaDeTokens.obtenerContenido(i);
                tabla.addRow(
                        new Object[] { i, token.getLexema(), token.getTipo(), token.getDescripcion(), token.getFila(),
                                token.getColumna() });
            } catch (Exception e) {
                System.err.println("Error al imprimir token: " + e.getMessage());
            }
        }
        return tabla;
    }

}
