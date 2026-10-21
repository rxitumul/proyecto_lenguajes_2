package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.PromptzalLexer;
import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.TokenEntidad;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoAgente;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoComando;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoConector;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoDirectiva;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoEjecucion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExpresion;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoExportar;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoPrograma;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoTermino;
import com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST.NodoVariable;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Analizador Sintáctico Descendente Recursivo para el lenguaje Promptzal escrito a mano.
 * Cada no terminal de la Gramática Libre de Contexto (GLC) corresponde a un método identificable.
 * Implementa recuperación de errores sintácticos en MODO PÁNICO mediante tokens de sincronización
 * (AGENTE, LLAVE_C, COMANDO_IA, VARIABLE, EJECUTAR, EXPORTAR, EOF) para reportar todos los errores
 * sintácticos de una sola corrida (indicando token encontrado, esperado, fila y columna).
 */
public class PromptzalParser {

    // Conjunto canónico de tokens de sincronización exigidos por la rúbrica
    public static final Set<String> TOKENS_SINCRONIZACION = Set.of(
            Terminal.AGENTE,
            Terminal.LLAVE_C,
            Terminal.COMANDO_IA,
            Terminal.VARIABLE,
            Terminal.EJECUTAR,
            Terminal.EXPORTAR,
            Terminal.EOF
    );

    private final List<TokenEntidad> tokens;
    private int indice;
    private final List<ErrorSintactico> errores;
    private NodoPrograma arbolSintactico;

    /**
     * Construye un nuevo parser a partir de una lista secuencial de tokens.
     */
    public PromptzalParser(List<TokenEntidad> tokens) {
        this.tokens = tokens != null ? new ArrayList<>(tokens) : new ArrayList<>();
        this.indice = 0;
        this.errores = new ArrayList<>();
        this.arbolSintactico = null;
    }

    public static PromptzalParser desdeLexer(PromptzalLexer lexer) {
        if (lexer == null) {
            return new PromptzalParser(Collections.emptyList());
        }
        return new PromptzalParser(lexer.getListaDeTokens());
    }

    public static PromptzalParser desdeTexto(String codigoFuente) throws java.io.IOException {
        PromptzalLexer lexer = new PromptzalLexer(new StringReader(codigoFuente != null ? codigoFuente : ""));
        while (lexer.yylex() != null) {
            // El lexer almacena automáticamente en su lista interna
        }
        return new PromptzalParser(lexer.getListaDeTokens());
    }

    /**
     * Ejecuta el análisis sintáctico completo.
     * Retorna true si el código no contiene errores sintácticos, false si se registraron errores.
     */
    public boolean parse() {
        this.indice = 0;
        this.errores.clear();
        this.arbolSintactico = null;

        try {
            this.arbolSintactico = programa();
            return errores.isEmpty();
        } catch (Exception e) {
            // Cualquier fallo residual no previsto
            return false;
        }
    }

    /**
     * Retorna el Árbol de Sintaxis Abstracta (AST) generado si el análisis fue exitoso.
     */
    public NodoPrograma parsearAST() {
        if (parse()) {
            return this.arbolSintactico;
        }
        return null;
    }

    // =========================================================================
    // MECANISMO DE RECUPERACIÓN DE ERRORES EN MODO PÁNICO
    // =========================================================================

    /**
     * Descarta tokens infractores hasta encontrar un token perteneciente al conjunto
     * de sincronización suministrado o el fin de archivo (EOF).
     */
    private void sincronizar(Set<String> sincronizadores) {
        while (!esFinDeArchivo()) {
            String tipoActual = Terminal.normalizar(tokenActual());
            if (sincronizadores.contains(tipoActual)) {
                break;
            }
            indice++;
        }
    }

