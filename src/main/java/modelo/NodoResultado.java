package modelo;

/**
 * Nodo de la lista doble circular de resultados (ListaResultados).
 * Apunta a un producto del catálogo sin tocar sus enlaces, para filtrar, ordenar y paginar.
 */
public class NodoResultado {
    public Producto producto;
    public NodoResultado sig, ant;

    public NodoResultado(Producto producto) {
        this.producto = producto;
        sig = ant = null;
    }
}
