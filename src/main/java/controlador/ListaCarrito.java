package controlador;

import modelo.ItemCarrito;
import modelo.Producto;

/**
 * Carrito y productos de cada pedido: lista doblemente encadenada (no circular).
 * La cabeza no tiene anterior y el último no tiene siguiente.
 */
public class ListaCarrito {

    public ItemCarrito cab;
    public ItemCarrito ult;   // último nodo: agregar al final no necesita recorrer la lista

    public ListaCarrito() { cab = ult = null; }

    public boolean getEsVacia() { return cab == null; }

    public int getLongLista() {
        int contador = 0;
        ItemCarrito actual = cab;
        while (actual != null) {
            contador++;
            actual = actual.sig;
        }
        return contador;
    }

    public ItemCarrito getUltimo() { return ult; }

    /** Busca el mismo producto en la misma talla y color. */
    public ItemCarrito getBuscar(Producto producto, String talla, String color) {
        ItemCarrito actual = cab;
        while (actual != null) {
            if (actual.getEsIgual(producto, talla, color)) return actual;
            actual = actual.sig;
        }
        return null;
    }

    public boolean getContieneProducto(Producto producto) {
        ItemCarrito actual = cab;
        while (actual != null) {
            if (actual.getProducto() == producto) return true;
            actual = actual.sig;
        }
        return false;
    }

    public void setAddF(ItemCarrito nuevo) {
        if (nuevo == null) return;
        if (getEsVacia()) {
            cab = ult = nuevo;
        } else {
            ult.sig = nuevo;
            nuevo.ant = ult;
            ult = nuevo;
        }
    }

    /** Elimina el nodo indicado (los cuatro casos de la lista doble). */
    public void setDelxNodo(ItemCarrito buscado) {
        if (getEsVacia() || buscado == null) return;
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

    public int getUnidades() {
        int unidades = 0;
        ItemCarrito actual = cab;
        while (actual != null) {
            unidades += actual.getCantidad();
            actual = actual.sig;
        }
        return unidades;
    }

    public long getTotal() {
        long total = 0;
        ItemCarrito actual = cab;
        while (actual != null) {
            total += actual.getSubtotal();
            actual = actual.sig;
        }
        return total;
    }

    /** Total sin descuentos (para mostrar cuánto se ahorra). */
    public long getTotalAnterior() {
        long total = 0;
        ItemCarrito actual = cab;
        while (actual != null) {
            total += actual.getSubtotalAnterior();
            actual = actual.sig;
        }
        return total;
    }
}

