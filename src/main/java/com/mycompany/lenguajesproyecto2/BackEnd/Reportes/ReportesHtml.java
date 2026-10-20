package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.lenguajesproyecto2.frontEnd.Configuraciones;

public class ReportesHtml {
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
