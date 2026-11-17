package controlador;

import modelo.Resena;

/**
 * Reseñas de un producto: pila sencillamente encadenada circular.
 * La reseña más reciente queda en el tope; el detalle del producto muestra las dos primeras.
 */
public class PilaResenas {

    public Resena tope;
    public Resena base;   // último nodo (apunta al tope): apilar no necesita recorrer la pila

    public PilaResenas() { tope = base = null; }

    public boolean getEsVacia() { return tope == null; }

    public int getLongPila() {
        int contador = 0;
        if (getEsVacia()) return 0;
        Resena actual = tope;
        do {
            contador++;
            actual = actual.sig;
        } while (actual != tope);
        return contador;
    }

    public Resena getBase() { return base; }

    public Resena getAntesNodo(Resena nodo) {
        if (getEsVacia()) return null;
        Resena anterior = tope;
        while (anterior.sig != nodo) anterior = anterior.sig;
        return anterior;
    }

    public void setPush(Resena nueva) {
        if (nueva == null) return;
        if (getEsVacia()) {
            tope = base = nueva;
            tope.sig = tope;
        } else {
            nueva.sig = tope;
            base.sig = nueva;
            tope = nueva;
        }
    }

    /** Agrega al fondo (se usa al cargar las reseñas en orden del más reciente al más antiguo). */
    public void setAddBase(Resena nueva) {
        if (getEsVacia()) {
            setPush(nueva);
        } else {
            base.sig = nueva;
            nueva.sig = tope;
            base = nueva;
        }
    }

    public void setDelxNodo(Resena buscada) {
        if (getEsVacia() || buscada == null) return;
        if (buscada == tope && tope.sig == tope) {
            tope = base = null;
        } else if (buscada == tope) {
            tope = tope.sig;
            base.sig = tope;
        } else {
            Resena anterior = getAntesNodo(buscada);
            anterior.sig = buscada.sig;
            if (buscada == base) base = anterior;
        }
        buscada.sig = null;
    }

    public int getContarVisibles() {
        int contador = 0;
        if (getEsVacia()) return 0;
        Resena actual = tope;
        do {
            if (actual.isVisible()) contador++;
            actual = actual.sig;
        } while (actual != tope);
        return contador;
    }

    public int getContarOcultas() {
        return getLongPila() - getContarVisibles();
    }

    public double getPromedioVisibles() {
        int contador = 0, suma = 0;
        if (getEsVacia()) return 0;
        Resena actual = tope;
        do {
            if (actual.isVisible()) {
                suma += actual.getEstrellas();
                contador++;
            }
            actual = actual.sig;
        } while (actual != tope);
        return contador == 0 ? 0 : (double) suma / contador;
    }
}
