package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import javax.swing.table.DefaultTableModel;

import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas.ErrorLexico;
import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas.Listas;

public class ReporteDeError extends ReportesHtml {

    private Listas<ErrorLexico> listaDeErrores = new Listas<>();

    public int getTamano() {
        return listaDeErrores.getCapacidad();
    }

    // Método para agregar errores durante el análisis
    public void registrarError(ErrorLexico errorLexico) {
        listaDeErrores.agregarAlFinal(errorLexico);
    }

    // Generar el archivo HTML al terminar la lectura del documento
    public StringBuilder generarHTMLDeError() {
        StringBuilder html = new StringBuilder();
        appendHead(html, "PromptZal - Reporte de Errores Léxicos", false);

        html.append("<div class=\"container\">\n");
        html.append("  <div class=\"header\">\n");
        html.append("    <h1>Reporte de Errores Léxicos</h1>\n");
        html.append("    <p class=\"sub\">Detalle de caracteres o patrones léxicos no reconocidos.</p>\n");
        html.append("  </div>\n");

        int total = listaDeErrores.getCapacidad();
        html.append("  <div class=\"stats-container\">\n");
        html.append("    <div class=\"stat-card stat-error\">\n");
        html.append("      <div class=\"val\">").append(total).append("</div>\n");
        html.append("      <div class=\"lbl\">Total Errores Léxicos</div>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");

        if (listaDeErrores.esVacia()) {
            html.append("  <div class=\"success-box\">\n");
            html.append("    <h2>¡Sin errores léxicos!</h2>\n");
            html.append("    <p>No se encontraron errores léxicos durante el análisis.</p>\n");
            html.append("  </div>\n");
        } else {
            html.append("  <table>\n");
            html.append("    <thead>\n");
            html.append("      <tr><th>#</th><th>Lexema / Carácter</th><th>Descripción del Error</th><th>Fila</th><th>Columna</th></tr>\n");
            html.append("    </thead>\n");
            html.append("    <tbody>\n");
            int contador = 1;
            for (int i = 0; i < listaDeErrores.getCapacidad(); i++) {
                try {
                    ErrorLexico err = listaDeErrores.obtenerContenido(i);
                    html.append("      <tr>")
                            .append("<td>").append(contador++).append("</td>")
                            .append("<td><span class=\"code-id\">").append(err.getLexema().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")).append("</span></td>")
                            .append("<td>").append(err.getDescripcion()).append("</td>")
                            .append("<td>").append(err.getFila()).append("</td>")
                            .append("<td>").append(err.getColumna()).append("</td>")
                            .append("</tr>\n");
                } catch (Exception e) {
                    System.err.println("Error al leer elemento de la lista: " + e.getMessage());
                }
            }
            html.append("    </tbody>\n");
            html.append("  </table>\n");
        }

        appendFooter(html, "Reporte de Errores Léxicos");
        return html;
    }

    public DefaultTableModel tablaDeTokensConsola(DefaultTableModel tabla) {
        for (int i = 0; i < listaDeErrores.getCapacidad(); i++) {
            try {
                ErrorLexico token = listaDeErrores.obtenerContenido(i);
                tabla.addRow(
                        new Object[] { i, token.getLexema(), token.getDescripcion(), token.getFila(),
                                token.getColumna() });
            } catch (Exception e) {
                System.err.println("Error al imprimir token: " + e.getMessage());
            }
        }
        return tabla;
    }
}
