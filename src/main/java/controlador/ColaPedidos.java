package controlador;

import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.Usuario;

/**
 * Pedidos: cola doblemente encadenada circular.
 * Los pedidos entran por el final (setEncolar) y el administrador los atiende desde el inicio
 * (el más antiguo primero). Mis compras recorre desde inicio.ant hacia atrás (el más reciente primero).
 */
public class ColaPedidos {

    public Pedido inicio;

    public ColaPedidos() { inicio = null; }

    public boolean getEsVacia() { return inicio == null; }

    public int getLongCola() {
        int contador = 0;
        if (getEsVacia()) return 0;
        Pedido actual = inicio;
        do {
            contador++;
            actual = actual.sig;
        } while (actual != inicio);
        return contador;
    }

    public Pedido getBuscarxNumero(int numero) {
        if (getEsVacia()) return null;
        Pedido actual = inicio;
        do {
            if (actual.getNumero() == numero) return actual;
            actual = actual.sig;
        } while (actual != inicio);
        return null;
    }

    /** Número para un pedido nuevo: el del último + 1. */
    public int getSiguienteNumero() {
        return getEsVacia() ? 10001 : inicio.ant.getNumero() + 1;
    }

    /** Entra al final de la cola. */
    public void setEncolar(Pedido nuevo) {
        if (nuevo == null) return;
        if (getEsVacia()) {
            inicio = nuevo;
            inicio.sig = inicio.ant = inicio;
        } else {
            nuevo.ant = inicio.ant;
            nuevo.sig = inicio;
            nuevo.ant.sig = nuevo;
            inicio.ant = nuevo;
        }
    }

    /** El pedido "Por aprobar" más antiguo (el siguiente que debe atender el administrador). */
    public Pedido getSiguientePorAtender() {
        if (getEsVacia()) return null;
        Pedido actual = inicio;
        do {
            if (actual.getEstado() == EstadoPedido.POR_APROBAR) return actual;
            actual = actual.sig;
        } while (actual != inicio);
        return null;
    }

    public int getContarxEstado(EstadoPedido estado) {
        int contador = 0;
        if (getEsVacia()) return 0;
        Pedido actual = inicio;
        do {
            if (actual.getEstado() == estado) contador++;
            actual = actual.sig;
        } while (actual != inicio);
        return contador;
    }

    public int getContarxCliente(Usuario cliente) {
        int contador = 0;
        if (getEsVacia()) return 0;
        Pedido actual = inicio;
        do {
            if (actual.getCliente() == cliente) contador++;
            actual = actual.sig;
        } while (actual != inicio);
        return contador;
    }

    public boolean getTieneEnProceso(Usuario cliente) {
        if (getEsVacia()) return false;
        Pedido actual = inicio;
        do {
            if (actual.getCliente() == cliente && actual.getEnProceso()) return true;
            actual = actual.sig;
        } while (actual != inicio);
        return false;
    }

    /** Ventas: suma de los pedidos aprobados, enviados o entregados. */
    public long getTotalVentas() {
        long total = 0;
        if (getEsVacia()) return 0;
        Pedido actual = inicio;
        do {
            if (actual.getEstado() != EstadoPedido.POR_APROBAR && actual.getEstado() != EstadoPedido.RECHAZADO)
                total += actual.getTotal();
            actual = actual.sig;
        } while (actual != inicio);
        return total;
    }

    public int getContarDeHoy() {
        int contador = 0;
        if (getEsVacia()) return 0;
        Pedido actual = inicio;
        do {
            if (actual.getFecha().startsWith(Fechas.getHoy())) contador++;
            actual = actual.sig;
        } while (actual != inicio);
        return contador;
    }

    /** true si el cliente ya recibió (Entregado) ese producto: así puede escribir una opinión. */
    public boolean getComproYRecibio(Usuario cliente, modelo.Producto producto) {
        if (getEsVacia()) return false;
        Pedido actual = inicio;
        do {
            if (actual.getCliente() == cliente && actual.getEstado() == EstadoPedido.ENTREGADO
                    && actual.getItems().getContieneProducto(producto)) return true;
            actual = actual.sig;
        } while (actual != inicio);
        return false;
    }
}
