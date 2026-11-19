package vista;

import controlador.*;
import modelo.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Tarjeta de una prenda en el catálogo: foto, me gusta, nombre, precio y botón Comprar.
 * El diseño está en TarjetaProducto.fxml (fx:root): esta clase lo carga y pone los datos del producto.
 */
public class TarjetaProducto extends VBox {
    @FXML private ImageView imgFoto;
    @FXML private Label lblEtiqueta, lblNombre, lblPrecio;
    @FXML private Text txtAntes;
    @FXML private ToggleButton btnMeGusta;
    @FXML private Button btnComprar;
    private final Producto producto;
    private final Runnable alCambiarMeGusta;

    public TarjetaProducto(Producto producto, Runnable alCambiarMeGusta) {
        this.producto = producto;
        this.alCambiarMeGusta = alCambiarMeGusta;
        FXMLLoader cargador = new FXMLLoader(TarjetaProducto.class.getResource("TarjetaProducto.fxml"));
        cargador.setRoot(this);
        cargador.setController(this);
        try {
            cargador.load();
        } catch (IOException error) {
            throw new RuntimeException("No se pudo abrir TarjetaProducto.fxml", error);
        }
        imgFoto.setImage(Imagenes.producto(producto.getPortada(), 268));
        // Etiqueta: descuento, "NUEVO" o ninguna
        if (producto.getDescuento() > 0) {
            lblEtiqueta.setText("-" + producto.getDescuento() + "%");
        } else if (producto.isNuevo()) {
            lblEtiqueta.setText("NUEVO");
            lblEtiqueta.getStyleClass().setAll("label", "badge-oscuro");
        } else {
            lblEtiqueta.setVisible(false);
        }
        btnMeGusta.setSelected(Sesion.getLeGusta(producto));       // el CSS pinta el corazón lleno si está marcado
        btnMeGusta.setOnMouseClicked(evento -> evento.consume());   // que no abra el detalle
        lblNombre.setText(producto.getNombre());
        lblPrecio.setText(Formato.cop(producto.getPrecio()));
        if (producto.getPrecioAnterior() > 0) txtAntes.setText(Formato.cop(producto.getPrecioAnterior()));
        else { txtAntes.setVisible(false); txtAntes.setManaged(false); }
        if (producto.getStockTotal() == 0) {
            btnComprar.setText("Agotado");
            btnComprar.setGraphic(null);
            btnComprar.setDisable(true);
        }
        setOnMouseClicked(evento -> abrirDetalle(producto));
    }

    @FXML
    private void alternarMeGusta() {
        btnMeGusta.setSelected(Sesion.setAlternarMeGusta(producto));
        if (alCambiarMeGusta != null) alCambiarMeGusta.run();
    }

    /** Talla única con un solo color: va directo al carrito. Si hay que elegir talla o color, abre el detalle. */
    @FXML
    private void comprar(ActionEvent evento) {
        evento.consume();
        if (producto.getTipoTalla() == TipoTalla.UNICA && producto.getColores().getLongLista() == 1) {
            String color = producto.getColores().cab.nombre;
            if (Sesion.setAgregarAlCarrito(producto, "UNICA", color, 1)) Navegador.aviso("Agregado al carrito: " + producto.getNombre());
            else Navegador.aviso("No hay más unidades disponibles.");
        } else {
            abrirDetalle(producto);
        }
    }

    public static void abrirDetalle(Producto producto) {
        frmProducto pantalla = Navegador.tienda("Producto");
        pantalla.mostrar(producto);
    }
}
