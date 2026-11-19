package modelo;

/** Nodo de la lista doble de stock (ListaVariantes). En stock.txt: idProducto;talla;color;cantidad */
public class Variante {
    private final String talla, color;
    private int cantidad;
    public Variante sig, ant;

    public Variante(String talla, String color, int cantidad) {
        this.talla = talla; this.color = color; this.cantidad = cantidad;
        sig = ant = null;
    }

    public String getTalla() { return talla; }
    public String getColor() { return color; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
