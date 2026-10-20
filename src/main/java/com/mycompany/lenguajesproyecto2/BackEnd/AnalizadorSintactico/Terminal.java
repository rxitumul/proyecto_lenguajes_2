package com.mycompany.lenguajesproyecto2.BackEnd.AnalizadorSintactico;

/**
 * Terminales oficiales del lenguaje de programación Promptzal.
 */
public final class Terminal {

    private Terminal() {
        // Clase de constantes, no instanciable
    }

    public static final String DIRECTIVA   = "DIRECTIVA";
    public static final String AGENTE      = "AGENTE";
    public static final String CONTEXTO    = "CONTEXTO";
    public static final String VARIABLE    = "VARIABLE";
    public static final String EJECUTAR    = "EJECUTAR";
    public static final String EXPORTAR    = "EXPORTAR";
    public static final String COMANDO_IA  = "COMANDO_IA";
    public static final String CARGAR      = "CARGAR";
    public static final String CONECTOR    = "CONECTOR";
    public static final String FLECHA      = "FLECHA";
    public static final String IGUAL       = "IGUAL";
    public static final String MAS         = "MAS";
    public static final String LLAVE_A     = "LLAVE_A";
    public static final String LLAVE_C     = "LLAVE_C";
    public static final String PAR_A       = "PAR_A";
    public static final String PAR_C       = "PAR_C";
    public static final String COMA        = "COMA";
    public static final String ID          = "ID";
    public static final String CADENA      = "CADENA";
    public static final String NUMERO      = "NUMERO";
    public static final String EOF         = "EOF";

    /**
     * Normaliza un tipo de token recibido a la nomenclatura oficial,
     * permitiendo compatibilidad bidireccional con alias de lexers previos.
     *
     * @param tipo Tipo de token original
     * @return Terminal oficial correspondiente
     */
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
