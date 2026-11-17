package controlador;

import modelo.Producto;

/**
 * Catálogo de productos: lista doblemente encadenada circular.
 * El último nodo apunta a la cabeza (sig) y la cabeza al último (ant).
 */
public class ListaProductos {

    public Producto cab;

    public ListaProductos() { cab = null; }

    public boolean getEsVacia() { return cab == null; }

    public int getLongLista() {
        int contador = 0;
        if (getEsVacia()) return 0;
        Producto actual = cab;
        do {
            contador++;
            actual = actual.sig;
        } while (actual != cab);
        return contador;
    }

    public Producto getBuscarxId(int id) {
        if (getEsVacia()) return null;
        Producto actual = cab;
        do {
            if (actual.getId() == id) return actual;   // encontrado
            actual = actual.sig;                       // sigue buscando
        } while (actual != cab);
        return null;                                   // no encontrado
    }

    /** Id para un producto nuevo: el mayor id registrado + 1. */
    public int getSiguienteId() {
        int mayor = 0;
        if (!getEsVacia()) {
            Producto actual = cab;
            do {
                if (actual.getId() > mayor) mayor = actual.getId();
                actual = actual.sig;
            } while (actual != cab);
        }
        return mayor + 1;
    }

    /** Agrega al final (antes de la cabeza). */
    public void setAddF(Producto nuevo) {
        if (nuevo == null) return;
        if (getEsVacia()) {
            cab = nuevo;
            cab.sig = cab.ant = cab;
        } else {
            nuevo.ant = cab.ant;
            nuevo.sig = cab;
            nuevo.ant.sig = nuevo;
            cab.ant = nuevo;
        }
    }

    /**
     * Elimina por id. En la tienda no se usa (los productos se desactivan para no
     * dañar los pedidos antiguos), pero se deja para el manejo completo de la estructura.
     */
    public boolean setDelxId(int id) {
        Producto buscado = getBuscarxId(id);
        if (buscado == null) return false;
        if (buscado == cab && cab.sig == cab) {
            cab = null;
        } else if (buscado == cab) {
            cab = cab.sig;
            cab.ant = buscado.ant;
            cab.ant.sig = cab;
        } else if (buscado.sig == cab) {
            buscado.ant.sig = cab;
            cab.ant = buscado.ant;
        } else {
            buscado.ant.sig = buscado.sig;
            buscado.sig.ant = buscado.ant;
        }
        buscado.sig = buscado.ant = null;
        return true;
    }

    /** Cuántos productos activos están agotados o con menos de 10 unidades. */
    public int getContarBajoStock() {
        int contador = 0;
        if (getEsVacia()) return 0;
        Producto actual = cab;
        do {
            if (actual.isActivo() && actual.getStockTotal() < 10) contador++;
            actual = actual.sig;
        } while (actual != cab);
        return contador;
    }
}
