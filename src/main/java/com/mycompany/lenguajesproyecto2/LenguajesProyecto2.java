/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.lenguajesproyecto2;

import com.mycompany.lenguajesproyecto2.frontEnd.PaginaPrincipal.MenuEditor;

/**
 *
 * @author ricardocastillo
 */
public class LenguajesProyecto2 {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        java.awt.EventQueue.invokeLater(() -> {
            MenuEditor menu = new MenuEditor();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
        });
    }
}
