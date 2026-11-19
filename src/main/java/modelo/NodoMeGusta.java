package modelo;

/** Nodo de la pila circular de "me gusta" (PilaMeGusta). Guarda el id del producto. */
public class NodoMeGusta {
    public int idProducto;
    public NodoMeGusta sig;

    public NodoMeGusta(int idProducto) {
        this.idProducto = idProducto;
        sig = null;
    }
}
