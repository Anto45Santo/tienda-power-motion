package modelo;

/** Nodo de la pila circular de reseñas (PilaResenas). Solo enlace "sig", como la pila del docente. */
public class Resena {
    private final String autor, fecha, texto;
    private final int estrellas;
    private boolean visible = true;
    private final Producto producto;
    public Resena sig;

    public Resena(Producto producto, String autor, int estrellas, String fecha, String texto) {
        this.producto = producto; this.autor = autor; this.estrellas = estrellas; this.fecha = fecha; this.texto = texto;
        sig = null;
    }

    public String getAutor() { return autor; }
    public int getEstrellas() { return estrellas; }
    public String getFecha() { return fecha; }
    public String getTexto() { return texto; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public Producto getProducto() { return producto; }
}
