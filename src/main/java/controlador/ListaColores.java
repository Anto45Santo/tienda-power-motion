package controlador;

import modelo.NodoColor;

/** Colores de un producto: lista doblemente encadenada (no circular). */
public class ListaColores {

    public NodoColor cab;
    public NodoColor ult;   // último nodo: agregar al final no necesita recorrer la lista

    public ListaColores() { cab = ult = null; }

    public boolean getEsVacia() { return cab == null; }

    public int getLongLista() {
        int contador = 0;
        NodoColor actual = cab;
        while (actual != null) {
            contador++;
            actual = actual.sig;
        }
        return contador;
    }

    public NodoColor getUltimo() { return ult; }

    /** No distingue mayúsculas ni espacios: "azul " y "Azul" son el mismo color. */
    public NodoColor getBuscar(String nombre) {
        NodoColor actual = cab;
        while (actual != null) {
            if (actual.nombre.equalsIgnoreCase(nombre.trim())) return actual;
            actual = actual.sig;
        }
        return null;
    }

    /** Agrega al final si no existe. Devuelve false si ya estaba. */
    public boolean setAddF(String nombre) {
        if (getBuscar(nombre) != null) return false;
        NodoColor nuevo = new NodoColor(nombre.trim());
        if (getEsVacia()) {
            cab = ult = nuevo;
        } else {
            ult.sig = nuevo;
            nuevo.ant = ult;
            ult = nuevo;
        }
        return true;
    }

    public void setDelxNombre(String nombre) {
        NodoColor buscado = getBuscar(nombre);
        if (buscado == null) return;
        if (buscado == cab && cab.sig == null) {
            cab = ult = null;
        } else if (buscado == cab) {
            cab = cab.sig;
            cab.ant = null;
        } else if (buscado.sig == null) {
            buscado.ant.sig = null;
            ult = buscado.ant;
        } else {
            buscado.ant.sig = buscado.sig;
            buscado.sig.ant = buscado.ant;
        }
        buscado.sig = buscado.ant = null;
    }

    public void setVaciar() { cab = ult = null; }
}
