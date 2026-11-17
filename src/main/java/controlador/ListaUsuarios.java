package controlador;

import modelo.Usuario;

/** Usuarios registrados: lista doblemente encadenada (no circular). */
public class ListaUsuarios {

    public Usuario cab;
    public Usuario ult;   // último nodo: agregar al final no necesita recorrer la lista

    public ListaUsuarios() { cab = ult = null; }

    public boolean getEsVacia() { return cab == null; }

    public int getLongLista() {
        int contador = 0;
        Usuario actual = cab;
        while (actual != null) {
            contador++;
            actual = actual.sig;
        }
        return contador;
    }

    public Usuario getUltimo() { return ult; }

    public Usuario getBuscarxId(int id) {
        Usuario actual = cab;
        while (actual != null) {
            if (actual.getId() == id) return actual;
            actual = actual.sig;
        }
        return null;
    }

    public Usuario getBuscarxCorreo(String correo) {
        Usuario actual = cab;
        while (actual != null) {
            if (actual.getCorreo().equalsIgnoreCase(correo.trim())) return actual;
            actual = actual.sig;
        }
        return null;
    }

    /** Login: devuelve el usuario si el correo y la contraseña coinciden. */
    public Usuario getValidar(String correo, String clave) {
        Usuario encontrado = getBuscarxCorreo(correo);
        if (encontrado != null && Seguridad.getCoincide(clave, encontrado.getClave())) return encontrado;
        return null;
    }

    public int getSiguienteId() {
        int mayor = 0;
        Usuario actual = cab;
        while (actual != null) {
            if (actual.getId() > mayor) mayor = actual.getId();
            actual = actual.sig;
        }
        return mayor + 1;
    }

    public int getContarAdmins() {
        int contador = 0;
        Usuario actual = cab;
        while (actual != null) {
            if (actual.isAdmin()) contador++;
            actual = actual.sig;
        }
        return contador;
    }

    public void setAddF(Usuario nuevo) {
        if (nuevo == null) return;
        if (getEsVacia()) {
            cab = ult = nuevo;
        } else {
            ult.sig = nuevo;
            nuevo.ant = ult;
            ult = nuevo;
        }
    }

    public boolean setDelxId(int id) {
        Usuario buscado = getBuscarxId(id);
        if (buscado == null) return false;
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
        return true;
    }
}
