package modelo;

/** Nodo de la lista doble circular de imágenes (ListaImagenes). Solo guarda el nombre del archivo. */
public class NodoImagen {
    public String archivo;
    public NodoImagen sig, ant;

    public NodoImagen(String archivo) {
        this.archivo = archivo;
        sig = ant = null;
    }
}
