package com.mycompany.lenguajesproyecto2.frontEnd.MetodosDeAyudaGrafica;

import java.awt.Color;
import java.io.StringReader;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.PromptzalLexer;
import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.TokenEntidad;
import com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta.BibliotecaDeTokens;

/**
 * Gestor de pintado y sintaxis coloreada para el editor de PromptZal.
 * Utiliza la secuencia y arreglo de tokens generado por JFlex para reescribir
 * el contenido del JTextPane asignando el color correspondiente a cada familia
 * de palabras reservadas, directivas, comandos, conectores y literales.
 */
public class PintorDeTokens {

    // Paleta de colores armoniosa con temática oscura (Dark Modern Theme)
    public static final Color COLOR_DIRECTIVA = new Color(88, 166, 255);       // Celeste / Azul cielo
    public static final Color COLOR_RESERVADA = new Color(210, 168, 255);      // Violeta / Púrpura suave
    public static final Color COLOR_COMANDO_IA = new Color(255, 166, 87);      // Naranja cálido
    public static final Color COLOR_CONECTOR = new Color(227, 179, 65);        // Ámbar / Dorado
    public static final Color COLOR_FUNCION = new Color(247, 120, 186);        // Rosa / Fucsia
    public static final Color COLOR_CADENA = new Color(126, 231, 135);         // Verde menta
    public static final Color COLOR_NUMERO = new Color(121, 192, 255);         // Azul hielo / Cian
    public static final Color COLOR_ID = new Color(240, 246, 252);             // Blanco perla
    public static final Color COLOR_SIMBOLO = new Color(201, 209, 217);        // Gris claro
    public static final Color COLOR_COMENTARIO = new Color(139, 148, 158);     // Gris medio
    public static final Color COLOR_ERROR = new Color(255, 123, 114);          // Rojo vibrante

    private static final BibliotecaDeTokens BIBLIOTECA_DEFAULT = new BibliotecaDeTokens();

    /**
     * Determina la familia a la que pertenece un token generado por JFlex.
     */
    public static String clasificarFamilia(TokenEntidad token, BibliotecaDeTokens biblioteca) {
        if (token == null) {
            return "DEFECTO";
        }
        String tipo = token.getTipo();
        String lexema = token.getDescripcion();

        if ("ESPACIO".equals(tipo)) return "ESPACIO";
        if ("COMENTARIO".equals(tipo)) return "COMENTARIO";
        if ("ERROR_LEXICO".equals(tipo)) return "ERROR_LEXICO";
        if ("CADENA".equals(tipo)) return "CADENA";
        if ("NUMERO".equals(tipo)) return "NUMERO";
        if ("EOF".equals(tipo)) return "EOF";

        if ("DIRECTIVA".equals(tipo) || (lexema != null && lexema.startsWith("@"))) {
            return "DIRECTIVA";
        }

        BibliotecaDeTokens bib = biblioteca != null ? biblioteca : BIBLIOTECA_DEFAULT;

        if (lexema != null && bib.existeEnLosTokens(lexema)) {
            String cat = bib.mapeadorDeTokens(lexema);
            switch (cat) {
                case "DIRECTIVA":
                    return "DIRECTIVA";
                case "PALABRA_RESERVADA":
                    return "PALABRA_RESERVADA";
                case "COMANDO_IA":
                    return "COMANDO_IA";
                case "CONECTORES":
                    return "CONECTOR";
                case "FUNCION_DE_SISTEMA":
                    return "FUNCION_DE_SISTEMA";
                case "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES":
                    return "OPERADOR_SIMBOLO";
                default:
                    break;
            }
        }

        if ("AGENTE".equals(tipo) || "CONTEXTO".equals(tipo) || "VARIABLE".equals(tipo)
                || "EJECUTAR".equals(tipo) || "EXPORTAR".equals(tipo) || "PALABRA_RESERVADA".equals(tipo)) {
            return "PALABRA_RESERVADA";
        }
        if ("COMANDO_IA".equals(tipo)) return "COMANDO_IA";
        if ("CONECTOR".equals(tipo) || "FLECHA".equals(tipo)) return "CONECTOR";
        if ("CARGAR".equals(tipo) || "FUNCION_DE_SISTEMA".equals(tipo)) return "FUNCION_DE_SISTEMA";
        if ("ID".equals(tipo)) return "ID";

        return "OPERADOR_SIMBOLO";
    }

