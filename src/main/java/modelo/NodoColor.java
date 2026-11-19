package modelo;

/** Nodo de la lista doble de colores (ListaColores). */
public class NodoColor {
    public String nombre;
    public NodoColor sig, ant;

    public NodoColor(String nombre) {
        this.nombre = nombre;
        sig = ant = null;
    }
}