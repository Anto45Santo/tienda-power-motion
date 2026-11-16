package controlador;

import modelo.NodoImagen;

/**
 * Imágenes: lista doblemente encadenada circular.
 * Se usa en la galería de cada producto (flechas ‹ › con ant y sig, dan la vuelta solas)
 * y en las fotos que rotan en la bienvenida. La cabeza es la portada.
 */
public class ListaImagenes {

    public NodoImagen cab;

    public ListaImagenes() { cab = null; }

    public boolean getEsVacia() { return cab == null; }

    public int getLongLista() {
        int contador = 0;
        if (getEsVacia()) return 0;
        NodoImagen actual = cab;
        do {
            contador++;
            actual = actual.sig;
        } while (actual != cab);
        return contador;
    }

    public NodoImagen getBuscarxArchivo(String archivo) {
        if (getEsVacia()) return null;
        NodoImagen actual = cab;
        do {
            if (archivo.equals(actual.archivo)) return actual;
            actual = actual.sig;
        } while (actual != cab);
        return null;
    }

    public void setAddF(String archivo) {
        NodoImagen nuevo = new NodoImagen(archivo);
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

    public void setDelxArchivo(String archivo) {
        NodoImagen buscado = getBuscarxArchivo(archivo);
        if (buscado == null) return;
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
    }

    /** Deja la imagen indicada como cabeza (portada) sin cambiar el orden circular. */
    public void setPortada(String archivo) {
        NodoImagen buscado = getBuscarxArchivo(archivo);
        if (buscado != null) cab = buscado;
    }

    public void setVaciar() { cab = null; }
}
