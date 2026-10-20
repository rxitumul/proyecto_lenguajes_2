package com.mycompany.lenguajesproyecto2.frontEnd;

import javax.swing.JOptionPane;

public class Configuraciones {

    public static void pantallaDeError(String tipoDeError, String descripcionDeError) {
        JOptionPane.showMessageDialog(null, descripcionDeError, tipoDeError, JOptionPane.ERROR_MESSAGE);
    }

    public static void mensajeSi(String tipoDeError, String descripcionDeError) {
        JOptionPane.showMessageDialog(null, descripcionDeError, tipoDeError, JOptionPane.INFORMATION_MESSAGE);
    }
}
