package com.mycompany.lenguajesproyecto2.frontEnd.Pestana;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Panel de Terminal / Consola de Salida del Compilador PromptZal.
 * Se integra en el jTabbedPane al hacer clic en el botón 'Terminal'
 * y muestra la salida de ejecución, errores de compilación y mensajes del sistema.
 */
public class TerminalPanel extends javax.swing.JPanel {

    private JTextArea areaConsola;
    private JScrollPane scrollPane;
    private JButton btnLimpiar;
    private JLabel lblEstado;

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TerminalPanel(String textoInicial) {
        initComponents();
        if (textoInicial != null && !textoInicial.trim().isEmpty()) {
            imprimir(textoInicial);
        } else {
            imprimirEncabezadoBienvenida();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(13, 17, 23)); // Fondo oscuro estilo consola

        // Barra superior de herramientas de la terminal
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(new Color(22, 27, 34));

        lblEstado = new JLabel("  ● Terminal de Compilación PromptZal");
        lblEstado.setForeground(new Color(88, 166, 255));
        lblEstado.setFont(new Font("Monospaced", Font.BOLD, 12));
        barraSuperior.add(lblEstado, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panelBotones.setBackground(new Color(22, 27, 34));

        btnLimpiar = new JButton("Limpiar Consola");
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setBackground(new Color(33, 38, 45));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnLimpiar.addActionListener(e -> limpiar());
        panelBotones.add(btnLimpiar);

        barraSuperior.add(panelBotones, BorderLayout.EAST);
        add(barraSuperior, BorderLayout.NORTH);

        // Área de texto de la consola
        areaConsola = new JTextArea();
        areaConsola.setBackground(new Color(13, 17, 23));
        areaConsola.setForeground(new Color(240, 246, 252));
        areaConsola.setCaretColor(Color.WHITE);
        areaConsola.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaConsola.setEditable(false);
        areaConsola.setLineWrap(true);
        areaConsola.setWrapStyleWord(true);

        scrollPane = new JScrollPane(areaConsola);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void imprimirEncabezadoBienvenida() {
        String hora = LocalDateTime.now().format(FORMATO_HORA);
        areaConsola.append("┌────────────────────────────────────────────────────────────────────────┐\n");
        areaConsola.append("│  PROMPTZAL IDE - CONSOLA DE COMPILACIÓN [" + hora + "]                   │\n");
        areaConsola.append("│  Listo para compilar. Presione 'Copilar' para iniciar el análisis.    │\n");
        areaConsola.append("└────────────────────────────────────────────────────────────────────────┘\n\n");
    }

    public void limpiar() {
        areaConsola.setText("");
        imprimirEncabezadoBienvenida();
    }

    public void imprimir(String texto) {
        if (texto != null) {
            areaConsola.append(texto + "\n");
            areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
        }
    }

    public void establecerTexto(String texto) {
        areaConsola.setText(texto != null ? texto : "");
        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
    }

    public void imprimirError(String tipo, String mensaje) {
        String hora = LocalDateTime.now().format(FORMATO_HORA);
        areaConsola.append(String.format("[%s] [ERROR %s] %s\n", hora, tipo.toUpperCase(), mensaje));
        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
    }

    public void imprimirExito(String mensaje) {
        String hora = LocalDateTime.now().format(FORMATO_HORA);
        areaConsola.append(String.format("[%s] [EXITO] %s\n", hora, mensaje));
        areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
    }
}
