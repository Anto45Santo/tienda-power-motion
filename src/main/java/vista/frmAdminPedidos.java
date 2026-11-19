package vista;

import controlador.*;
import modelo.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 13 · Admin – Pedidos. Los pedidos están en una cola doble circular:
 * la tabla los muestra desde el inicio (el más antiguo primero), en el orden en que se deben atender.
 */
public class frmAdminPedidos {
    // Indicadores, pestañas y columnas están en AdminPedidos.fxml
    @FXML private Label lblVentas, lblHoy, lblPorAprobar, lblBajoStock;
    @FXML private ToggleButton tabPorAprobar, tabAprobado, tabEnviado, tabEntregado, tabRechazado;
    @FXML private TableView<Pedido> tabla;
    @FXML private TableColumn<Pedido, Pedido> colPedido, colCliente, colProductos, colComprobante, colFecha, colTotal, colEstado, colAcciones;
    private final ToggleGroup grupo = new ToggleGroup();
    private ToggleButton[] pestanas;                    // en el mismo orden que EstadoPedido.values()
    private EstadoPedido pestana = EstadoPedido.POR_APROBAR;

    @FXML
    private void initialize() {
        pestanas = new ToggleButton[]{tabPorAprobar, tabAprobado, tabEnviado, tabEntregado, tabRechazado};
        EstadoPedido[] estados = EstadoPedido.values();
        for (int posicion = 0; posicion < pestanas.length; posicion++) {
            EstadoPedido estado = estados[posicion];
            pestanas[posicion].setToggleGroup(grupo);
            pestanas[posicion].setOnAction(evento -> { pestana = estado; refrescar(); });
        }
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);   // las columnas llenan el ancho de la tabla
        Celdas.llenar(colPedido, pedido -> Celdas.dosLineas("#PM-" + pedido.getNumero(), "Transferencia"));
        Celdas.llenar(colCliente, pedido -> Celdas.fila(8,
                Ui.avatar(pedido.getCliente().getNombre().substring(0, 1), 13, "avatar-oscuro"),
                Celdas.texto(pedido.getCliente().getNombreCorto(), "negrita")));
        Celdas.llenar(colProductos, this::miniaturas);
        Celdas.llenar(colComprobante, pedido -> {
            Hyperlink ver = new Hyperlink("Ver");
            ver.setOnAction(evento -> verComprobante(pedido));
            return ver;
        });
        Celdas.llenar(colFecha, pedido -> Celdas.texto(pedido.getFecha(), null));
        Celdas.llenar(colTotal, pedido -> Celdas.texto(Formato.cop(pedido.getTotal()), "negrita"));
        Celdas.llenar(colEstado, pedido -> Ui.estado(pedido.getEstado()));
        Celdas.llenar(colAcciones, this::acciones);
        refrescar();
    }

    private HBox miniaturas(Pedido pedido) {
        HBox fotos = new HBox(4);
        ItemCarrito item = pedido.getItems().cab;
        for (int mostradas = 0; item != null && mostradas < 3; mostradas++) {
            fotos.getChildren().add(Ui.foto(item.getProducto().getPortada(), 30, 30));
            item = item.sig;
        }
        return fotos;
    }

    private void refrescar() {
        lblVentas.setText(Formato.cop(Datos.pedidos.getTotalVentas()));
        lblHoy.setText(String.valueOf(Datos.pedidos.getContarDeHoy()));
        lblPorAprobar.setText(String.valueOf(Datos.pedidos.getContarxEstado(EstadoPedido.POR_APROBAR)));
        lblBajoStock.setText(String.valueOf(Datos.catalogo.getContarBajoStock()));

        EstadoPedido[] estados = EstadoPedido.values();
        for (int posicion = 0; posicion < pestanas.length; posicion++) {
            pestanas[posicion].setText(estados[posicion].getTexto() + "  " + Datos.pedidos.getContarxEstado(estados[posicion]));
            pestanas[posicion].setSelected(estados[posicion] == pestana);
        }
        // TableView necesita una ObservableList: se copia solo lo que se muestra; la estructura sigue siendo la cola
        ObservableList<Pedido> filas = FXCollections.observableArrayList();
        if (!Datos.pedidos.getEsVacia()) {
            Pedido actual = Datos.pedidos.inicio;
            do {
                if (actual.getEstado() == pestana) filas.add(actual);
                actual = actual.sig;
            } while (actual != Datos.pedidos.inicio);
        }
        tabla.setItems(filas);
        Navegador.actualizarBarra();
    }

    private HBox acciones(Pedido pedido) {
        HBox botones = new HBox(6);
        botones.setAlignment(Pos.CENTER_LEFT);
        if (pedido.getEstado() == EstadoPedido.POR_APROBAR) {
            botones.getChildren().add(boton("check", "#16A34A", "btn-aprobar", "Aprobar pago", () -> aprobar(pedido)));
            botones.getChildren().add(boton("close", "#E11D48", "btn-rechazar", "Rechazar", () -> rechazar(pedido)));
        }
        botones.getChildren().add(boton("eye", "#2583FF", "btn-ver", "Ver pedido", () -> verDetalle(pedido)));
        return botones;
    }

    private Button boton(String icono, String color, String estilo, String ayuda, Runnable accion) {
        Button boton = new Button(null, Iconos.crear(icono, 16, color));
        boton.getStyleClass().addAll("btn-accion", estilo);
        boton.setTooltip(new Tooltip(ayuda));
        boton.setOnAction(evento -> accion.run());
        return boton;
    }

    /** Al aprobar se descuenta el stock de cada producto del pedido (recorre la lista doble de items). */
    private void aprobar(Pedido pedido) {
        ItemCarrito item = pedido.getItems().cab;
        while (item != null) {                              // primero se revisa que alcance el stock de todo
            if (item.getProducto().getStockDe(item.getTalla(), item.getColor()) < item.getCantidad()) {
                Navegador.aviso("No hay stock suficiente de " + item.getProducto().getNombre() + " (" + item.getTallaTexto()
                        + " · " + item.getColor() + ")");
                return;
            }
            item = item.sig;
        }
        item = pedido.getItems().cab;
        while (item != null) {
            Producto producto = item.getProducto();
            producto.getVariantes().setDescontar(item.getTalla(), item.getColor(), item.getCantidad());
            producto.setVendidos(producto.getVendidos() + item.getCantidad());
            item = item.sig;
        }
        pedido.setEstado(EstadoPedido.APROBADO);
        Datos.setGuardarPedidos();                          // pedidos.txt
        Datos.setGuardarProductos();                        // productos.txt (vendidos) y stock.txt
        refrescar();
        Navegador.aviso("Pedido #PM-" + pedido.getNumero() + " aprobado");
    }

    private void rechazar(Pedido pedido) {
        TextInputDialog dialogo = new TextInputDialog("Comprobante ilegible");
        dialogo.initOwner(Navegador.getStage());
        dialogo.setTitle("Rechazar pedido");
        dialogo.setHeaderText("Motivo del rechazo (lo verá el cliente)");
        dialogo.getDialogPane().getStylesheets().add(Navegador.estilos());
        dialogo.showAndWait().ifPresent(motivo -> {
            pedido.setEstado(EstadoPedido.RECHAZADO);
            pedido.setMotivoRechazo(ArchivoTexto.getLimpio(motivo));
            Datos.setGuardarPedidos();
            refrescar();
            Navegador.aviso("Pedido #PM-" + pedido.getNumero() + " rechazado");
        });
    }

    /** Muestra la imagen guardada en la carpeta comprobantes/ junto con los datos de envío. */
    private void verComprobante(Pedido pedido) {
        VBox contenido = new VBox(10);
        java.io.File archivo = new java.io.File(ArchivoTexto.CARPETA_COMPROBANTES, pedido.getComprobante());
        if (!pedido.getComprobante().isBlank() && archivo.exists()) {
            javafx.scene.image.ImageView imagen = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(archivo.toURI().toString(), 420, 300, true, true));
            imagen.setPreserveRatio(true);
            contenido.getChildren().add(imagen);
        } else {
            Label aviso = new Label("Este pedido no tiene una imagen de comprobante guardada");
            aviso.setPrefSize(420, 200);
            aviso.setAlignment(Pos.CENTER);
            aviso.setStyle("-fx-background-color: #F4F7FB; -fx-text-alignment: center;");
            contenido.getChildren().add(aviso);
        }
        contenido.getChildren().add(Celdas.dosLineas("Enviar a: " + pedido.getNombreEnvio() + " · " + pedido.getTelefonoEnvio(),
                pedido.getDireccionCompleta()));
        Dialogos.nuevo("Comprobante · #PM-" + pedido.getNumero(), contenido, ButtonType.CLOSE).showAndWait();
    }

    /** Detalle del pedido con el botón para avanzar el estado: Aprobado → Enviado → Entregado. */
    private void verDetalle(Pedido pedido) {
        VBox contenido = new VBox(10);
        contenido.setPrefWidth(560);
        contenido.getChildren().add(Celdas.fila(10, Celdas.texto("Cliente: " + pedido.getCliente().getNombreCompleto()
                + " · " + pedido.getCliente().getTelefono(), null), Ui.estado(pedido.getEstado())));
        contenido.getChildren().add(Celdas.texto("Enviar a: " + pedido.getNombreEnvio() + " · " + pedido.getDireccionCompleta(), "gris"));
        if (pedido.getEstado() == EstadoPedido.RECHAZADO)
            contenido.getChildren().add(Celdas.texto("Motivo del rechazo: " + pedido.getMotivoRechazo(), "gris"));
        ItemCarrito item = pedido.getItems().cab;
        while (item != null) {
            contenido.getChildren().add(Celdas.fila(12, Ui.foto(item.getProducto().getPortada(), 44, 44),
                    Celdas.dosLineas(item.getProducto().getNombre(), item.getTallaTexto() + " · " + item.getColor() + " · x" + item.getCantidad()),
                    Ui.espacio(), Celdas.texto(Formato.cop(item.getSubtotal()), "negrita")));
            item = item.sig;
        }
        contenido.getChildren().add(Celdas.texto("Total: " + Formato.cop(pedido.getTotal()), "subtitulo"));
        EstadoPedido siguiente = pedido.getEstado() == EstadoPedido.APROBADO ? EstadoPedido.ENVIADO
                : pedido.getEstado() == EstadoPedido.ENVIADO ? EstadoPedido.ENTREGADO : null;
        String titulo = "Pedido #PM-" + pedido.getNumero();
        if (siguiente == null) {
            Dialogos.nuevo(titulo, contenido, ButtonType.CLOSE).showAndWait();
            return;
        }
        ButtonType avanzar = new ButtonType("Marcar como " + siguiente.getTexto().toLowerCase(), ButtonBar.ButtonData.OK_DONE);
        Dialogos.nuevo(titulo, contenido, avanzar, ButtonType.CLOSE).showAndWait().ifPresent(respuesta -> {
            if (respuesta == avanzar) {
                pedido.setEstado(siguiente);
                Datos.setGuardarPedidos();
                pestana = siguiente;
                refrescar();
                Navegador.aviso(titulo + ": " + siguiente.getTexto());
            }
        });
    }
}