    private boolean esFinDeArchivo() {
        return indice >= tokens.size() || Terminal.normalizar(tokenActual()).equals(Terminal.EOF);
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE LA GLC: NO TERMINALES EN MÉTODOS IDENTIFICABLES
    // =========================================================================

    /**
     * <programa> -> <directivas> <agentes> <ejecuciones> <exportar> EOF
     */
    private NodoPrograma programa() {
        int filaInicial = tokenActual().getFila();
        int colInicial = tokenActual().getColumna();
        NodoPrograma prog = new NodoPrograma(filaInicial, colInicial);

        // 1. Directivas
        directivas(prog);

        // 2. Agentes
        agentes(prog);

        // 3. Ejecuciones
        ejecuciones(prog);

        // 4. Exportar
        exportar(prog);

        // 5. EOF
        try {
            emparejar(Terminal.EOF);
        } catch (ErrorSintacticoException e) {
            sincronizar(Set.of(Terminal.EOF));
        }

        // Verificación de tokens sobrantes tras EOF
        if (indice < tokens.size()) {
            TokenEntidad extra = tokens.get(indice);
            if (!Terminal.normalizar(extra).equals(Terminal.EOF)) {
                reportarError("Fin de archivo (EOF)");
            }
        }

        return prog;
    }

    /**
     * <directivas> -> DIRECTIVA CADENA <directivas> | ε
     */
    private void directivas(NodoPrograma prog) {
        while (comprobar(Terminal.DIRECTIVA)) {
            try {
                TokenEntidad tokDir = emparejar(Terminal.DIRECTIVA);
                TokenEntidad tokCad = emparejar(Terminal.CADENA);
                prog.agregarDirectiva(new NodoDirectiva(tokDir.getDescripcion(), tokCad.getDescripcion(),
                        tokDir.getFila(), tokDir.getColumna()));
            } catch (ErrorSintacticoException e) {
                // Modo pánico: descartar hasta la siguiente DIRECTIVA, AGENTE, EJECUTAR, EXPORTAR, EOF
                sincronizar(Set.of(Terminal.DIRECTIVA, Terminal.AGENTE, Terminal.EJECUTAR, Terminal.EXPORTAR, Terminal.EOF));
            }
        }
    }

    /**
     * <agentes> -> <agente> <mas_agentes>
     */
    private void agentes(NodoPrograma prog) {
        NodoAgente primerAgente = agente();
        if (primerAgente != null) {
            prog.agregarAgente(primerAgente);
        }
        mas_agentes(prog);
    }

    /**
     * <mas_agentes> -> <agente> <mas_agentes> | ε
     */
    private void mas_agentes(NodoPrograma prog) {
        while (comprobar(Terminal.AGENTE)) {
            NodoAgente sigAgente = agente();
            if (sigAgente != null) {
                prog.agregarAgente(sigAgente);
            }
        }
    }

    /**
     * <agente> -> AGENTE ID LLAVE_A <contexto> <variables> <comandos> LLAVE_C
     */
    private NodoAgente agente() {
        try {
            TokenEntidad tokAgente = emparejar(Terminal.AGENTE);
            TokenEntidad tokId = emparejar(Terminal.ID);
            emparejar(Terminal.LLAVE_A);

            String contextoStr = contexto();
            List<NodoVariable> listaVariables = new ArrayList<>();
            variables(listaVariables);

            List<NodoComando> listaComandos = new ArrayList<>();
            comandos(listaComandos);

            emparejar(Terminal.LLAVE_C);

            return new NodoAgente(tokId.getDescripcion(), contextoStr, listaVariables,
                    listaComandos, tokAgente.getFila(), tokAgente.getColumna());
        } catch (ErrorSintacticoException e) {
            // Modo pánico para agente: descartar hasta encontrar el siguiente AGENTE, EJECUTAR, EXPORTAR o EOF
            sincronizar(Set.of(Terminal.AGENTE, Terminal.EJECUTAR, Terminal.EXPORTAR, Terminal.EOF));
            return null;
        }
    }

    /**
     * <contexto> -> CONTEXTO IGUAL CADENA
     */
    private String contexto() {
        try {
            emparejar(Terminal.CONTEXTO);
            emparejar(Terminal.IGUAL);
            TokenEntidad tokCad = emparejar(Terminal.CADENA);
            return tokCad.getDescripcion();
        } catch (ErrorSintacticoException e) {
            // Modo pánico dentro de agente: descartar hasta VARIABLE, COMANDO_IA, LLAVE_C
            sincronizar(Set.of(Terminal.VARIABLE, Terminal.COMANDO_IA, Terminal.LLAVE_C, Terminal.AGENTE, Terminal.EJECUTAR, Terminal.EOF));
            return "";
        }
    }

    /**
     * <variables> -> VARIABLE ID IGUAL <expresion> <variables> | ε
     */
    private void variables(List<NodoVariable> lista) {
        while (comprobar(Terminal.VARIABLE)) {
            try {
                TokenEntidad tokVar = emparejar(Terminal.VARIABLE);
                TokenEntidad tokId = emparejar(Terminal.ID);
                emparejar(Terminal.IGUAL);
                NodoExpresion exp = expresion();

                lista.add(new NodoVariable(tokId.getDescripcion(), exp, tokVar.getFila(), tokVar.getColumna()));
            } catch (ErrorSintacticoException e) {
                // Modo pánico para variables: sincroniza con VARIABLE, COMANDO_IA, LLAVE_C
                sincronizar(Set.of(Terminal.VARIABLE, Terminal.COMANDO_IA, Terminal.LLAVE_C, Terminal.AGENTE, Terminal.EJECUTAR, Terminal.EOF));
            }
        }
    }

    /**
     * <comandos> -> <comando> <mas_comandos>
     */
    private void comandos(List<NodoComando> lista) {
        while (comprobar(Terminal.COMANDO_IA)) {
            try {
                lista.add(comando());
            } catch (ErrorSintacticoException e) {
                // Modo pánico para comandos: sincroniza con COMANDO_IA, LLAVE_C
                sincronizar(Set.of(Terminal.COMANDO_IA, Terminal.LLAVE_C, Terminal.AGENTE, Terminal.EJECUTAR, Terminal.EOF));
            }
        }
    }

    /**
     * <comando> -> COMANDO_IA <expresion> <conectores> FLECHA ID
     */
    private NodoComando comando() {
        TokenEntidad tokCmd = emparejar(Terminal.COMANDO_IA);
        NodoExpresion exp = expresion();
        List<NodoConector> listaConectores = new ArrayList<>();
        conectores(listaConectores);
        emparejar(Terminal.FLECHA);
        TokenEntidad tokDestino = emparejar(Terminal.ID);

        return new NodoComando(tokCmd.getDescripcion(), exp, listaConectores,
                tokDestino.getDescripcion(), tokCmd.getFila(), tokCmd.getColumna());
    }

    /**
     * <conectores> -> CONECTOR <expresion> <conectores> | ε
     */
    private void conectores(List<NodoConector> lista) {
        while (comprobar(Terminal.CONECTOR)) {
            TokenEntidad tokCon = emparejar(Terminal.CONECTOR);
            NodoExpresion exp = expresion();
            lista.add(new NodoConector(tokCon.getDescripcion(), exp, tokCon.getFila(), tokCon.getColumna()));
        }
    }

    /**
     * <expresion> -> <termino> <mas_terminos>
     */
    private NodoExpresion expresion() {
        TokenEntidad actual = tokenActual();
        NodoExpresion exp = new NodoExpresion(actual.getFila(), actual.getColumna());
        exp.agregarTermino(termino());
        mas_terminos(exp);
        return exp;
    }

    /**
     * <mas_terminos> -> MAS <termino> <mas_terminos> | ε
     */
    private void mas_terminos(NodoExpresion exp) {
        while (comprobar(Terminal.MAS)) {
            emparejar(Terminal.MAS);
            exp.agregarTermino(termino());
        }
    }

    /**
     * <termino> -> CADENA | ID | NUMERO <unidad> | <cargar>
     */
    private NodoTermino termino() {
        if (comprobar(Terminal.CADENA)) {
            TokenEntidad tok = emparejar(Terminal.CADENA);
            return NodoTermino.crearCadena(tok.getDescripcion(), tok.getFila(), tok.getColumna());
        } else if (comprobar(Terminal.ID)) {
            TokenEntidad tok = emparejar(Terminal.ID);
            return NodoTermino.crearId(tok.getDescripcion(), tok.getFila(), tok.getColumna());
        } else if (comprobar(Terminal.NUMERO)) {
            TokenEntidad tokNum = emparejar(Terminal.NUMERO);
            String unidadStr = unidad();
            return NodoTermino.crearNumero(tokNum.getDescripcion(), unidadStr, tokNum.getFila(), tokNum.getColumna());
        } else if (comprobar(Terminal.CARGAR)) {
            TokenEntidad tokCargar = tokenActual();
            String rutaArchivo = cargar();
            return NodoTermino.crearCargar(rutaArchivo, tokCargar.getFila(), tokCargar.getColumna());
        } else {
            reportarError("un término válido (CADENA, ID, NUMERO o CARGAR)");
            return null;
        }
    }

    /**
     * <unidad> -> ID | ε
     */
    private String unidad() {
        if (comprobar(Terminal.ID)) {
            TokenEntidad tokId = emparejar(Terminal.ID);
            return tokId.getDescripcion();
        }
        return null;
    }

    /**
     * <cargar> -> CARGAR PAR_A CADENA PAR_C
     */
    private String cargar() {
        emparejar(Terminal.CARGAR);
        emparejar(Terminal.PAR_A);
        TokenEntidad tokCad = emparejar(Terminal.CADENA);
        emparejar(Terminal.PAR_C);
        return tokCad.getDescripcion();
    }

    /**
     * <ejecuciones> -> EJECUTAR ID <mas_ejecuciones>
     */
    private void ejecuciones(NodoPrograma prog) {
        while (comprobar(Terminal.EJECUTAR)) {
            try {
                emparejar(Terminal.EJECUTAR);
                TokenEntidad tokId = emparejar(Terminal.ID);
                prog.agregarEjecucion(new NodoEjecucion(tokId.getDescripcion(), tokId.getFila(), tokId.getColumna()));
            } catch (ErrorSintacticoException e) {
                // Modo pánico: descartar hasta el siguiente EJECUTAR, EXPORTAR, EOF
                sincronizar(Set.of(Terminal.EJECUTAR, Terminal.EXPORTAR, Terminal.EOF));
            }
        }
    }

    /**
     * <exportar> -> EXPORTAR ID <mas_ids>
     */
    private void exportar(NodoPrograma prog) {
        if (comprobar(Terminal.EXPORTAR)) {
            try {
                TokenEntidad tokExp = emparejar(Terminal.EXPORTAR);
                TokenEntidad tokId = emparejar(Terminal.ID);

                NodoExportar nodoExp = new NodoExportar(tokExp.getFila(), tokExp.getColumna());
                nodoExp.agregarVariable(tokId.getDescripcion(), tokId.getFila(), tokId.getColumna());
                mas_ids(nodoExp);

                prog.setExportar(nodoExp);
            } catch (ErrorSintacticoException e) {
                // Modo pánico: descartar hasta EOF
                sincronizar(Set.of(Terminal.EOF));
            }
        }
    }

    /**
     * <mas_ids> -> COMA ID <mas_ids> | ε
     */
    private void mas_ids(NodoExportar nodoExp) {
        while (comprobar(Terminal.COMA)) {
            emparejar(Terminal.COMA);
            TokenEntidad tokId = emparejar(Terminal.ID);
            nodoExp.agregarVariable(tokId.getDescripcion(), tokId.getFila(), tokId.getColumna());
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DEL RECORRIDO Y VALIDACIÓN DE TOKENS
    // =========================================================================

    private TokenEntidad tokenActual() {
        if (indice < tokens.size()) {
            return tokens.get(indice);
        }
        int fila = tokens.isEmpty() ? 1 : tokens.get(tokens.size() - 1).getFila();
        int col = tokens.isEmpty() ? 1 : tokens.get(tokens.size() - 1).getColumna()
                + (tokens.get(tokens.size() - 1).getDescripcion() != null
                   ? tokens.get(tokens.size() - 1).getDescripcion().length() : 1);
        return new TokenEntidad(Terminal.EOF, "EOF", fila, col);
    }

    private boolean comprobar(String tipoEsperado) {
        TokenEntidad actual = tokenActual();
        String tipoNorm = Terminal.normalizar(actual);
        return tipoNorm.equals(tipoEsperado);
    }

    private TokenEntidad emparejar(String tipoEsperado) {
        TokenEntidad actual = tokenActual();
        String tipoNorm = Terminal.normalizar(actual);

        if (tipoNorm.equals(tipoEsperado)) {
            indice++;
            return actual;
        }

        String mensaje = String.format(
            "Se esperaba el terminal '%s', pero se encontró '%s' (Lexema: '%s')",
            tipoEsperado, actual.getTipo(), actual.getDescripcion()
        );
        ErrorSintactico error = new ErrorSintactico(actual.getFila(), actual.getColumna(), actual, tipoEsperado, mensaje);
        errores.add(error);
        throw new ErrorSintacticoException(error);
    }

    private void reportarError(String descripcionEsperado) {
        TokenEntidad actual = tokenActual();
        String mensaje = String.format(
            "Se esperaba %s, pero se encontró '%s' (Lexema: '%s')",
            descripcionEsperado, actual.getTipo(), actual.getDescripcion()
        );
        ErrorSintactico error = new ErrorSintactico(actual.getFila(), actual.getColumna(), actual, descripcionEsperado, mensaje);
        errores.add(error);
        throw new ErrorSintacticoException(error);
    }

    // =========================================================================
    // GETTERS Y UTILIDADES PÚBLICAS
    // =========================================================================

    public List<ErrorSintactico> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public int getIndiceActual() {
        return indice;
    }

    public NodoPrograma getArbolSintactico() {
        return arbolSintactico;
    }

    public void imprimirErrores() {
        if (errores.isEmpty()) {
            System.out.println("No se detectaron errores sintácticos. El programa es válido.");
            return;
        }
        System.err.println("=== ERRORES SINTÁCTICOS DETECTADOS (" + errores.size() + ") ===");
        for (ErrorSintactico error : errores) {
            System.err.println(error);
        }
    }
}
