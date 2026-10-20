package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico;

import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.PromptzalLexer;
import com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.TokenEntidad;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Analizador Sintáctico Descendente Recursivo para el lenguaje Promptzal.
 * Recorre secuencialmente una lista de objetos TokenEntidad mediante un índice
 * y valida el cumplimiento estricto de la Gramática Libre de Contexto (GLC) oficial.
 */
public class PromptzalParser {

    private final List<TokenEntidad> tokens;
    private int indice;
    private final List<ErrorSintactico> errores;

    /**
     * Construye un nuevo parser a partir de una lista secuencial de tokens.
     *
     * @param tokens Lista de tokens generada por el analizador léxico.
     */
    public PromptzalParser(List<TokenEntidad> tokens) {
        this.tokens = tokens != null ? new ArrayList<>(tokens) : new ArrayList<>();
        this.indice = 0;
        this.errores = new ArrayList<>();
    }

    /**
     * Método fábrica conveniente para inicializar el parser directamente desde un lexer de JFlex.
     *
     * @param lexer Instancia de PromptzalLexer previamente ejecutada.
     * @return Nueva instancia de PromptzalParser lista para analizar.
     */
    public static PromptzalParser desdeLexer(PromptzalLexer lexer) {
        if (lexer == null) {
            return new PromptzalParser(Collections.emptyList());
        }
        return new PromptzalParser(lexer.getListaDeTokens());
    }

    /**
     * Método fábrica conveniente para compilar texto plano en una sola invocación.
     *
     * @param codigoFuente Código en lenguaje Promptzal.
     * @return Instancia configurada de PromptzalParser.
     * @throws java.io.IOException Si ocurre un fallo de lectura en el buffer.
     */
    public static PromptzalParser desdeTexto(String codigoFuente) throws java.io.IOException {
        PromptzalLexer lexer = new PromptzalLexer(new StringReader(codigoFuente != null ? codigoFuente : ""));
        while (lexer.yylex() != null) {
            // El lexer almacena automáticamente en su lista interna
        }
        return new PromptzalParser(lexer.getListaDeTokens());
    }

