package com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BibliotecaDeDot {
    private StringBuilder archivoOdt;
    private Set<String> transicionesRegistradas;
    private String phatImagen;

    public BibliotecaDeDot() {

        this.archivoOdt = new StringBuilder();
        this.transicionesRegistradas = new HashSet<>();
        inicializarEstructuraBase();
    }

    public String getPhatImagen() {
        return phatImagen;
    }

    private void inicializarEstructuraBase() {
        archivoOdt.append("digraph DespachadorPromptZal {\n");
        archivoOdt.append("  rankdir=TB;\n");
        archivoOdt.append("  node [shape = circle, fontname = \"Segoe UI\", fontsize = 12];\n");
        archivoOdt.append("  node [shape = point, width = 0.2]; qi;\n");
        // autómata Separador
        archivoOdt.append("  node [shape = circle]; q0;\n");
        archivoOdt.append("  node [shape = circle]; q1;\n");
        archivoOdt.append("  node [shape = doublecircle]; q2;\n");

        // Sub-autómata Directivas
        archivoOdt.append("  q0_dir [label=\"q0_dir\\n(Directivas)\"];\n");
        archivoOdt.append("  q1_dir [shape = circle];\n");
        archivoOdt.append("  q2_dir [shape = circle];\n");
        archivoOdt.append("  q3_dir [shape = doublecircle];\n");

        // Sub-autómata Palabras / IDs
        archivoOdt.append("  q0_pal [label=\"q0_pal\\n(Palabras/IDs)\"];\n");
        archivoOdt.append("  q1_pal [shape = doublecircle];\n");
        archivoOdt.append("  q2_pal [shape = circle];\n");
        archivoOdt.append("  q3_pal [shape = doublecircle];\n");
        archivoOdt.append("  q4_pal [shape = circle];\n");
        archivoOdt.append("  q5_pal [shape = doublecircle];\n");
        archivoOdt.append("  q6_pal [shape = doublecircle];\n");
        archivoOdt.append("  q7_pal [shape = doublecircle];\n");

        // Sub-autómata Conectores
        archivoOdt.append("  q0_con [label=\"q0_con\\n(Conectores)\"];\n");
        archivoOdt.append("  q1_con [shape = circle];\n");
        archivoOdt.append("  q2_con [shape = circle];\n");
        archivoOdt.append("  q3_con [shape = circle];\n");
        archivoOdt.append("  q4_con [shape = doublecircle];\n");

        // Sub-autómata Conectores IA
        archivoOdt.append("  q0_conIA [label=\"q0_conIA\\n(Conectores IA)\"];\n");
        archivoOdt.append("  q1_conIA [shape = circle];\n");
        archivoOdt.append("  q2_conIA [shape = circle];\n");
        archivoOdt.append("  q3_conIA [shape = circle];\n");
        archivoOdt.append("  q4_conIA [shape = doublecircle];\n");

        // Enlace inicial
        archivoOdt.append("  qi -> q0;\n");
    }

    /**
     * Agrega una transición dinámicamente solo si no ha sido registrada antes,
     * evitando saturar el archivo con líneas repetidas (como bucles de letras).
     */
    public void agregarTransicion(String origen, String destino, String etiqueta) {
        String lineaTransicion = String.format("  %s -> %s [label = \"%s\"];\n", origen, destino, etiqueta);

        if (transicionesRegistradas.add(lineaTransicion)) {
            archivoOdt.append(lineaTransicion);
        }
    }

    /**
     * Sobrecarga por si una transición no lleva etiqueta explicita.
     */
    public void agregarTransicion(String origen, String destino) {
        String lineaTransicion = String.format("  %s -> %s;\n", origen, destino);

        if (transicionesRegistradas.add(lineaTransicion)) {
            archivoOdt.append(lineaTransicion);
        }
    }

    public StringBuilder obtenerCodigoDot() {
        StringBuilder dotFinal = new StringBuilder(archivoOdt.toString());
        dotFinal.append("}\n");
        return dotFinal;
    }

    public boolean generarJpgDesdeDot(String rutaDot) {
        try {
            File archivoDot = new File(rutaDot);
            File directorioPadre = archivoDot.getParentFile();

            String rutaSalidaImg = (directorioPadre != null ? directorioPadre.getAbsolutePath() : "")
                    + File.separator + "afd.png";

            ProcessBuilder procesoBuilder = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaSalidaImg);

            procesoBuilder.redirectErrorStream(true);

            Process proceso = procesoBuilder.start();

            int codigoSalida = proceso.waitFor();
            phatImagen = rutaSalidaImg;
            return codigoSalida == 0;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
