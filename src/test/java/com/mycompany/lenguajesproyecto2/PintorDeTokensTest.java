package com.mycompany.lenguajesproyecto2;

import java.awt.Color;
import java.io.StringReader;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.PromptzalLexer;
import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.TokenEntidad;
import com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta.BibliotecaDeTokens;
import com.mycompany.lenguajesproyecto2.frontEnd.MetodosDeAyudaGrafica.PintorDeTokens;

import static org.junit.jupiter.api.Assertions.*;

public class PintorDeTokensTest {

    @Test
    @DisplayName("El lexer de JFlex genera todos los tokens y separa correctamente los útiles de los de coloreado")
    public void testLexerGeneraTodosLosTokens() throws Exception {
        String codigo = """
            @modelo "gpt-4"
            // Comentario de una línea
            AGENTE Bot {
                contexto = "asistente"
                variable x = 10
                RESUMIR x -> salida
            }
            EJECUTAR Bot
            EXPORTAR salida
            """;

        PromptzalLexer lexer = new PromptzalLexer(new StringReader(codigo));
        while (lexer.yylex() != null) {
            // Escaneo secuencial
        }

        List<TokenEntidad> todosLosTokens = lexer.getTodosLosTokens();
        List<TokenEntidad> tokensUtiles = lexer.getListaDeTokens();

        assertNotNull(todosLosTokens, "La lista completa no debe ser nula");
        assertNotNull(tokensUtiles, "La lista de útiles no debe ser nula");
        assertTrue(todosLosTokens.size() > tokensUtiles.size(),
                "La lista total debe contener espacios y comentarios además de tokens útiles");

        boolean contieneEspacio = todosLosTokens.stream().anyMatch(t -> "ESPACIO".equals(t.getTipo()));
        boolean contieneComentario = todosLosTokens.stream().anyMatch(t -> "COMENTARIO".equals(t.getTipo()));
        assertTrue(contieneEspacio, "Debe contener tokens de espacio");
        assertTrue(contieneComentario, "Debe contener tokens de comentario");

        // Ningún espacio ni comentario en la lista útil para el parser
        boolean utilSinEspacio = tokensUtiles.stream().noneMatch(t -> "ESPACIO".equals(t.getTipo()));
        boolean utilSinComentario = tokensUtiles.stream().noneMatch(t -> "COMENTARIO".equals(t.getTipo()));
        assertTrue(utilSinEspacio, "Lista útil no debe contener espacios");
        assertTrue(utilSinComentario, "Lista útil no debe contener comentarios");
    }

    @Test
    @DisplayName("PintorDeTokens clasifica adecuadamente las familias de palabras reservadas y tokens")
    public void testClasificacionFamilias() {
        BibliotecaDeTokens bib = new BibliotecaDeTokens();

        TokenEntidad tokDir = new TokenEntidad("DIRECTIVA", "@modelo", 1, 1);
        assertEquals("DIRECTIVA", PintorDeTokens.clasificarFamilia(tokDir, bib));

        TokenEntidad tokAgente = new TokenEntidad("AGENTE", "AGENTE", 2, 1);
        assertEquals("PALABRA_RESERVADA", PintorDeTokens.clasificarFamilia(tokAgente, bib));

        TokenEntidad tokVar = new TokenEntidad("VARIABLE", "variable", 3, 1);
        assertEquals("PALABRA_RESERVADA", PintorDeTokens.clasificarFamilia(tokVar, bib));

        TokenEntidad tokComando = new TokenEntidad("COMANDO_IA", "RESUMIR", 4, 1);
        assertEquals("COMANDO_IA", PintorDeTokens.clasificarFamilia(tokComando, bib));

        TokenEntidad tokConector = new TokenEntidad("CONECTOR", "SOBRE", 5, 1);
        assertEquals("CONECTOR", PintorDeTokens.clasificarFamilia(tokConector, bib));

        TokenEntidad tokFlecha = new TokenEntidad("FLECHA", "->", 5, 10);
        assertEquals("CONECTOR", PintorDeTokens.clasificarFamilia(tokFlecha, bib));

        TokenEntidad tokCargar = new TokenEntidad("CARGAR", "CARGAR", 6, 1);
        assertEquals("FUNCION_DE_SISTEMA", PintorDeTokens.clasificarFamilia(tokCargar, bib));

        TokenEntidad tokCadena = new TokenEntidad("CADENA", "\"texto prueba\"", 7, 1);
        assertEquals("CADENA", PintorDeTokens.clasificarFamilia(tokCadena, bib));

        TokenEntidad tokNumero = new TokenEntidad("NUMERO", "42.5", 8, 1);
        assertEquals("NUMERO", PintorDeTokens.clasificarFamilia(tokNumero, bib));

        TokenEntidad tokId = new TokenEntidad("ID", "miVariable", 9, 1);
        assertEquals("ID", PintorDeTokens.clasificarFamilia(tokId, bib));

        TokenEntidad tokComent = new TokenEntidad("COMENTARIO", "// nota", 10, 1);
        assertEquals("COMENTARIO", PintorDeTokens.clasificarFamilia(tokComent, bib));

        TokenEntidad tokError = new TokenEntidad("ERROR_LEXICO", "§", 11, 1);
        assertEquals("ERROR_LEXICO", PintorDeTokens.clasificarFamilia(tokError, bib));
    }

    @Test
    @DisplayName("PintorDeTokens reescribe el JTextPane coloreando por familias")
    public void testReescribirYColorearEditor() {
        String codigo = """
            @modelo "claude-3"
            AGENTE Analista {
                variable entrada = "datos"
                ANALIZAR entrada -> salida
            }
            EJECUTAR Analista
            EXPORTAR salida
            """;

        JTextPane textPane = new JTextPane();
        textPane.setText(codigo);

        PintorDeTokens.colorearEditor(textPane);

        StyledDocument doc = textPane.getStyledDocument();
        assertNotNull(doc, "El StyledDocument no debe ser nulo");
        assertTrue(doc.getLength() > 0, "El documento debe contener texto");

        // Verificar que el texto plano obtenido coincide con el original
        String textoResultado = textPane.getText();
        assertTrue(textoResultado.contains("@modelo"));
        assertTrue(textoResultado.contains("AGENTE"));
        assertTrue(textoResultado.contains("ANALIZAR"));
        assertTrue(textoResultado.contains("salida"));

        // Verificar que hay atributos de color aplicados en los primeros caracteres (@modelo)
        AttributeSet attrs = doc.getCharacterElement(0).getAttributes();
        Color colorDirectiva = StyleConstants.getForeground(attrs);
        assertEquals(PintorDeTokens.COLOR_DIRECTIVA, colorDirectiva,
                "El token @modelo debe tener el color de la familia DIRECTIVA");
    }

    @Test
    @DisplayName("MenuEditor se inicializa correctamente y soporta las pestañas Terminal y Lenguaje Promptzal")
    public void testMenuEditorInicializacion() {
        assertDoesNotThrow(() -> {
            com.mycompany.lenguajesproyecto2.frontEnd.PaginaPrincipal.MenuEditor editor =
                new com.mycompany.lenguajesproyecto2.frontEnd.PaginaPrincipal.MenuEditor();
            assertNotNull(editor);
            assertNotNull(editor.getContentPane());

            // Apertura de pestaña Terminal y escritura de mensajes
            editor.mostrarPestanaTerminal("Mensaje de prueba en la Terminal");
            editor.mostrarPestanaPromptzal("# Prompt generado con PromptZal");
        });
    }
}
