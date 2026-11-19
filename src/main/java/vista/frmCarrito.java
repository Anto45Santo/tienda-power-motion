package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/** 08 · Carrito (lista doblemente encadenada). */
public class frmCarrito {
    public static final long ENVIO_GRATIS_DESDE = 400_000, COSTO_ENVIO = 15_000;

    @FXML private Hyperlink lnkVolver;
    @FXML private Label lblTitulo, lblCantidad, lblSubtotal, lblDescuentos, lblEnvio, lblTotal;
    @FXML private VBox listaItems, boxVacio;
    @FXML private HBox boxConProductos;

    @FXML
    private void initialize() {
        refrescar();
    }

    public static long getEnvio(long total) {
        return total >= ENVIO_GRATIS_DESDE ? 0 : COSTO_ENVIO;
    }

    private void refrescar() {
        ListaCarrito carrito = Sesion.getCarrito();
        int unidades = carrito.getUnidades();
        lblTitulo.setText("Carrito (" + unidades + ")");
        boxVacio.setVisible(carrito.getEsVacia());
        boxConProductos.setVisible(!carrito.getEsVacia());
        listaItems.getChildren().clear();
        ItemCarrito actual = carrito.cab;
        while (actual != null) {                    // recorrido de la lista doble
            listaItems.getChildren().add(fila(actual));
            actual = actual.sig;
        }
        long total = carrito.getTotal(), sinDescuento = carrito.getTotalAnterior();
        lblCantidad.setText("Productos (" + unidades + ")");
        lblSubtotal.setText(Formato.cop(sinDescuento));
        lblDescuentos.setText(sinDescuento > total ? "− " + Formato.cop(sinDescuento - total) : Formato.cop(0));
        long envio = getEnvio(total);
        lblEnvio.setText(envio == 0 ? "Gratis" : Formato.cop(envio));
        lblEnvio.getStyleClass().setAll("label", envio == 0 ? "verde" : "negrita");
        lblTotal.setText(Formato.cop(total + envio));
        Navegador.actualizarBarra();
    }

    private HBox fila(ItemCarrito item) {
        Label nombre = new Label(item.getProducto().getNombre());
        nombre.getStyleClass().add("subtitulo");
        Label detalle = new Label(item.getTallaTexto() + " · " + item.getColor());
        detalle.getStyleClass().add("gris");
        Hyperlink eliminar = new Hyperlink("Eliminar");
        eliminar.getStyleClass().add("link-rojo");
        eliminar.setOnAction(evento -> {
            Sesion.getCarrito().setDelxNodo(item);
            Sesion.setGuardarCarrito();
            refrescar();
        });
        Hyperlink mover = new Hyperlink("Mover a Me gusta");
        mover.setOnAction(evento -> {
            if (!Sesion.getLeGusta(item.getProducto())) Sesion.setAlternarMeGusta(item.getProducto());
            Sesion.getCarrito().setDelxNodo(item);
            Sesion.setGuardarCarrito();
            refrescar();
            Navegador.aviso("Guardado en Me gusta");
        });
        HBox enlaces = new HBox(14, eliminar, mover);
        VBox informacion = new VBox(4, nombre, detalle, enlaces);
        VBox.setMargin(enlaces, new Insets(10, 0, 0, 0));

        Spinner<Integer> cantidad = new Spinner<>(1, Math.max(1, item.getDisponibles()), item.getCantidad());
        cantidad.setPrefWidth(100);
        cantidad.valueProperty().addListener((propiedad, antes, ahora) -> { item.setCantidad(ahora); Sesion.setGuardarCarrito(); refrescar(); });

        Label precio = new Label(Formato.cop(item.getSubtotal()));
        precio.getStyleClass().add("precio");
        VBox precios = new VBox(2, precio);
        precios.setAlignment(Pos.CENTER_RIGHT);
        precios.setMinWidth(110);
        if (item.getProducto().getPrecioAnterior() > 0) {
            Text antes = new Text(Formato.cop(item.getSubtotalAnterior()));
            antes.getStyleClass().add("precio-antes");
            precios.getChildren().add(antes);
        }
        HBox fila = new HBox(Ui.foto(item.getProducto().getPortada(), 110, 110), informacion, Ui.espacio(), cantidad, precios);
        fila.getStyleClass().add("item-carrito");
        return fila;
    }

    @FXML private void continuar() { Navegador.tienda("Pago"); }
    @FXML private void volver() { Navegador.tienda("Catalogo"); }
}
