package controlador;

import modelo.ArchivoTexto;
import modelo.ItemCarrito;
import modelo.Producto;
import modelo.Usuario;

import java.io.IOException;

/** Usuario conectado, su carrito (lista doble) y sus "me gusta" (pila circular). */
public final class Sesion {
    private static Usuario usuario;
    private static final ListaCarrito carrito = new ListaCarrito();
    private static final PilaMeGusta meGusta = new PilaMeGusta();
    /** La vista se registra aquí para refrescar los contadores de la barra. */
    public static Runnable alCambiar = () -> {};

    private Sesion() {}

    public static void setIniciar(Usuario conectado) {
        usuario = conectado;
        carrito.setVaciar();
        meGusta.setVaciar();
        if (conectado.isAdmin()) return;
        try {                         // lo que el cliente dejó guardado la última vez
            ArchivoTexto.cargarMeGusta(conectado.getId(), meGusta);
            ArchivoTexto.cargarCarrito(conectado.getId(), carrito, Datos.catalogo);
        } catch (IOException | RuntimeException problema) {
            System.err.println("No se pudo leer megusta.txt / carrito.txt: " + problema.getMessage());
        }
    }

    /** Reescribe las líneas de este usuario en megusta.txt. */
    public static void setGuardarMeGusta() {
        if (usuario == null) return;
        try { ArchivoTexto.setGuardarMeGusta(usuario.getId(), meGusta); }
        catch (IOException problema) { Datos.error = "No se pudo guardar megusta.txt"; }
    }

    /** Reescribe las líneas de este usuario en carrito.txt (el carrito se conserva al cerrar la app). */
    public static void setGuardarCarrito() {
        if (usuario == null) return;
        try { ArchivoTexto.setGuardarCarrito(usuario.getId(), carrito); }
        catch (IOException problema) { Datos.error = "No se pudo guardar carrito.txt"; }
    }

    /** Borra del archivo los "me gusta" y el carrito de un usuario eliminado. */
    public static void setBorrarDatosDe(int idUsuario) {
        try {
            ArchivoTexto.setGuardarMeGusta(idUsuario, new PilaMeGusta());
            ArchivoTexto.setGuardarCarrito(idUsuario, new ListaCarrito());
        } catch (IOException problema) { Datos.error = "No se pudieron limpiar los datos del usuario"; }
    }

    public static void setCerrar() {
        usuario = null;
        carrito.setVaciar();
        meGusta.setVaciar();
    }

    public static Usuario getUsuario() { return usuario; }
    public static ListaCarrito getCarrito() { return carrito; }
    public static PilaMeGusta getMeGusta() { return meGusta; }

    public static boolean getLeGusta(Producto producto) {
        return meGusta.getBuscarxId(producto.getId()) != null;
    }

    /** Marca o desmarca "me gusta" y devuelve el nuevo estado. */
    public static boolean setAlternarMeGusta(Producto producto) {
        boolean ahora;
        if (getLeGusta(producto)) {
            meGusta.setDelxId(producto.getId());
            ahora = false;
        } else {
            meGusta.setPush(producto.getId());
            ahora = true;
        }
        setGuardarMeGusta();
        alCambiar.run();
        return ahora;
    }

    /** Agrega al carrito sin pasar del stock. Devuelve false si no hay unidades suficientes. */
    public static boolean setAgregarAlCarrito(Producto producto, String talla, String color, int cantidad) {
        int disponibles = producto.getStockDe(talla, color);
        ItemCarrito existente = carrito.getBuscar(producto, talla, color);
        int yaEnCarrito = existente == null ? 0 : existente.getCantidad();
        if (yaEnCarrito + cantidad > disponibles) return false;
        if (existente == null) carrito.setAddF(new ItemCarrito(producto, talla, color, cantidad));
        else existente.setCantidad(yaEnCarrito + cantidad);
        setGuardarCarrito();
        alCambiar.run();
        return true;
    }
}
