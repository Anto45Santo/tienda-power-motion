package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/** 11 · Mis compras. */
public class frmCompras {
    @FXML private Hyperlink lnkVolver;
    @FXML private HBox boxFiltros;
    @FXML private VBox lista, boxVacio;
    private final ToggleGroup grupo = new ToggleGroup();
    private String filtro = "Todas";

    @FXML
    private void initialize() {
        // Los filtros están en Compras.fxml (userData = nombre del filtro); aquí se les pone la cantidad
        for (javafx.scene.Node nodo : boxFiltros.getChildren()) {
            ToggleButton chip = (ToggleButton) nodo;
            String nombreFiltro = (String) chip.getUserData();
            chip.setText(nombreFiltro + "  " + getContar(nombreFiltro));
            chip.setToggleGroup(grupo);
            chip.setOnAction(evento -> { if (!chip.isSelected()) chip.setSelected(true); filtro = nombreFiltro; refrescar(); });
        }
        refrescar();
    }

    private boolean getPasaFiltro(Pedido pedido, String nombreFiltro) {
        if (pedido.getCliente() != Sesion.getUsuario()) return false;
        switch (nombreFiltro) {
            case "En proceso": return pedido.getEnProceso();
            case "Entregadas": return pedido.getEstado() == EstadoPedido.ENTREGADO;
            case "Rechazadas": return pedido.getEstado() == EstadoPedido.RECHAZADO;
            default: return true;
        }
    }

    private int getContar(String nombreFiltro) {
        int contador = 0;
        if (Datos.pedidos.getEsVacia()) return 0;
        Pedido actual = Datos.pedidos.inicio;
        do {
            if (getPasaFiltro(actual, nombreFiltro)) contador++;
            actual = actual.sig;
        } while (actual != Datos.pedidos.inicio);
        return contador;
    }

    private void refrescar() {
        lista.getChildren().clear();
        // Recorre la cola circular hacia atrás desde el último (inicio.ant): las compras más recientes primero
        if (!Datos.pedidos.getEsVacia()) {
            Pedido ultimo = Datos.pedidos.inicio.ant, actual = ultimo;
            do {
                if (getPasaFiltro(actual, filtro)) lista.getChildren().add(fila(actual));
                actual = actual.ant;
            } while (actual != ultimo);
        }
        boxVacio.setVisible(lista.getChildren().isEmpty());
    }

