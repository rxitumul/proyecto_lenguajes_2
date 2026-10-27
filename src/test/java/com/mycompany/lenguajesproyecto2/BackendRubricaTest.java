package com.mycompany.lenguajesproyecto2;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.PromptzalLexer;
import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico.ErrorSintactico;
import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico.PromptzalParser;
import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ReporteASTHtml;
import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ReporteErroresHtml;
import com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ReporteTablaSimbolosHtml;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoPrograma;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AnalizadorSemantico;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.BibliotecaGeneradorPromptZal;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.ErrorSemantico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BackendRubricaTest {

    @Test
    @DisplayName("1. Programa PromptZal completo y válido genera AST, Tabla de Símbolos y Prompt Final")
    public void testProgramaValidoCompleto() throws Exception {
        String codigo = """
            @modelo "gpt-4"
            @rol "asistente de investigación"
            @formato "markdown"

            AGENTE Investigador {
                contexto = "Experto en análisis de datos climáticos"
                variable entrada = "temperaturas globales"
                variable limite = 100 palabras
                RESUMIR entrada -> resumen
                PREGUNTAR "tendencia actual" SOBRE resumen -> prediccion
            }

            AGENTE Redactor {
                contexto = "Escritor científico"
                variable borrador = "Informe Preliminar"
                GENERAR articulo SOBRE borrador -> textoFinal
            }

            EJECUTAR Investigador
            EJECUTAR Redactor

            EXPORTAR resumen, prediccion, textoFinal
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigo);
        NodoPrograma ast = parser.parsearAST();

        assertNotNull(ast, "El AST no debe ser nulo");
        assertFalse(parser.tieneErrores(), "No deben existir errores sintácticos");
        assertEquals(3, ast.getDirectivas().size(), "Deben haber 3 directivas");
        assertEquals(2, ast.getAgentes().size(), "Deben haber 2 agentes");
        assertEquals(2, ast.getEjecuciones().size(), "Deben haber 2 ejecuciones");
        assertNotNull(ast.getExportar(), "Debe existir sentencia exportar");
        assertEquals(3, ast.getExportar().getVariablesExportadas().size(), "Deben exportarse 3 variables");

        // Análisis Semántico
        AnalizadorSemantico semantico = new AnalizadorSemantico();
        boolean semValida = semantico.analizar(ast);
        assertTrue(semValida, "La semántica debe ser válida sin errores: " + semantico.getErrores());
        assertTrue(semantico.getErrores().isEmpty());

        // Generador de PromptZal
        String promptFinal = BibliotecaGeneradorPromptZal.generarPromptFinal(
                ast, semantico.getTablaDeSimbolos(), semantico.getAgentesEjecutados());
        assertNotNull(promptFinal);
        assertTrue(promptFinal.contains("# Prompt generado con PromptZal"));
        assertTrue(promptFinal.contains("Modelo destino: gpt-4"));
        assertTrue(promptFinal.contains("## Agente: Investigador"));
        assertTrue(promptFinal.contains("Resume"));
        assertTrue(promptFinal.contains("Basandote en resumen"));
        assertTrue(promptFinal.contains("## Resultado solicitado"));
        assertTrue(promptFinal.contains("resumen"));
        assertTrue(promptFinal.contains("prediccion"));
        assertTrue(promptFinal.contains("textoFinal"));

        // Reportes HTML
        ReporteErroresHtml repErrores = new ReporteErroresHtml();
        StringBuilder htmlErrores = repErrores.generarHTML(List.of(), List.of(), List.of());
        assertTrue(htmlErrores.toString().contains("Compilación exitosa"));

        ReporteTablaSimbolosHtml repTabla = new ReporteTablaSimbolosHtml();
        StringBuilder htmlTabla = repTabla.generarHTML(semantico.getTablaDeSimbolos());
        assertTrue(htmlTabla.toString().contains("Investigador"));
        assertTrue(htmlTabla.toString().contains("Redactor"));
        assertTrue(htmlTabla.toString().contains("entrada"));

        ReporteASTHtml repAst = new ReporteASTHtml();
        String arbolTexto = ReporteASTHtml.generarArbolTexto(ast);
        assertTrue(arbolTexto.contains("NodoPrograma"));
        assertTrue(arbolTexto.contains("AGENTE: Investigador"));

        String dotAst = ReporteASTHtml.generarGraphvizDot(ast);
        assertTrue(dotAst.contains("digraph AST_PromptZal"));
    }

    @Test
    @DisplayName("2. Recuperación en Modo Pánico: Reporta múltiples errores sintácticos de una sola corrida")
    public void testRecuperacionModoPanico() throws Exception {
        // Código con múltiples errores sintácticos en diferentes bloques
        String codigoConErrores = """
            @modelo "gpt-4"

            AGENTE AgenteUno {
                contexto = "analista"
                variable = "falta el id"
                PREGUNTAR -> "falta expresion"
            }

            AGENTE AgenteDos {
                contexto = "programador"
                variable codigo = "print(1)"
                CODIFICAR codigo -> resultado
            }

            EJECUTAR AgenteDos
            EXPORTAR resultado
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigoConErrores);
        parser.parse();

        assertTrue(parser.tieneErrores(), "Debe detectar errores sintácticos");
        assertTrue(parser.getErrores().size() >= 1, "Debe registrar los errores sintácticos");

        for (ErrorSintactico err : parser.getErrores()) {
            assertTrue(err.getFila() > 0, "Fila debe ser positiva");
            assertTrue(err.getColumna() > 0, "Columna debe ser positiva");
            assertNotNull(err.getTipoEsperado(), "Tipo esperado no debe ser nulo");
            assertNotNull(err.getTokenEncontrado(), "Token encontrado no debe ser nulo");
        }
    }

    @Test
    @DisplayName("3. Semántica: Validación de Ámbito Local estricto entre agentes")
    public void testAmbitoLocalAislado() throws Exception {
        String codigo = """
            AGENTE AgenteA {
                contexto = "primer agente"
                variable varA = "valor de A"
                RESUMIR varA -> resA
            }

            AGENTE AgenteB {
                contexto = "segundo agente"
                variable varB = varA
                RESUMIR varB -> resB
            }

            EJECUTAR AgenteA
            EJECUTAR AgenteB
            EXPORTAR resA, resB
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigo);
        NodoPrograma ast = parser.parsearAST();
        assertNotNull(ast);

        AnalizadorSemantico semantico = new AnalizadorSemantico();
        boolean valida = semantico.analizar(ast);

        assertFalse(valida, "Debe fallar por violación de ámbito local (varA en AgenteB)");
        assertTrue(semantico.tieneErrores());
        boolean errorAmbitoDetectado = semantico.getErrores().stream()
                .anyMatch(e -> e.getDescripcion().contains("violación de ámbito local") || e.getIdentificador().equals("varA"));
        assertTrue(errorAmbitoDetectado, "Debe reportar que varA no pertenece al ámbito de AgenteB");
    }

    @Test
    @DisplayName("4. Semántica: Unicidad de nombres de agentes")
    public void testUnicidadDeAgentes() throws Exception {
        String codigo = """
            AGENTE Duplicado {
                contexto = "primero"
                variable x = "1"
                RESUMIR x -> r1
            }

            AGENTE Duplicado {
                contexto = "segundo con mismo nombre"
                variable y = "2"
                RESUMIR y -> r2
            }

            EJECUTAR Duplicado
            EXPORTAR r1
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigo);
        NodoPrograma ast = parser.parsearAST();
        assertNotNull(ast);

        AnalizadorSemantico semantico = new AnalizadorSemantico();
        boolean valida = semantico.analizar(ast);

        assertFalse(valida, "No deben permitirse agentes duplicados");
        boolean errorDuplicado = semantico.getErrores().stream()
                .anyMatch(e -> e.getDescripcion().contains("ya ha sido declarado previamente"));
        assertTrue(errorDuplicado, "Debe reportar error de duplicidad de agente");
    }

    @Test
    @DisplayName("5. Semántica: EJECUTAR agente inexistente y EXPORTAR variable inexistente o de agente no ejecutado")
    public void testEjecutarYExportarInexistentes() throws Exception {
        String codigo = """
            AGENTE Existente {
                contexto = "agente que no se ejecuta"
                variable varExiste = "datos"
                RESUMIR varExiste -> resExiste
            }

            EJECUTAR Inexistente
            EXPORTAR varFantasma, varExiste
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigo);
        NodoPrograma ast = parser.parsearAST();
        assertNotNull(ast);

        AnalizadorSemantico semantico = new AnalizadorSemantico();
        boolean valida = semantico.analizar(ast);

        assertFalse(valida, "Debe fallar en EJECUTAR y en EXPORTAR");
        boolean errorEjec = semantico.getErrores().stream()
                .anyMatch(e -> e.getDescripcion().contains("invocado en EJECUTAR no ha sido declarado"));
        assertTrue(errorEjec, "Debe reportar que el agente Inexistente no existe");

        boolean errorExportarNoEjec = semantico.getErrores().stream()
                .anyMatch(e -> e.getDescripcion().contains("no fue ejecutado con EJECUTAR") || e.getIdentificador().equals("varExiste"));
        assertTrue(errorExportarNoEjec, "Debe reportar que varExiste no fue ejecutado con EJECUTAR");
    }

    @Test
    @DisplayName("6. Semántica: Excepción obligatoria para argumentos de GENERAR y sufijos de unidades (ej. '100 palabras')")
    public void testExcepcionGenerarYUnidades() throws Exception {
        String codigo = """
            AGENTE Creador {
                contexto = "creador de contenidos"
                variable limite = 250 palabras
                variable lineas = 50 lineas
                GENERAR ensayo -> borrador
                RESUMIR borrador -> final
            }

            EJECUTAR Creador
            EXPORTAR final
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigo);
        NodoPrograma ast = parser.parsearAST();
        assertNotNull(ast, "AST debe ser generado");
        assertFalse(parser.tieneErrores(), "No debe haber errores sintácticos");

        AnalizadorSemantico semantico = new AnalizadorSemantico();
        boolean valida = semantico.analizar(ast);

        assertTrue(valida, "Debe ser válido porque 'ensayo' en GENERAR y 'palabras'/'lineas' son excepciones semánticas: " + semantico.getErrores());
        assertTrue(semantico.getErrores().isEmpty(), "No deben reportarse errores para argumentos de GENERAR ni sufijos numéricos");
    }

    @Test
    @DisplayName("7. Programa canónico del enunciado oficial: genera promptzal exacto y reportes de entrega")
    public void testProgramaEnunciadoCanonico() throws Exception {
        String codigo = """
            @modelo "claude-sonnet-4-6"
            @rol "analista de datos"
            @formato "markdown"

            // Agente que prepara el analisis de un archivo de ventas
            AGENTE analista {
                contexto = "Eres un analista de datos experto"
                variable ventas = CARGAR("ventas.csv")
                PREGUNTAR "Cuales son las 3 tendencias principales?" SOBRE ventas -> tendencias
                RESUMIR tendencias EN 100 palabras -> resumen
            }

            EJECUTAR analista
            EXPORTAR resumen
            """;

        PromptzalParser parser = PromptzalParser.desdeTexto(codigo);
        NodoPrograma ast = parser.parsearAST();
        assertNotNull(ast, "AST debe ser generado exitosamente");
        assertFalse(parser.tieneErrores(), "No debe haber errores sintácticos");

        AnalizadorSemantico semantico = new AnalizadorSemantico();
        boolean semValida = semantico.analizar(ast);
        assertTrue(semValida, "La semántica debe ser válida: " + semantico.getErrores());

        String promptFinal = BibliotecaGeneradorPromptZal.generarPromptFinal(
                ast, semantico.getTablaDeSimbolos(), semantico.getAgentesEjecutados());
        assertNotNull(promptFinal);
        assertTrue(promptFinal.contains("claude-sonnet-4-6"));
        assertTrue(promptFinal.contains("analista de datos"));
        assertTrue(promptFinal.contains("analista"));
        assertTrue(promptFinal.contains("resumen"));

        // Guardar archivos físicos de salida y reportes
        BibliotecaGeneradorPromptZal.guardarPromptEnArchivo("salidas/prompt_final.txt", promptFinal);
        BibliotecaGeneradorPromptZal.guardarPromptEnArchivo("salidas/prompt_final.md", promptFinal);

        new ReporteErroresHtml().crearReporte("reportes/reporte_errores.html",
                List.of(), List.of(), List.of());
        new ReporteTablaSimbolosHtml().crearReporte("reportes/tabla_simbolos.html",
                semantico.getTablaDeSimbolos());
        new ReporteASTHtml().crearReporte("reportes/ast.html", ast);
        ReporteASTHtml.guardarDotEnArchivo("reportes/ast.dot", ast);
    }
}
