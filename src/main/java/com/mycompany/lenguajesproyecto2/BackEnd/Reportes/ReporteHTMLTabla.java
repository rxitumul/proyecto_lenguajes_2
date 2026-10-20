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
        html.append("<!DOCTYPE html>\n<html>\n<head>\n <meta charset=\"UTF-8\">\n");
        html.append("<title>Reporte de Tokens</title>\n");
        html.append("<style>\n");
        html.append(
                "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0d1117; color: #c9d1d9; margin: 0; padding: 30px; }\n");
        html.append(
                "  .container { max-width: 1100px; margin: auto; background: #161b22; padding: 25px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,0.6); border: 1px solid #30363d; }\n");
        html.append(
                "  h1 { color: #58a6ff; border-bottom: 2px solid #30363d; padding-bottom: 10px; margin-top: 0; font-size: 24px; }\n");
        html.append("  table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 14px; }\n");
        html.append("  th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #30363d; }\n");
        html.append(
                "  th { background-color: #1f6feb; color: #ffffff; font-weight: 600; text-transform: uppercase; font-size: 12px; letter-spacing: 0.5px; }\n");
        html.append("  td { color: #b1bac4; }\n");
        html.append("  tr:hover { background-color: #21262d; }\n");
        html.append(
                "  .no-data { padding: 20px; background-color: #0f2b1d; color: #3fb950; border-left: 4px solid #2ea043; border-radius: 4px; font-weight: 500; margin-top: 20px; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        html.append("<div class=\"container\">\n");
        html.append("<h1>Reporte de Tokens</h1>\n");

        if (listaDeTokens.esVacia()) {
            html.append(
                    "<p style='color: green;'><strong>No se encontraron tokens durante el análisis.</strong></p>\n");
        } else {
            html.append("<table>\n");
            html.append(
                    "<tr><th>#</th><th>Lexema / Carácter</th><th>Tipo</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>\n");
            int contador = 1;
            for (int i = 0; i < listaDeTokens.getCapacidad(); i++) {
                try {
                    RegistroDeTokens token = listaDeTokens.obtenerContenido(i);
                    html.append("<tr>")
                            .append("<td>").append(contador++).append("</td>")
                            .append("<td>").append(token.getLexema()).append("</td>")
                            .append("<td>").append(token.getTipo()).append("</td>")
                            .append("<td>").append(token.getDescripcion()).append("</td>")
                            .append("<td>").append(token.getFila()).append("</td>")
                            .append("<td>").append(token.getColumna()).append("</td>")
                            .append("</tr>\n");
                } catch (Exception e) {
                    System.err.println("Error al leer token de la lista: " + e.getMessage());
                }
            }
            html.append("</table>\n");
        }

        html.append("</body>\n</html>");
        html.append("</div>\n</body>\n</html>");

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
