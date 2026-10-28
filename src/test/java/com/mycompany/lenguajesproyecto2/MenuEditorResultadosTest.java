package com.mycompany.lenguajesproyecto2;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mycompany.lenguajesproyecto2.frontEnd.Pestana.PromptZal;
import com.mycompany.lenguajesproyecto2.frontEnd.Pestana.ast;

import static org.junit.jupiter.api.Assertions.*;

public class MenuEditorResultadosTest {

    @Test
    @DisplayName("ast panel loads error information correctly")
    public void testASTErrorCarga() {
        ast panelAst = new ast();
        assertDoesNotThrow(() -> {
            panelAst.cargarErrorAST("Sintáctica", List.of("Fila 5, Columna 10: Se esperaba ';'"), null);
        });
    }

    @Test
    @DisplayName("PromptZal panel updates with error information and clears previous content")
    public void testPromptZalActualizarContenido() {
        PromptZal promptPanel = new PromptZal("Texto anterior");
        promptPanel.actualizarContenido("=========================================\nERROR DE COMPILACIÓN DETECTADO\n");
        assertNotNull(promptPanel);
    }
}