    /**
     * Inicia el análisis sintáctico descendente recursivo desde el símbolo inicial <programa>.
     *
     * @return true si la sintaxis es 100% válida según la GLC oficial, false si hubo errores.
     */
    public boolean parse() {
        this.indice = 0;
        this.errores.clear();

        try {
            programa();
            return errores.isEmpty();
        } catch (ErrorSintacticoException e) {
            // Error capturado y almacenado en la lista interna
            return false;
        }
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE LA GRAMÁTICA LIBRE DE CONTEXTO OFICIAL (GLC)
    // =========================================================================

    /**
     * <programa> -> <directivas> <agentes> <ejecuciones> <exportar> EOF
     */
    private void programa() {
        directivas();
        agentes();
        ejecuciones();
        exportar();
        emparejar(Terminal.EOF);

        // Verificación de tokens sobrantes tras EOF
        if (indice < tokens.size()) {
            TokenEntidad extra = tokens.get(indice);
            if (!Terminal.normalizar(extra.getTipo()).equals(Terminal.EOF)) {
                reportarError("Fin de archivo (EOF)");
            }
        }
    }

    /**
     * <directivas> -> DIRECTIVA CADENA <directivas> | ε
     */
    private void directivas() {
        if (comprobar(Terminal.DIRECTIVA)) {
            emparejar(Terminal.DIRECTIVA);
            emparejar(Terminal.CADENA);
            directivas();
        }
        // ε: Siguiente conjunto de seguimiento First(<agentes>) = { AGENTE }
    }

    /**
     * <agentes> -> <agente> <mas_agentes>
     */
    private void agentes() {
        agente();
        mas_agentes();
    }

    /**
     * <mas_agentes> -> <agente> <mas_agentes> | ε
     */
    private void mas_agentes() {
        if (comprobar(Terminal.AGENTE)) {
            agente();
            mas_agentes();
        }
        // ε: Siguiente conjunto de seguimiento First(<ejecuciones>) = { EJECUTAR }
    }

    /**
     * <agente> -> AGENTE ID LLAVE_A <contexto> <variables> <comandos> LLAVE_C
     */
    private void agente() {
        emparejar(Terminal.AGENTE);
        emparejar(Terminal.ID);
        emparejar(Terminal.LLAVE_A);
        contexto();
        variables();
        comandos();
        emparejar(Terminal.LLAVE_C);
    }

    /**
     * <contexto> -> CONTEXTO IGUAL CADENA
     */
    private void contexto() {
        emparejar(Terminal.CONTEXTO);
        emparejar(Terminal.IGUAL);
        emparejar(Terminal.CADENA);
    }

    /**
     * <variables> -> VARIABLE ID IGUAL <expresion> <variables> | ε
     */
    private void variables() {
        if (comprobar(Terminal.VARIABLE)) {
            emparejar(Terminal.VARIABLE);
            emparejar(Terminal.ID);
            emparejar(Terminal.IGUAL);
            expresion();
            variables();
        }
        // ε: Siguiente conjunto de seguimiento First(<comandos>) = { COMANDO_IA }
    }

    /**
     * <comandos> -> <comando> <mas_comandos>
     */
    private void comandos() {
        comando();
        mas_comandos();
    }

    /**
     * <mas_comandos> -> <comando> <mas_comandos> | ε
     */
    private void mas_comandos() {
        if (comprobar(Terminal.COMANDO_IA)) {
            comando();
            mas_comandos();
        }
        // ε: Siguiente conjunto de seguimiento = { LLAVE_C }
    }

    /**
     * <comando> -> COMANDO_IA <expresion> <conectores> FLECHA ID
     */
    private void comando() {
        emparejar(Terminal.COMANDO_IA);
        expresion();
        conectores();
        emparejar(Terminal.FLECHA);
        emparejar(Terminal.ID);
    }

    /**
     * <conectores> -> CONECTOR <expresion> <conectores> | ε
     */
    private void conectores() {
        if (comprobar(Terminal.CONECTOR)) {
            emparejar(Terminal.CONECTOR);
            expresion();
            conectores();
        }
        // ε: Siguiente conjunto de seguimiento = { FLECHA }
    }

    /**
     * <expresion> -> <termino> <mas_terminos>
     */
    private void expresion() {
        termino();
        mas_terminos();
    }

    /**
     * <mas_terminos> -> MAS <termino> <mas_terminos> | ε
     */
    private void mas_terminos() {
        if (comprobar(Terminal.MAS)) {
            emparejar(Terminal.MAS);
            termino();
            mas_terminos();
        }
        // ε: Seguidores válidos según el contexto: { CONECTOR, FLECHA, VARIABLE, COMANDO_IA }
    }

    /**
     * <termino> -> CADENA | ID | NUMERO <unidad> | <cargar>
     */
    private void termino() {
        if (comprobar(Terminal.CADENA)) {
            emparejar(Terminal.CADENA);
        } else if (comprobar(Terminal.ID)) {
            emparejar(Terminal.ID);
        } else if (comprobar(Terminal.NUMERO)) {
            emparejar(Terminal.NUMERO);
            unidad();
        } else if (comprobar(Terminal.CARGAR)) {
            cargar();
        } else {
            reportarError("un término válido (CADENA, ID, NUMERO o CARGAR)");
        }
    }

    /**
     * <unidad> -> ID | ε
     */
    private void unidad() {
        if (comprobar(Terminal.ID)) {
            emparejar(Terminal.ID);
        }
        // ε: Siguiente seguidor = { MAS, CONECTOR, FLECHA, VARIABLE, COMANDO_IA }
    }

    /**
     * <cargar> -> CARGAR PAR_A CADENA PAR_C
     */
    private void cargar() {
        emparejar(Terminal.CARGAR);
        emparejar(Terminal.PAR_A);
        emparejar(Terminal.CADENA);
        emparejar(Terminal.PAR_C);
    }

    /**
     * <ejecuciones> -> EJECUTAR ID <mas_ejecuciones>
     */
    private void ejecuciones() {
        emparejar(Terminal.EJECUTAR);
        emparejar(Terminal.ID);
        mas_ejecuciones();
    }

    /**
     * <mas_ejecuciones> -> EJECUTAR ID <mas_ejecuciones> | ε
     */
    private void mas_ejecuciones() {
        if (comprobar(Terminal.EJECUTAR)) {
            emparejar(Terminal.EJECUTAR);
            emparejar(Terminal.ID);
            mas_ejecuciones();
        }
        // ε: Siguiente conjunto de seguimiento First(<exportar>) = { EXPORTAR }
    }

    /**
     * <exportar> -> EXPORTAR ID <mas_ids>
     */
    private void exportar() {
        emparejar(Terminal.EXPORTAR);
        emparejar(Terminal.ID);
        mas_ids();
    }

    /**
     * <mas_ids> -> COMA ID <mas_ids> | ε
     */
    private void mas_ids() {
        if (comprobar(Terminal.COMA)) {
            emparejar(Terminal.COMA);
            emparejar(Terminal.ID);
            mas_ids();
        }
        // ε: Siguiente conjunto de seguimiento = { EOF }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DEL RECORRIDO Y VALIDACIÓN DE TOKENS
    // =========================================================================

    /**
     * Obtiene el token actual en la posición indicada por el índice.
     * Si el índice alcanza o supera el tamaño de la lista, genera un token EOF sintético
     * manteniendo la fila y columna del último elemento.
     *
     * @return TokenEntidad actual o EOF sintético.
     */
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

    /**
     * Comprueba si el tipo normalizado del token actual coincide con el terminal esperado
     * sin avanzar el índice del recorrido.
     *
     * @param tipoEsperado Terminal a consultar.
     * @return true si coincide, false en caso contrario.
     */
    private boolean comprobar(String tipoEsperado) {
        TokenEntidad actual = tokenActual();
        String tipoNorm = Terminal.normalizar(actual.getTipo());
        return tipoNorm.equals(tipoEsperado);
    }

    /**
     * Valida que el token actual coincida con el terminal esperado.
     * Si coincide, avanza el índice y retorna el token emparejado.
     * Si no coincide, registra un ErrorSintactico con la fila y columna exacta y detiene el análisis.
     *
     * @param tipoEsperado Terminal requerido por la gramática.
     * @return TokenEntidad emparejado exitosamente.
     * @throws ErrorSintacticoException Si el token no coincide.
     */
    private TokenEntidad emparejar(String tipoEsperado) {
        TokenEntidad actual = tokenActual();
        String tipoNorm = Terminal.normalizar(actual.getTipo());

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

    /**
     * Notifica y almacena un error sintáctico con la fila y columna exactas del token actual.
     *
     * @param descripcionEsperado Descripción amigable de lo que se esperaba en este punto.
     * @throws ErrorSintacticoException Siempre lanza la excepción para desenrollar la pila.
     */
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
