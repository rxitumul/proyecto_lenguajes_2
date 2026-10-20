package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

public class BibliotecaDeReporteDeUsoDeTokensHtml {

    private int directivas;
    private int reservadas;
    private int conectores;
    private int comandoIA;
    private int errores;
    private int funcionSistema;
    private int identificadores;

    public BibliotecaDeReporteDeUsoDeTokensHtml() {
        directivas = 0;
        reservadas = 0;
        identificadores = 0;
        conectores = 0;
        comandoIA = 0;
        errores = 0;
    }

    public StringBuilder generarReporteEstadistico() {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n<html>\n<head>\n <meta charset=\"UTF-8\">\n");
        html.append("<title>Reporte Estadístico - PromptZal</title>\n");
        html.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");

        html.append("<style>\n");
        html.append(
                "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0d1117; color: #c9d1d9; margin: 0; padding: 30px; }\n");
        html.append(
                "  .container { max-width: 1100px; margin: auto; background: #161b22; padding: 25px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,0.6); border: 1px solid #30363d; }\n");
        html.append(
                "  h1 { color: #58a6ff; border-bottom: 2px solid #30363d; padding-bottom: 10px; margin-top: 0; font-size: 24px; text-align: center; }\n");
        html.append(
                "  .chart-container { width: 75%; margin: 30px auto; background-color: #161b22; padding: 20px; border-radius: 8px; border: 1px solid #30363d; }\n");
        html.append("</style>\n");

        html.append("</head>\n<body>\n");
        html.append("<div class=\"container\">\n");
        html.append("<h1>Reporte Estadístico de Análisis</h1>\n");

        html.append("<div class=\"chart-container\">\n");
        html.append("<canvas id=\"estadisticasChart\"></canvas>\n");
        html.append("</div>\n");

        html.append("<script>\n");
        html.append("  const ctx = document.getElementById('estadisticasChart').getContext('2d');\n");
        html.append("  const estadisticasChart = new Chart(ctx, {\n");
        html.append("      type: 'bar',\n");
        html.append("      data: {\n");
        html.append(
                "          labels: ['Directivas', 'Palabras Reservadas', 'Identificadores', 'Conectores', 'Conectores IA','Función de sistema', 'Errores Léxicos'],\n");
        html.append("          datasets: [{\n");
        html.append("              label: 'Cantidad de Elementos',\n");

        // AQUÍ SE INYECTAN DINÁMICAMENTE LOS NÚMEROS DESDE JAVA
        html.append("              data: [")
                .append(directivas).append(", ")
                .append(reservadas).append(", ")
                .append(identificadores).append(", ")
                .append(conectores).append(", ")
                .append(comandoIA).append(", ")
                .append(funcionSistema).append(", ")
                .append(errores)
                .append("],\n");

        html.append("              backgroundColor: [\n");
        html.append("                  'rgba(31, 111, 235, 0.7)',\n"); // Azul
        html.append("                  'rgba(46, 160, 67, 0.7)',\n"); // Verde
        html.append("                  'rgba(187, 128, 255, 0.7)',\n"); // Morado
        html.append("                  'rgba(210, 153, 34, 0.7)',\n"); // Naranja
        html.append("                  'rgba(88, 166, 255, 0.7)',\n"); // Celeste
        html.append("                  'rgba(248, 81, 73, 0.7)'\n"); // Rojo (Errores)
        html.append("              ],\n");
        html.append("              borderColor: [\n");
        html.append("                  '#1f6feb', '#2ea043', '#bb80ff', '#d29922', '#58a6ff', '#f85149'\n");
        html.append("              ],\n");
        html.append("              borderWidth: 1\n");
        html.append("          }]\n");
        html.append("      },\n");
        html.append("      options: {\n");
        html.append("          responsive: true,\n");
        html.append("          plugins: {\n");
        html.append("              legend: {\n");
        html.append("                  labels: { color: '#c9d1d9', font: { family: 'Segoe UI' } }\n");
        html.append("              }\n");
        html.append("          },\n");
        html.append("          scales: {\n");
        html.append("              y: { ticks: { color: '#8b949e' }, grid: { color: '#30363d' } },\n");
        html.append("              x: { ticks: { color: '#8b949e' }, grid: { color: '#30363d' } }\n");
        html.append("          }\n");
        html.append("      }\n");
        html.append("  });\n");
        html.append("</script>\n");

        html.append("</div>\n</body>\n</html>");

        return html;
    }

    public int getConectores() {
        return conectores;
    }

    public int getComandoIA() {
        return comandoIA;
    }

    public int getFuncionSistema() {
        return funcionSistema;
    }

    public int getDirectivas() {
        return directivas;
    }

    public int getErrores() {
        return errores;
    }

    public int getIdentificadores() {
        return identificadores;
    }

    public int getReservadas() {
        return reservadas;
    }

    public void setFuncionSistema(int funcionSistema) {
        this.funcionSistema = funcionSistema;
    }

    public void setConectores(int conectores) {
        this.conectores = conectores;
    }

    public void setComandoIA(int comandoIA) {
        this.comandoIA = comandoIA;
    }

    public void setDirectivas(int directivas) {
        this.directivas = directivas;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public void setIdentificadores(int identificadores) {
        this.identificadores = identificadores;
    }

    public void setReservadas(int reservadas) {
        this.reservadas = reservadas;
    }
}
