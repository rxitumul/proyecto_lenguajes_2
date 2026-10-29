package com.mycompany.lenguajesproyecto2.BackEnd.BibliotecaCarpeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Biblioteca Central de Tokens, Símbolos y Reglas del Lenguaje PromptZal.
 * 
 * Cumple con el principio de Fuente Única de Verdad:
 * Para agregar cualquier palabra reservada, símbolo especial, comando de IA,
 * conector o directiva, SOLO se debe registrar en esta clase.
 * El analizador léxico (JFlex), el analizador sintáctico (Parser) y el generador
 * consultan dinámicamente esta biblioteca sin necesidad de reconfiguraciones manuales.
 */
public class BibliotecaDeTokens {

    private final Map<String, TokensDefinicion> reservadas = new HashMap<>();
    private final Map<String, String> mapeoTerminales = new HashMap<>();


    public BibliotecaDeTokens() {
        inicializarDirectivas();
        inicializarPalabrasReservadas();
        inicializarComandosIA();
        inicializarConectores();
        inicializarFuncionesDeSistema();
        inicializarSimbolosYOperadores();
    }

    // =========================================================================
    // INICIALIZACIÓN DE COMPONENTES DEL LENGUAJE
    // =========================================================================

    private void inicializarDirectivas() {
        registrarDirectiva("@modelo", "Indica la herramienta o modelo de IA (ej. 'claude-3-5', 'gpt-4').");
        registrarDirectiva("@rol", "Define el rol general que asumirá la IA en el encabezado del prompt.");
        registrarDirectiva("@formato", "Indica el formato de salida deseado (ej. 'json', 'markdown', 'lista').");
        registrarDirectiva("@temperatura", "Ajusta la creatividad y determinismo del modelo (0.0 a 1.0).");
        registrarDirectiva("@max_tokens", "Límite superior de tokens para la respuesta generada.");
        registrarDirectiva("@idioma", "Idioma de preferencia de salida para las respuestas.");
    }

    private void inicializarPalabrasReservadas() {
        registrarPalabraReservada("AGENTE", "AGENTE", "Declara un agente: la unidad que agrupa contexto, variables y comandos.");
        registrarPalabraReservada("contexto", "CONTEXTO", "Define el rol que asumirá la IA dentro del agente.");
        registrarPalabraReservada("variable", "VARIABLE", "Declara una variable que almacena un valor o el resultado de un comando.");
        registrarPalabraReservada("EJECUTAR", "EJECUTAR", "Corre un agente previamente definido.");
        registrarPalabraReservada("EXPORTAR", "EXPORTAR", "Indica qué resultados se incluyen en el prompt final.");
    }

    private void inicializarComandosIA() {
        registrarComando("PREGUNTAR", "Formula una pregunta a la IA sobre unos datos o contexto.");
        registrarComando("GENERAR", "Solicita a la IA que genere contenido nuevo.");
        registrarComando("CODIFICAR", "Comando de IA para generar o transformar código.");
        registrarComando("RESUMIR", "Pide un resumen o síntesis de un contenido.");
        registrarComando("ANALIZAR", "Solicita un análisis detallado de datos bajo cierto criterio.");
        registrarComando("TRADUCIR", "Traduce un contenido a otro idioma o formato.");
        registrarComando("CLASIFICAR", "Clasifica un contenido dentro de un conjunto de categorías.");
        registrarComando("EXTRAER", "Extrae información específica de un texto.");
    }

    private void inicializarConectores() {
        registrarConector("SOBRE", "Indica los datos sobre los que opera un comando.");
        registrarConector("DESDE", "Indica la fuente o especificación base.");
        registrarConector("EN", "Indica un parámetro del comando (idioma, formato, entorno).");
        registrarConector("COMO", "Indica el formato o rol de salida deseado.");
        registrarConector("PARA", "Indica el destinatario o propósito del resultado.");
        registrarConector("CON", "Agrega parámetros o restricciones adicionales.");
        registrarConector("MEDIANTE", "Indica el método o técnica a utilizar.");
        registrarConector("SEGUN", "Indica un criterio o estándar de referencia.");
    }

    private void inicializarFuncionesDeSistema() {
        registrarTokenInterno("CARGAR", "FUNCION_DE_SISTEMA", "CARGAR",
                "Carga el contenido de un archivo local y lo entrega como valor.");
    }

