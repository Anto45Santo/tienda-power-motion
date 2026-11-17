package controlador;

import modelo.Variante;

/** Stock de un producto (una variante por talla y color): lista doblemente encadenada (no circular). */
public class ListaVariantes {

    public Variante cab;
    public Variante ult;   // último nodo: agregar al final no necesita recorrer la lista

    public ListaVariantes() { cab = ult = null; }

    public boolean getEsVacia() { return cab == null; }

    public Variante getUltimo() { return ult; }

    public Variante getBuscar(String talla, String color) {
        Variante actual = cab;
        while (actual != null) {
            if (actual.getTalla().equals(talla) && actual.getColor().equals(color)) return actual;
            actual = actual.sig;
        }
        return null;
    }

    /** true si la talla está registrada (aunque tenga 0 unidades). */
    public boolean getTieneTalla(String talla) {
        Variante actual = cab;
        while (actual != null) {
            if (actual.getTalla().equals(talla)) return true;
            actual = actual.sig;
        }
        return false;
    }

    /** true si la talla tiene unidades en algún color. */
    public boolean getHayStockTalla(String talla) {
        Variante actual = cab;
        while (actual != null) {
            if (actual.getTalla().equals(talla) && actual.getCantidad() > 0) return true;
            actual = actual.sig;
        }
        return false;
    }

    public int getTotalUnidades() {
        int total = 0;
        Variante actual = cab;
        while (actual != null) {
            total += actual.getCantidad();
            actual = actual.sig;
        }
        return total;
    }

    public void setAddF(Variante nueva) {
        if (nueva == null) return;
        if (getEsVacia()) {
            cab = ult = nueva;
        } else {
            ult.sig = nueva;
            nueva.ant = ult;
            ult = nueva;
        }
    }

    /** Resta unidades al aprobar un pedido (nunca deja negativos). */
    public void setDescontar(String talla, String color, int unidades) {
        Variante encontrada = getBuscar(talla, color);
        if (encontrada != null) encontrada.setCantidad(Math.max(0, encontrada.getCantidad() - unidades));
    }

    public void setVaciar() { cab = ult = null; }
}

