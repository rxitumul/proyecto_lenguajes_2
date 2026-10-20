package com.mycompany.lenguajesproyecto2.BackEnd.Contenedores;

import java.io.File;

public class ItemArchivo {
    private File archivo;

    public ItemArchivo(File archivo) {
        this.archivo = archivo;
    }

    public File getArchivo() {
        return archivo;
    }

    @Override
    public String toString() {
        // Esto es lo que el JTree muestra visualmente en pantalla
        return archivo.getName();
    }

}