    private void inicializarSimbolosYOperadores() {
        // Símbolos de control de bloques y flujo
        registrarSimbolo("->", "FLECHA", "CONECTORES", "Asigna el resultado del comando a una variable.");
        registrarSimbolo(",", "COMA", "CONECTORES", "Separador de elementos.");
        registrarSimbolo("(", "PAR_A", "CONECTORES", "Paréntesis de apertura.");
        registrarSimbolo(")", "PAR_C", "CONECTORES", "Paréntesis de cierre.");

        // Operadores de bloques y expresiones
        registrarSimbolo("{", "LLAVE_A", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Llave de apertura de bloque.");
        registrarSimbolo("}", "LLAVE_C", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Llave de cierre de bloque.");
        registrarSimbolo("=", "IGUAL", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Operador de asignación.");
        registrarSimbolo("+", "MAS", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Operador de concatenación o suma.");

        // Símbolos opcionales / extensibles
        registrarSimbolo(";", "PUNTO_Y_COMA", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Fin de instrucción.");
        registrarSimbolo(":", "DOS_PUNTOS", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Dos puntos.");
        registrarSimbolo("...", "TRES_PUNTOS", "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES", "Operador de continuidad.");
    }

    // =========================================================================
    // MÉTODOS DE REGISTRO MODULARES (PUNTO ÚNICO DE EXTENSIÓN)
    // =========================================================================

    public void registrarDirectiva(String directiva, String descripcion) {
        String key = directiva.trim().toLowerCase();
        reservadas.put(key, new TokensDefinicion("DIRECTIVA", descripcion));
        mapeoTerminales.put(key, "DIRECTIVA");
    }

    public void registrarPalabraReservada(String palabra, String terminal, String descripcion) {
        String key = palabra.trim();
        reservadas.put(key, new TokensDefinicion("PALABRA_RESERVADA", descripcion));
        mapeoTerminales.put(key, terminal);
    }

    public void registrarComando(String comando, String descripcion) {
        String key = comando.trim().toUpperCase();
        reservadas.put(key, new TokensDefinicion("COMANDO_IA", descripcion));
        mapeoTerminales.put(key, "COMANDO_IA");
    }

    public void registrarConector(String conector, String descripcion) {
        String key = conector.trim().toUpperCase();
        reservadas.put(key, new TokensDefinicion("CONECTORES", descripcion));
        mapeoTerminales.put(key, "CONECTOR");
    }

    public void registrarSimbolo(String simbolo, String terminal, String categoria, String descripcion) {
        String key = simbolo.trim();
        reservadas.put(key, new TokensDefinicion(categoria, descripcion));
        mapeoTerminales.put(key, terminal);
    }

    private void registrarTokenInterno(String token, String categoria, String terminal, String descripcion) {
        reservadas.put(token, new TokensDefinicion(categoria, descripcion));
        mapeoTerminales.put(token, terminal);
    }

    // =========================================================================
    // MÉTODOS DE CONSULTA LÉXICA Y SINTÁCTICA
    // =========================================================================

    public boolean existeEnLosTokens(String palabra) {
        if (palabra == null) return false;
        return reservadas.containsKey(palabra) || reservadas.containsKey(palabra.toLowerCase()) || reservadas.containsKey(palabra.toUpperCase());
    }

    public String mapeadorDeTokens(String token) {
        if (token == null) return "DESCONOCIDO";
        TokensDefinicion def = reservadas.get(token);
        if (def == null) def = reservadas.get(token.toLowerCase());
        if (def == null) def = reservadas.get(token.toUpperCase());
        return def != null ? def.getTipo() : "DESCONOCIDO";
    }

    public String obtenerTerminal(String token) {
        if (token == null) return "ID";
        if (mapeoTerminales.containsKey(token)) {
            return mapeoTerminales.get(token);
        }
        if (mapeoTerminales.containsKey(token.toUpperCase())) {
            return mapeoTerminales.get(token.toUpperCase());
        }
        if (mapeoTerminales.containsKey(token.toLowerCase())) {
            return mapeoTerminales.get(token.toLowerCase());
        }

        // Si no está explícito en el mapa de terminales pero existe en las reservadas
        String tipo = mapeadorDeTokens(token);
        switch (tipo) {
            case "PALABRA_RESERVADA":
                return token.toUpperCase();
            case "COMANDO_IA":
                return "COMANDO_IA";
            case "CONECTORES":
                return "CONECTOR";
            case "DIRECTIVA":
                return "DIRECTIVA";
            case "FUNCION_DE_SISTEMA":
                return "CARGAR";
            default:
                return "ID";
        }
    }

    public String getDescripcion(String token) {
        if (token == null) return "Token no reconocido";
        TokensDefinicion definicion = reservadas.get(token);
        if (definicion == null) definicion = reservadas.get(token.toLowerCase());
        if (definicion == null) definicion = reservadas.get(token.toUpperCase());
        return definicion != null ? definicion.getDescripcion() : "Token no reconocido";
    }

    public TokensDefinicion obtenerDefinicion(String token) {
        if (token == null) return null;
        TokensDefinicion def = reservadas.get(token);
        if (def == null) def = reservadas.get(token.toLowerCase());
        if (def == null) def = reservadas.get(token.toUpperCase());
        return def;
    }

    // =========================================================================
    // PREDICADOS GRAMATICALES (PARA QUE EL PARSER TRABAJE DIRECTO DESDE AQUÍ)
    // =========================================================================

    public boolean esComandoIA(String token) {
        return "COMANDO_IA".equals(mapeadorDeTokens(token));
    }

    public boolean esConector(String token) {
        return "CONECTORES".equals(mapeadorDeTokens(token));
    }

    public boolean esDirectiva(String token) {
        return "DIRECTIVA".equals(mapeadorDeTokens(token));
    }

    public boolean esPalabraReservada(String token) {
        return "PALABRA_RESERVADA".equals(mapeadorDeTokens(token));
    }

    public boolean esSimbolo(String token) {
        return "OPERADORES_LITERALES_COMENTARIOS_IDENTIFICADORES".equals(mapeadorDeTokens(token));
    }

    public Map<String, TokensDefinicion> getTodasLasDefiniciones() {
        return Collections.unmodifiableMap(reservadas);
    }
}
