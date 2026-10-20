package com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas;

import java.io.Serializable;

public class Nodo<T> implements Serializable {

    private T contenidoLocal;
    private Nodo<T> siguienteLocal;
    private Nodo<T> anteriorLocal;

    public Nodo(T contenido) {
        contenidoLocal = contenido;
    }

    public T getContenido() {
        return contenidoLocal;
    }

    public Nodo<T> getSiguiente() {
        return siguienteLocal;
    }

    public Nodo<T> getAnterior() {
        return anteriorLocal;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        siguienteLocal = siguiente;
    }

    public void setAnterior(Nodo<T> anterior) {
        anteriorLocal = anterior;
    }

    public void setContenido(T contenido) {
        contenidoLocal = contenido;
    }
}
