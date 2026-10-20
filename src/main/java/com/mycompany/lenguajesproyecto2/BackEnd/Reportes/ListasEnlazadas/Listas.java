package com.mycompany.lenguajesproyecto2.BackEnd.Reportes.ListasEnlazadas;

public class Listas<T> {

    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int capacidad;

    public Listas() {
        capacidad = 0;
    }

    public void agregarAlFinal(T contenido) {
        Nodo<T> nuevo = new Nodo<T>(contenido);
        if (esVacia()) {
            inicio = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            nuevo.setAnterior(fin);
        }
        fin = nuevo;
        capacidad++;
    }

    public boolean esVacia() {
        return inicio == null;
    }

    public T obtenerContenido(int index) throws ListaEnlazadaException {
        Nodo<T> nodoBuscado = obtenerNodo(index);
        return nodoBuscado.getContenido();
    }

    private Nodo<T> obtenerNodo(int index) throws ListaEnlazadaException {
        if (index < 0 || index >= capacidad) {
            throw new ListaEnlazadaException("El indice esta fuera de rango, porfavor intente denuevo");
        }
        Nodo<T> actual = inicio;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguiente();
        }
        return actual;
    }

    public void eliminar(int index) throws ListaEnlazadaException {
        if (index < 0 || index >= capacidad) {
            throw new ListaEnlazadaException("El indice esta fuera de rango, porfavor intente denuevo");
        }

        if (index == 0) {
            inicio = inicio.getSiguiente();

            if (inicio == null) {
                fin = null;
            }
        } else {
            Nodo<T> nodoAEliminar = obtenerNodo(index);
            Nodo<T> anterior = nodoAEliminar.getAnterior();
            Nodo<T> siguiente = nodoAEliminar.getSiguiente();

            anterior.setSiguiente(siguiente);
            if (siguiente != null) {
                siguiente.setAnterior(anterior);
            } else {
                fin = anterior;
            }
        }
        capacidad--;
    }

    public void eliminarUltimo() throws ListaEnlazadaException {
        if (esVacia()) {
            throw new ListaEnlazadaException("La lista esta vacia");
        }

        if (capacidad == 1) {
            inicio = null;
            fin = null;
        } else {
            Nodo<T> penultimo = obtenerNodo(capacidad - 2);
            penultimo.setSiguiente(null);
            fin = penultimo;
        }
        capacidad--;
    }

    public boolean contiene(T contenido) {
        Nodo<T> actual = inicio;
        while (actual != null) {
            if (actual.getContenido() != null && actual.getContenido().equals(contenido)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public void limpiar() {
        inicio = null;
        fin = null;
        capacidad = 0;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
