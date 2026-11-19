package vista;

import controlador.*;
import modelo.*;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class frmAdmin {
    @FXML private ToggleButton navPedidos, navProductos, navImagenes, navResenas, navUsuarios;
    @FXML private Label lblTitulo, lblSubtitulo, lblAdmin, lblAviso, lblIniciales;
    @FXML private StackPane contenido;
    private final ToggleGroup grupo = new ToggleGroup();
    private final PauseTransition ocultar = new PauseTransition(Duration.seconds(2.5));

    @FXML
    private void initialize() {
        ToggleButton[] opciones = {navPedidos, navProductos, navImagenes, navResenas, navUsuarios};
        for (ToggleButton opcion : opciones) opcion.setToggleGroup(grupo);   // los iconos están en Admin.fxml
        lblAdmin.setText(Sesion.getUsuario().getNombreCompleto());
        lblIniciales.setText(Sesion.getUsuario().getIniciales());
        ocultar.setOnFinished(evento -> lblAviso.setVisible(false));
        actualizarContadores();
    }

    public void mostrar(Parent pantalla, String fxml) {
        contenido.getChildren().removeIf(nodo -> nodo != lblAviso);
        contenido.getChildren().add(0, pantalla);
        String[] textos;
        switch (fxml) {
            case "AdminPedidos": textos = new String[]{"Pedidos", "Aprueba los pagos y gestiona los envíos."}; navPedidos.setSelected(true); break;
            case "AdminProductos": textos = new String[]{"Productos", "Inventario de calzado, ropa y accesorios."}; navProductos.setSelected(true); break;
            case "AdminProductoForm": textos = new String[]{"Producto", ""}; navProductos.setSelected(true); break;
            case "AdminImagenes": textos = new String[]{"Imágenes", "Fotos de la bienvenida y guías de tallas."}; navImagenes.setSelected(true); break;
            case "AdminResenas": textos = new String[]{"Reseñas", "Oculta o elimina opiniones de los clientes."}; navResenas.setSelected(true); break;
            default: textos = new String[]{"Usuarios", "Clientes registrados y administradores."}; navUsuarios.setSelected(true);
        }
        titulo(textos[0], textos[1]);
    }

    public void titulo(String titulo, String subtitulo) {
        lblTitulo.setText(titulo);
        lblSubtitulo.setText(subtitulo);
    }

    /** Números al lado de Pedidos (por aprobar) y Reseñas (ocultas). */
    public void actualizarContadores() {
        int porAprobar = Datos.pedidos.getContarxEstado(EstadoPedido.POR_APROBAR);
        int ocultas = 0;
        if (!Datos.catalogo.getEsVacia()) {
            Producto actual = Datos.catalogo.cab;
            do {
                ocultas += actual.getResenas().getContarOcultas();
                actual = actual.sig;
            } while (actual != Datos.catalogo.cab);
        }
        navPedidos.setText(porAprobar > 0 ? "Pedidos  (" + porAprobar + ")" : "Pedidos");
        navResenas.setText(ocultas > 0 ? "Reseñas  (" + ocultas + " ocultas)" : "Reseñas");
    }

    public void aviso(String texto) {
        lblAviso.setText(texto);
        lblAviso.setVisible(true);
        lblAviso.toFront();
        ocultar.playFromStart();
    }

    @FXML private void irPedidos() { Navegador.admin("AdminPedidos"); }
    @FXML private void irProductos() { Navegador.admin("AdminProductos"); }
    @FXML private void irImagenes() { Navegador.admin("AdminImagenes"); }
    @FXML private void irResenas() { Navegador.admin("AdminResenas"); }
    @FXML private void irUsuarios() { Navegador.admin("AdminUsuarios"); }
    @FXML private void cerrarSesion() { Sesion.setCerrar(); Navegador.ir("Login"); }
}
