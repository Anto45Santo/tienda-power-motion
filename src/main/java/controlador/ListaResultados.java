package controlador;

import modelo.NodoResultado;
import modelo.Producto;

import java.util.Comparator;

/**
 * Resultados del catálogo después de filtrar: lista doblemente encadenada circular.
 * Cada nodo apunta a un producto (no lo copia), así el catálogo no se altera.
 * Se usa para ordenar (inserción ordenada) y para paginar (getNodoEn).
 */
public class ListaResultados {

    public NodoResultado cab;
    private int cantidad;

    public ListaResultados() { cab = null; cantidad = 0; }

    public boolean getEsVacia() { return cab == null; }

    public int getLongLista() { return cantidad; }

    public void setAddF(Producto producto) {
        NodoResultado nuevo = new NodoResultado(producto);
        if (getEsVacia()) {
            cab = nuevo;
            cab.sig = cab.ant = cab;
        } else {
            nuevo.ant = cab.ant;
            nuevo.sig = cab;
            nuevo.ant.sig = nuevo;
            cab.ant = nuevo;
        }
        cantidad++;
    }

    /**
     * Inserta manteniendo el orden del criterio (ordenamiento por inserción).
     * Si el criterio es null, agrega al final (orden del catálogo).
     */
    public void setAddOrdenado(Producto producto, Comparator<Producto> criterio) {
        if (criterio == null || getEsVacia() || criterio.compare(producto, cab.ant.producto) >= 0) {
            setAddF(producto);                     // va al final
            return;
        }
        NodoResultado nuevo = new NodoResultado(producto);
        if (criterio.compare(producto, cab.producto) < 0) {
            // va antes de la cabeza y pasa a ser la nueva cabeza
            nuevo.sig = cab;
            nuevo.ant = cab.ant;
            cab.ant.sig = nuevo;
            cab.ant = nuevo;
            cab = nuevo;
        } else {
            NodoResultado actual = cab;
            while (actual.sig != cab && criterio.compare(producto, actual.sig.producto) >= 0)
                actual = actual.sig;
            nuevo.sig = actual.sig;
            nuevo.ant = actual;
            actual.sig.ant = nuevo;
            actual.sig = nuevo;
        }
        cantidad++;
    }

    /** Nodo en la posición indicada (0 = cabeza). Sirve para empezar cada página. */
    public NodoResultado getNodoEn(int posicion) {
        if (getEsVacia()) return null;
        NodoResultado actual = cab;
        for (int paso = 0; paso < posicion % cantidad; paso++) actual = actual.sig;
        return actual;
    }
}

