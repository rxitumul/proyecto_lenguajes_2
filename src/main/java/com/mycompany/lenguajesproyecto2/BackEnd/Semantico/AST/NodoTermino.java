package com.mycompany.lenguajesproyecto2.BackEnd.Semantico.AST;

/**
 * Nodo AST que representa un término atómico dentro de una expresión.
 */
public class NodoTermino extends NodoAST {

    public enum TipoTermino {
        CADENA,
        ID,
        NUMERO,
        CARGAR
    }

    private final TipoTermino tipo;
    private final String valor;
    private final String unidad;       // Para números con sufijo de unidad (ej. "250 palabras")
    private final String rutaArchivo;  // Para funciones CARGAR("archivo.txt")

    public NodoTermino(TipoTermino tipo, String valor, int fila, int columna) {
        this(tipo, valor, null, null, fila, columna);
    }

    public NodoTermino(TipoTermino tipo, String valor, String unidad, String rutaArchivo, int fila, int columna) {
        super(fila, columna);
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad;
        this.rutaArchivo = rutaArchivo;
    }

    public static NodoTermino crearCadena(String valor, int fila, int columna) {
        return new NodoTermino(TipoTermino.CADENA, valor, fila, columna);
    }

    public static NodoTermino crearId(String identificador, int fila, int columna) {
        return new NodoTermino(TipoTermino.ID, identificador, fila, columna);
    }

    public static NodoTermino crearNumero(String numero, String unidad, int fila, int columna) {
        return new NodoTermino(TipoTermino.NUMERO, numero, unidad, null, fila, columna);
    }

    public static NodoTermino crearCargar(String rutaArchivo, int fila, int columna) {
        return new NodoTermino(TipoTermino.CARGAR, "CARGAR", null, rutaArchivo, fila, columna);
    }

    public TipoTermino getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public boolean tieneUnidad() {
        return unidad != null && !unidad.trim().isEmpty();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public boolean esIdentificador() {
        return tipo == TipoTermino.ID;
    }

    @Override
    public String toString() {
        switch (tipo) {
            case CADENA:
                return valor;
            case ID:
                return valor;
            case NUMERO:
                return tieneUnidad() ? valor + " " + unidad : valor;
            case CARGAR:
                return "CARGAR(" + rutaArchivo + ")";
            default:
                return valor;
        }
    }
}
