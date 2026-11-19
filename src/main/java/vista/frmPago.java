package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class frmPago {
    @FXML private Hyperlink lnkVolver;
    @FXML private Label lblNumero, lblTotal, lblArchivo, lblError;
    @FXML private Button btnCopiar, btnEnviar;
    @FXML private VBox listaItems;
    @FXML private TextField txtNombre, txtTelefono, txtDireccion, txtCiudad;
    @FXML private ComboBox<String> cmbDepartamento;
    @FXML private HBox zonaArchivo;
    private File comprobante;

    @FXML
    private void initialize() {
        // Los iconos y la lista de departamentos están en Pago.fxml
        Usuario conectado = Sesion.getUsuario();
        txtNombre.setText(conectado.getNombreCompleto());
        txtTelefono.setText(conectado.getTelefono());
        txtDireccion.setText(conectado.getDireccion());

        listaItems.getChildren().clear();               // quita las filas de ejemplo del FXML
        ItemCarrito actual = Sesion.getCarrito().cab;
        while (actual != null) {                         // recorrido de la lista doble del carrito
            Label nombre = new Label(actual.getProducto().getNombre());
            nombre.getStyleClass().add("negrita");
            nombre.setMaxWidth(230);
            Label detalle = new Label(actual.getTallaTexto() + " · x" + actual.getCantidad());
            detalle.getStyleClass().add("gris");
            Label precio = new Label(Formato.cop(actual.getSubtotal()));
            precio.getStyleClass().add("negrita");
            HBox fila = new HBox(12, Ui.foto(actual.getProducto().getPortada(), 48, 48), new VBox(2, nombre, detalle), Ui.espacio(), precio);
            fila.setAlignment(Pos.CENTER_LEFT);
            listaItems.getChildren().add(fila);
            actual = actual.sig;
        }
        long total = Sesion.getCarrito().getTotal();
        total += frmCarrito.getEnvio(total);
        lblTotal.setText(Formato.cop(total));
    }

    @FXML
    private void copiar() {
        ClipboardContent contenido = new ClipboardContent();
        contenido.putString(lblNumero.getText().replace(" ", ""));
        Clipboard.getSystemClipboard().setContent(contenido);
        Navegador.aviso("Número copiado");
    }

    @FXML
    private void examinar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Comprobante de pago");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File elegido = selector.showOpenDialog(Navegador.getStage());
        if (elegido == null) return;
        if (elegido.length() > 5 * 1024 * 1024) { error("La imagen supera 5 MB."); return; }
        comprobante = elegido;
        lblArchivo.setText(elegido.getName());
    }

    @FXML
    private void enviar() {
        if (txtNombre.getText().isBlank() || txtTelefono.getText().isBlank() || txtDireccion.getText().isBlank()
                || txtCiudad.getText().isBlank() || cmbDepartamento.getValue() == null) { error("Completa los datos de envío."); return; }
        if (comprobante == null) { error("Adjunta la imagen del comprobante de pago."); return; }

        ItemCarrito revisar = Sesion.getCarrito().cab;   // el stock pudo cambiar mientras el cliente pagaba
        while (revisar != null) {
            if (revisar.getProducto().getStockDe(revisar.getTalla(), revisar.getColor()) < revisar.getCantidad()) {
                error("Ya no hay suficientes unidades de " + revisar.getProducto().getNombre() + ". Revisa tu carrito.");
                return;
            }
            revisar = revisar.sig;
        }

        // El pedido entra al final de la cola; los productos se copian del carrito (nodos nuevos)
        Pedido pedido = new Pedido(Datos.pedidos.getSiguienteNumero(), Sesion.getUsuario(), Fechas.getAhora(), EstadoPedido.POR_APROBAR);
        try {   // la imagen se copia a comprobantes/ y en pedidos.txt solo queda su nombre
            pedido.setComprobante(ArchivoTexto.setCopiarImagen(comprobante, ArchivoTexto.CARPETA_COMPROBANTES, "PM" + pedido.getNumero()));
        } catch (java.io.IOException problema) {
            error("No se pudo copiar el comprobante: " + problema.getMessage());
            return;
        }
        pedido.setEnvio(ArchivoTexto.getLimpio(txtNombre.getText()), ArchivoTexto.getLimpio(txtTelefono.getText()),
                ArchivoTexto.getLimpio(txtDireccion.getText()), ArchivoTexto.getLimpio(txtCiudad.getText()), cmbDepartamento.getValue());
        ItemCarrito actual = Sesion.getCarrito().cab;
        while (actual != null) {
            pedido.getItems().setAddF(actual.getCopia());
            actual = actual.sig;
        }
        Datos.pedidos.setEncolar(pedido);
        if (!Datos.setGuardarPedidos()) { error(Datos.error); return; }
        Sesion.getCarrito().setVaciar();
        Sesion.setGuardarCarrito();
        Navegador.actualizarBarra();
        Navegador.tienda("Compras");
        int numero = pedido.getNumero();
        Navegador.aviso("Pedido #PM-" + numero + " enviado. Te avisaremos cuando se apruebe el pago.");
    }

    private void error(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    @FXML private void volver() { Navegador.tienda("Carrito"); }
}

