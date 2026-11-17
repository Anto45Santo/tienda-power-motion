package controlador;

import modelo.NodoMeGusta;

/**
 * "Me gusta" del usuario: pila sencillamente encadenada circular.
 * El último producto marcado queda en el tope, por eso aparece primero en la pantalla Me gusta.
 * La base (último nodo) apunta otra vez al tope.
 */
public class PilaMeGusta {

    public NodoMeGusta tope;
    public NodoMeGusta base;   // último nodo (apunta al tope): apilar no necesita recorrer la pila

    public PilaMeGusta() { tope = base = null; }

    public boolean getEsVacia() { return tope == null; }

    public int getLongPila() {
        int contador = 0;
        if (getEsVacia()) return 0;
        NodoMeGusta actual = tope;
        do {
            contador++;
            actual = actual.sig;
        } while (actual != tope);
        return contador;
    }

    public NodoMeGusta getBuscarxId(int idProducto) {
        if (getEsVacia()) return null;
        NodoMeGusta actual = tope;
        do {
            if (actual.idProducto == idProducto) return actual;
            actual = actual.sig;
        } while (actual != tope);
        return null;
    }

    /** Último nodo (el que apunta al tope). */
    public NodoMeGusta getBase() { return base; }

    /** Nodo que está antes del indicado. */
    public NodoMeGusta getAntesNodo(NodoMeGusta nodo) {
        if (getEsVacia()) return null;
        NodoMeGusta anterior = tope;
        while (anterior.sig != nodo) anterior = anterior.sig;
        return anterior;
    }

    /** Apila el producto si todavía no estaba. */
    public void setPush(int idProducto) {
        if (getBuscarxId(idProducto) != null) return;
        NodoMeGusta nuevo = new NodoMeGusta(idProducto);
        if (getEsVacia()) {
            tope = base = nuevo;
            tope.sig = tope;
        } else {
            nuevo.sig = tope;
            base.sig = nuevo;
            tope = nuevo;
        }
    }

    /** Agrega al fondo de la pila (se usa al cargar megusta.txt, que se guarda del tope a la base). */
    public void setAddBase(int idProducto) {
        if (getBuscarxId(idProducto) != null) return;
        if (getEsVacia()) {
            setPush(idProducto);
            return;
        }
        NodoMeGusta nuevo = new NodoMeGusta(idProducto);
        base.sig = nuevo;
        nuevo.sig = tope;
        base = nuevo;
    }

    /** Quita un producto de cualquier posición de la pila. */
    public void setDelxId(int idProducto) {
        NodoMeGusta buscado = getBuscarxId(idProducto);
        if (buscado == null) return;
        if (buscado == tope && tope.sig == tope) {
            tope = base = null;
        } else if (buscado == tope) {
            tope = tope.sig;
            base.sig = tope;
        } else {
            NodoMeGusta anterior = getAntesNodo(buscado);
            anterior.sig = buscado.sig;
            if (buscado == base) base = anterior;
        }
        buscado.sig = null;
    }

    public void setVaciar() { tope = base = null; }
}
