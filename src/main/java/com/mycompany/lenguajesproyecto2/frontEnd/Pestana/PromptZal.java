package com.mycompany.lenguajesproyecto2.frontEnd.Pestana;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Panel de visualización del PromptZal final generado por el compilador.
 * Cumple con el requisito 4 del enunciado:
 * "Un panel del promptzal donde se muestra el prompt generado, con opción de guardarlo
 * como archivo .md o .txt."
 */
public class PromptZal extends javax.swing.JPanel {

    private JButton btnGuardarPrompt;

    public PromptZal(String texto) {
        initComponents();
        agregarBotonGuardar();
        jTextArea1.setText(texto);
    }

    public void actualizarContenido(String texto) {
        jTextArea1.setText(texto);
        jTextArea1.setCaretPosition(0);
    }

    private void agregarBotonGuardar() {
        JPanel panelBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBarra.setBackground(new Color(17, 30, 108));

        btnGuardarPrompt = new JButton("Guardar Prompt (.md / .txt)");
        btnGuardarPrompt.setForeground(Color.WHITE);
        btnGuardarPrompt.setBackground(new Color(31, 111, 235));
        btnGuardarPrompt.setFocusPainted(false);
        btnGuardarPrompt.addActionListener(e -> guardarArchivoPrompt());

        panelBarra.add(btnGuardarPrompt);
        add(panelBarra, BorderLayout.NORTH);
    }

    private void guardarArchivoPrompt() {
        String contenido = jTextArea1.getText();
        if (contenido == null || contenido.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay contenido de prompt generado para guardar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar PromptZal");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivo Markdown (*.md)", "md"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivo de Texto (*.txt)", "txt"));
        fileChooser.setSelectedFile(new File("prompt_final.md"));

        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(archivo)) {
                fw.write(contenido);
                JOptionPane.showMessageDialog(this,
                        "Prompt guardado exitosamente en:\n" + archivo.getAbsolutePath(),
                        "Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el archivo: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(29, 41, 81));
        setToolTipText("");
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(29, 41, 81));

        jTextArea1.setBackground(new java.awt.Color(29, 41, 81));
        jTextArea1.setColumns(20);
        jTextArea1.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea1.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        jTextArea1.setRows(5);
        jTextArea1.setEditable(false);
        jScrollPane1.setViewportView(jTextArea1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
}