    private HBox fila(Pedido pedido) {
        HBox fotos = new HBox(6);
        fotos.setMinWidth(180);
        ItemCarrito item = pedido.getItems().cab;
        for (int mostradas = 0; item != null && mostradas < 3; mostradas++) {
            fotos.getChildren().add(Ui.foto(item.getProducto().getPortada(), 52, 60));
            item = item.sig;
        }
        Label numero = new Label("Compra #PM-" + pedido.getNumero());
        numero.getStyleClass().add("subtitulo");
        Label detalle = new Label(pedido.getFecha() + " · " + pedido.getUnidades() + (pedido.getUnidades() == 1 ? " producto" : " productos"));
        detalle.getStyleClass().add("gris");
        VBox informacion = new VBox(4, numero, detalle);
        informacion.setMinWidth(250);
        VBox estado = new VBox(4, Ui.estado(pedido.getEstado()));
        estado.setMinWidth(170);
        if (pedido.getEstado() == EstadoPedido.RECHAZADO && !pedido.getMotivoRechazo().isEmpty()) {
            Label motivo = new Label(pedido.getMotivoRechazo());
            motivo.getStyleClass().add("error");
            estado.getChildren().add(motivo);
        }
        Label total = new Label(Formato.cop(pedido.getTotal()));
        total.getStyleClass().add("precio");
        total.setMinWidth(110);
        HBox acciones = new HBox(8);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        if (pedido.getEstado() == EstadoPedido.RECHAZADO) {
            Button subir = new Button("Subir otro comprobante", Iconos.crear("upload", 16, "#FFFFFF"));
            subir.getStyleClass().add("btn-primario");
            subir.setOnAction(evento -> subirComprobante(pedido));
            acciones.getChildren().add(subir);
        } else {
            if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
                Button calificar = new Button("Calificar", Iconos.crear("star", 16, "#2583FF"));
                calificar.getStyleClass().add("btn-suave");
                calificar.setOnAction(evento -> {
                    frmProducto pantalla = Navegador.tienda("Producto");
                    pantalla.mostrar(pedido.getItems().cab.getProducto());
                });
                acciones.getChildren().add(calificar);
            }
            Button verDetalle = new Button("Ver detalle");
            verDetalle.getStyleClass().add("btn-borde");
            verDetalle.setOnAction(evento -> verDetalle(pedido));
            acciones.getChildren().add(verDetalle);
        }
        HBox fila = new HBox(fotos, informacion, estado, total, Ui.espacio(), acciones);
        fila.getStyleClass().add("fila-compra");
        return fila;
    }

    private void subirComprobante(Pedido pedido) {
        FileChooser selector = new FileChooser();
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        java.io.File elegido = selector.showOpenDialog(Navegador.getStage());
        if (elegido == null) return;
        try {   // el comprobante rechazado se reemplaza por el nuevo en la carpeta comprobantes/
            String nuevo = ArchivoTexto.setCopiarImagen(elegido, ArchivoTexto.CARPETA_COMPROBANTES, "PM" + pedido.getNumero());
            ArchivoTexto.setBorrarImagen(ArchivoTexto.CARPETA_COMPROBANTES, pedido.getComprobante());
            pedido.setComprobante(nuevo);
        } catch (java.io.IOException problema) {
            Navegador.aviso("No se pudo copiar el comprobante: " + problema.getMessage());
            return;
        }
        pedido.setEstado(EstadoPedido.POR_APROBAR);
        pedido.setMotivoRechazo("");
        Datos.setGuardarPedidos();
        refrescar();
        Navegador.aviso("Comprobante enviado. El pedido vuelve a revisión.");
    }

    private void verDetalle(Pedido pedido) {
        VBox contenido = new VBox(10);
        HBox pasos = new HBox(6);
        EstadoPedido[] orden = {EstadoPedido.POR_APROBAR, EstadoPedido.APROBADO, EstadoPedido.ENVIADO, EstadoPedido.ENTREGADO};
        for (EstadoPedido paso : orden) {
            boolean cumplido = paso.ordinal() <= pedido.getEstado().ordinal();
            Label etiqueta = new Label(paso.getTexto());
            etiqueta.getStyleClass().addAll("estado", cumplido ? "estado-aprobado" : "estado-neutro");
            pasos.getChildren().add(etiqueta);
        }
        contenido.getChildren().add(pasos);
        ItemCarrito item = pedido.getItems().cab;
        while (item != null) {
            Label nombre = new Label(item.getProducto().getNombre() + "  ·  " + item.getTallaTexto() + " · " + item.getColor() + " · x" + item.getCantidad());
            Label precio = new Label(Formato.cop(item.getSubtotal()));
            precio.getStyleClass().add("negrita");
            HBox fila = new HBox(12, Ui.foto(item.getProducto().getPortada(), 44, 44), nombre, Ui.espacio(), precio);
            fila.setAlignment(Pos.CENTER_LEFT);
            contenido.getChildren().add(fila);
            item = item.sig;
        }
        Label total = new Label("Total: " + Formato.cop(pedido.getTotal()));
        total.getStyleClass().add("subtitulo");
        contenido.getChildren().add(total);
        contenido.setPrefWidth(560);
        Dialogos.nuevo("Compra #PM-" + pedido.getNumero(), contenido, ButtonType.CLOSE).showAndWait();
    }

    @FXML private void volver() { Navegador.tienda("Catalogo"); }
}
