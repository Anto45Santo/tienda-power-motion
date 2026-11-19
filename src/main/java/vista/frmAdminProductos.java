package vista;

import controlador.*;
import modelo.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/** 14 · Admin – Productos. Recorre el catálogo (lista doble circular) y muestra la tabla. */
public class frmAdminProductos {
    @FXML private HBox boxPestanas;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tabla;
    @FXML private TableColumn<Producto, Producto> colProducto, colCategoria, colTallaje, colPrecio, colStock, colAcciones;
    private final ToggleGroup grupo = new ToggleGroup();
    private String pestana = "Todos";

    @FXML
    private void initialize() {
        // Las pestañas están en AdminProductos.fxml: el texto de cada una es el filtro
        for (javafx.scene.Node nodo : boxPestanas.getChildren()) {
            ToggleButton chip = (ToggleButton) nodo;
            chip.setToggleGroup(grupo);
            chip.setOnAction(evento -> { if (!chip.isSelected()) chip.setSelected(true); pestana = chip.getText(); refrescar(); });
        }
        txtBuscar.textProperty().addListener((propiedad, antes, ahora) -> refrescar());

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);   // las columnas llenan el ancho de la tabla
        Celdas.llenar(colProducto, producto -> Celdas.fila(10, Ui.foto(producto.getPortada(), 40, 40),
                Celdas.dosLineas(producto.getNombre(), producto.getMarca())));
        Celdas.llenar(colCategoria, producto -> Celdas.texto(producto.getCategoria() + " · " + producto.getSubcategoria(), null));
        Celdas.llenar(colTallaje, producto -> Celdas.texto(producto.getTipoTalla().getNombre(), "etiqueta"));
        Celdas.llenar(colPrecio, producto -> Celdas.texto(Formato.cop(producto.getPrecio()), "negrita"));
        Celdas.llenar(colStock, producto -> {
            int unidades = producto.getStockTotal();
            Label estado = !producto.isActivo() ? Ui.pill("Inactivo", "estado-neutro")
                    : unidades == 0 ? Ui.pill("Agotado", "estado-rechazado")
                    : getBajoStock(producto) ? Ui.pill("Bajo stock", "estado-pendiente")
                    : Ui.pill("En stock", "estado-aprobado");
            return Celdas.fila(8, estado, Celdas.texto(unidades + " u.", "negrita"));
        });
        Celdas.llenar(colAcciones, producto -> {
            Button editar = new Button(null, Iconos.crear("edit", 16, "#2583FF"));
            editar.getStyleClass().addAll("btn-accion", "btn-ver");
            editar.setTooltip(new Tooltip("Editar"));
            editar.setOnAction(evento -> editar(producto));
            boolean activo = producto.isActivo();
            Button cambiarEstado = new Button(null, Iconos.crear(activo ? "trash" : "refresh", 16, activo ? "#E11D48" : "#16A34A"));
            cambiarEstado.getStyleClass().addAll("btn-accion", activo ? "btn-rechazar" : "btn-aprobar");
            cambiarEstado.setTooltip(new Tooltip(activo ? "Desactivar (deja de verse en la tienda)" : "Activar"));
            cambiarEstado.setOnAction(evento -> alternarActivo(producto));
            return Celdas.fila(6, editar, cambiarEstado);
        });
        tabla.setRowFactory(vista -> {
            TableRow<Producto> fila = new TableRow<>();
            fila.setOnMouseClicked(evento -> { if (evento.getClickCount() == 2 && !fila.isEmpty()) editar(fila.getItem()); });
            return fila;
        });
        refrescar();
    }

    /** Bajo stock: quedan menos de 10 unidades en total (pero no está agotado). */
    public static boolean getBajoStock(Producto producto) {
        int unidades = producto.getStockTotal();
        return unidades > 0 && unidades < 10;
    }

    private boolean getPasaPestana(Producto producto) {
        switch (pestana) {
            case "Calzado": case "Ropa": case "Accesorios": return producto.isActivo() && producto.getCategoria().equals(pestana);
            case "Bajo stock": return producto.isActivo() && producto.getStockTotal() < 10;
            case "Inactivos": return !producto.isActivo();
            default: return producto.isActivo();
        }
    }

    private void refrescar() {
        String buscado = txtBuscar.getText().toLowerCase().trim();
        ObservableList<Producto> filas = FXCollections.observableArrayList();
        if (!Datos.catalogo.getEsVacia()) {
            Producto actual = Datos.catalogo.cab;
            do {
                String datos = (actual.getNombre() + " " + actual.getMarca()).toLowerCase();
                if (getPasaPestana(actual) && (buscado.isEmpty() || datos.contains(buscado))) filas.add(actual);
                actual = actual.sig;
            } while (actual != Datos.catalogo.cab);
        }
        tabla.setItems(filas);
    }

    private void editar(Producto producto) {
        frmAdminProductoForm formulario = Navegador.admin("AdminProductoForm");
        formulario.editar(producto);
    }

    /** No se borra: los pedidos anteriores siguen necesitando el producto. */
    private void alternarActivo(Producto producto) {
        if (producto.isActivo() && !Dialogos.confirmar("Desactivar producto",
                "“" + producto.getNombre() + "” dejará de verse en la tienda. ¿Continuar?")) return;
        producto.setActivo(!producto.isActivo());
        Datos.setGuardarProductos();                       // productos.txt
        refrescar();
        Navegador.aviso(producto.isActivo() ? "Producto activado" : "Producto desactivado");
    }

    @FXML
    private void nuevo() {
        frmAdminProductoForm formulario = Navegador.admin("AdminProductoForm");
        formulario.nuevo();
    }
}
