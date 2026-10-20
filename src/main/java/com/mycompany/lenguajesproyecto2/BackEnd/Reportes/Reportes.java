package com.mycompany.lenguajesproyecto2.BackEnd.Reportes;

import java.util.ArrayList;

import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas.ErrorLexico;


public class Reportes {
    
private ArrayList<RegistroDeTokens> reportesTokensValidos;
private ArrayList<ErrorLexico> reportesTokensNoValidos;

    public Reportes() {
        reportesTokensValidos = new ArrayList<>();
        reportesTokensNoValidos = new ArrayList<>();
    }

    public void agregarReporteValido(RegistroDeTokens reporte) {
        reportesTokensValidos.add(reporte);
    }

    public void agregarReporteNoValido(ErrorLexico reporte) {
        reportesTokensNoValidos.add(reporte);
    }

    public void mostrarReportesValidos() {
        for (RegistroDeTokens reporte : reportesTokensValidos) {
            System.out.println(reporte);
        }
    }

    public void mostrarReportesNoValidos() {
        for (ErrorLexico reporte : reportesTokensNoValidos) {
            System.out.println(reporte);
        }
    }

}