    /**
     * Construye un conjunto de atributos de estilo según la familia del token.
     */
    public static SimpleAttributeSet obtenerEstilo(TokenEntidad token, BibliotecaDeTokens biblioteca) {
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        String familia = clasificarFamilia(token, biblioteca);

        switch (familia) {
            case "DIRECTIVA":
                StyleConstants.setForeground(estilo, COLOR_DIRECTIVA);
                StyleConstants.setBold(estilo, true);
                break;
            case "PALABRA_RESERVADA":
                StyleConstants.setForeground(estilo, COLOR_RESERVADA);
                StyleConstants.setBold(estilo, true);
                break;
            case "COMANDO_IA":
                StyleConstants.setForeground(estilo, COLOR_COMANDO_IA);
                StyleConstants.setBold(estilo, true);
                break;
            case "CONECTOR":
                StyleConstants.setForeground(estilo, COLOR_CONECTOR);
                StyleConstants.setBold(estilo, true);
                break;
            case "FUNCION_DE_SISTEMA":
                StyleConstants.setForeground(estilo, COLOR_FUNCION);
                StyleConstants.setBold(estilo, true);
                break;
            case "CADENA":
                StyleConstants.setForeground(estilo, COLOR_CADENA);
                StyleConstants.setBold(estilo, false);
                break;
            case "NUMERO":
                StyleConstants.setForeground(estilo, COLOR_NUMERO);
                StyleConstants.setBold(estilo, false);
                break;
            case "ID":
                StyleConstants.setForeground(estilo, COLOR_ID);
                StyleConstants.setBold(estilo, false);
                break;
            case "COMENTARIO":
                StyleConstants.setForeground(estilo, COLOR_COMENTARIO);
                StyleConstants.setItalic(estilo, true);
                break;
            case "ERROR_LEXICO":
                StyleConstants.setForeground(estilo, COLOR_ERROR);
                StyleConstants.setBold(estilo, true);
                StyleConstants.setUnderline(estilo, true);
                break;
            case "OPERADOR_SIMBOLO":
                StyleConstants.setForeground(estilo, COLOR_SIMBOLO);
                StyleConstants.setBold(estilo, false);
                break;
            case "ESPACIO":
            default:
                StyleConstants.setForeground(estilo, COLOR_ID);
                StyleConstants.setBold(estilo, false);
                break;
        }

        return estilo;
    }

    /**
     * Reescribe el código en el JTextPane usando la lista ordenada de tokens generada por JFlex,
     * pintando de color cada familia de palabras reservadas, directivas y demás símbolos.
     * Preserva la posición del cursor de texto (caret) para una experiencia fluida.
     *
     * @param editor el JTextPane a reescribir
     * @param tokens la lista de tokens generada por JFlex
     * @param biblioteca la biblioteca de tokens de referencia
     */
    public static void reescribirYColorear(JTextPane editor, List<TokenEntidad> tokens, BibliotecaDeTokens biblioteca) {
        if (editor == null || tokens == null) {
            return;
        }

        int caretPos = editor.getCaretPosition();

        StyledDocument nuevoDoc = new DefaultStyledDocument();
        try {
            for (TokenEntidad tok : tokens) {
                if ("EOF".equals(tok.getTipo())) {
                    continue;
                }
                String texto = tok.getDescripcion();
                if (texto != null && !texto.isEmpty()) {
                    SimpleAttributeSet estilo = obtenerEstilo(tok, biblioteca);
                    nuevoDoc.insertString(nuevoDoc.getLength(), texto, estilo);
                }
            }
            editor.setStyledDocument(nuevoDoc);

            int maxLen = nuevoDoc.getLength();
            if (caretPos > maxLen) {
                caretPos = maxLen;
            }
            if (caretPos >= 0) {
                editor.setCaretPosition(caretPos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Escanea el texto actual del JTextPane con el analizador JFlex (PromptzalLexer)
     * y reescribe el área de texto coloreando cada familia de tokens.
     *
     * @param editor el JTextPane a analizar y colorear
     */
    public static void colorearEditor(JTextPane editor) {
        if (editor == null) {
            return;
        }
        String texto = editor.getText();
        if (texto == null) {
            return;
        }

        try {
            PromptzalLexer lexer = new PromptzalLexer(new StringReader(texto));
            while (lexer.yylex() != null) {
                // Escaneo secuencial completo
            }
            List<TokenEntidad> tokens = lexer.getTodosLosTokens();
            BibliotecaDeTokens biblioteca = lexer.getBiblioteca();

            reescribirYColorear(editor, tokens, biblioteca);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
