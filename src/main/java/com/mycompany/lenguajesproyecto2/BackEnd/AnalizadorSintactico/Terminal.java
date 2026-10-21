package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico;

import com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta.BibliotecaDeTokens;

/**
 * Terminales oficiales del lenguaje de programación Promptzal.
 * Consulta y delega dinámicamente en BibliotecaDeTokens para que cualquier símbolo
 * o palabra registrada se reconozca y normalice automáticamente desde un único punto.
 */
public final class Terminal {

    private static final BibliotecaDeTokens BIBLIOTECA_CENTRAL = new BibliotecaDeTokens();

    private Terminal() {
        // Clase de constantes, no instanciable
    }

    public static final String DIRECTIVA = "DIRECTIVA";
    public static final String AGENTE = "AGENTE";
    public static final String CONTEXTO = "CONTEXTO";
    public static final String VARIABLE = "VARIABLE";
    public static final String EJECUTAR = "EJECUTAR";
    public static final String EXPORTAR = "EXPORTAR";
    public static final String COMANDO_IA = "COMANDO_IA";
    public static final String CARGAR = "CARGAR";
    public static final String CONECTOR = "CONECTOR";
    public static final String FLECHA = "FLECHA";
    public static final String IGUAL = "IGUAL";
    public static final String MAS = "MAS";
    public static final String LLAVE_A = "LLAVE_A";
    public static final String LLAVE_C = "LLAVE_C";
    public static final String PAR_A = "PAR_A";
    public static final String PAR_C = "PAR_C";
    public static final String COMA = "COMA";
    public static final String ID = "ID";
    public static final String CADENA = "CADENA";
    public static final String NUMERO = "NUMERO";
    public static final String EOF = "EOF";
    public static final String ERROR_LEXICO = "ERROR_LEXICO";

    public static String normalizar(com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorLexico.TokenEntidad token) {
        if (token == null) {
            return "";
        }
        return normalizar(token.getTipo(), token.getDescripcion());
    }

    public static String normalizar(String tipo, String lexema) {
        if (tipo == null) {
            return "";
        }

        // 1. Si el lexema está registrado en BibliotecaDeTokens, la biblioteca es la autoridad
        if (lexema != null && BIBLIOTECA_CENTRAL.existeEnLosTokens(lexema)) {
            return BIBLIOTECA_CENTRAL.obtenerTerminal(lexema);
        }

        String t = tipo.trim().toUpperCase();
        if (lexema != null) {
            String lex = lexema.trim();
            if (t.equals("PALABRA_RESERVADA")) {
                return lex.toUpperCase();
            }
            if (t.equals("FUNCION_DE_SISTEMA") && lex.equalsIgnoreCase("CARGAR")) {
                return CARGAR;
            }
            if (t.equals("CONECTORES")) {
                if (lex.equals("->")) return FLECHA;
                if (lex.equals(",")) return COMA;
                if (lex.equals("(")) return PAR_A;
                if (lex.equals(")")) return PAR_C;
                return CONECTOR;
            }
            if (t.equals("OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES")) {
                if (lex.equals("{")) return LLAVE_A;
                if (lex.equals("}")) return LLAVE_C;
                if (lex.equals("=")) return IGUAL;
                if (lex.equals("+")) return MAS;
            }
        }
        return normalizar(tipo);
    }

    public static String normalizar(String tipo) {
        if (tipo == null) {
            return "";
        }
        String t = tipo.trim().toUpperCase();
        switch (t) {
            case "IDENTIFICADOR":
                return ID;
            case "LLAVE_IZQ":
                return LLAVE_A;
            case "LLAVE_DER":
                return LLAVE_C;
            case "PARENTESIS_IZQ":
                return PAR_A;
            case "PARENTESIS_DER":
                return PAR_C;
            case "ENTERO":
            case "DECIMAL":
                return NUMERO;
            default:
                return t;
        }
    }
}
