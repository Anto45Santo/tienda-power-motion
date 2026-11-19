package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 15 · Admin – Nuevo / Editar producto. */
public class frmAdminProductoForm {
    @FXML private TextField txtNombre, txtPrecio, txtAnterior, txtColor;
    @FXML private ComboBox<String> cmbCategoria, cmbMarca, cmbSub, cmbGenero;
    @FXML private FlowPane boxColores, flowTallas;
    @FXML private HBox boxNuevoColor, boxImagenes, segmento, notaEdicion;
    @FXML private Button btnColor, btnGuardar;
    @FXML private TextArea txtDescripcion;
    @FXML private StackPane iconoCandado;
    @FXML private Label lblNotaTallaje, lblMatriz, lblError;
    @FXML private VBox boxTallas;
    @FXML private GridPane matriz;
    @FXML private ToggleButton tipoCalzado, tipoTextil, tipoUnica;

    private Producto editando;                                    // null = producto nuevo
    private TipoTalla tipo = TipoTalla.CALZADO;
    private final ListaColores colores = new ListaColores();        // lista doble (los colores del formulario)
    private final ListaImagenes imagenes = new ListaImagenes();     // lista doble circular (la cabeza es la portada)
    private final Map<String, TextField> casillas = new HashMap<>(); // "talla|color" → casilla de stock
    private final Map<String, String> valoresEscritos = new HashMap<>(); // conserva lo escrito al rearmar la tabla
    private final ToggleGroup grupoTipo = new ToggleGroup();

    @FXML
    private void initialize() {
        // Las opciones de las listas, los 3 botones de tallaje y los iconos están en AdminProductoForm.fxml
        ToggleButton[] botones = {tipoCalzado, tipoTextil, tipoUnica};
        TipoTalla[] tipos = TipoTalla.values();
        for (int posicion = 0; posicion < botones.length; posicion++) {
            ToggleButton boton = botones[posicion];
            TipoTalla opcion = tipos[posicion];
            boton.setUserData(opcion);
            boton.setToggleGroup(grupoTipo);
            boton.setOnAction(evento -> {
                if (!boton.isSelected()) { boton.setSelected(true); return; }
                guardarValores();
                tipo = opcion;
                armarTallas(true);
            });
        }
        // Al elegir la categoría de un producto nuevo se sugiere el tallaje (se puede cambiar)
        cmbCategoria.setOnAction(evento -> {
            if (editando != null || cmbCategoria.getValue() == null) return;
            switch (cmbCategoria.getValue()) {
                case "Ropa": seleccionarTipo(TipoTalla.TEXTIL); break;
                case "Accesorios": seleccionarTipo(TipoTalla.UNICA); break;
                default: seleccionarTipo(TipoTalla.CALZADO);
            }
        });
    }

    // ------------------------------------------------------------------ abrir
    public void nuevo() {
        editando = null;
        Navegador.getAdmin().titulo("Nuevo producto", "Completa los datos; al guardar se publica en el catálogo.");
        btnGuardar.setText("Guardar producto");
        cmbGenero.setValue("Hombre");
        seleccionarTipo(TipoTalla.CALZADO);
        armarColores();
        armarImagenes();
    }

    public void editar(Producto producto) {
        editando = producto;
        Navegador.getAdmin().titulo("Editar producto", "Ref. PM-" + String.format("%04d", producto.getId())
                + " · " + producto.getVendidos() + " unidades vendidas");
        btnGuardar.setText("Guardar cambios");
        txtNombre.setText(producto.getNombre());
        cmbCategoria.setValue(producto.getCategoria());
        cmbMarca.setValue(producto.getMarca());
        cmbSub.setValue(producto.getSubcategoria());
        cmbGenero.setValue(producto.getGenero());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtAnterior.setText(producto.getPrecioAnterior() > 0 ? String.valueOf(producto.getPrecioAnterior()) : "");
        txtDescripcion.setText(producto.getDescripcion());
        // Se copian los colores (lista doble) y las imágenes (lista circular) al formulario
        NodoColor color = producto.getColores().cab;
        while (color != null) {
            colores.setAddF(color.nombre);
            color = color.sig;
        }
        if (!producto.getImagenes().getEsVacia()) {
            NodoImagen imagen = producto.getImagenes().cab;
            do {
                imagenes.setAddF(imagen.archivo);
                imagen = imagen.sig;
            } while (imagen != producto.getImagenes().cab);
        }
        // Las cantidades actuales llenan la tabla de stock
        Variante variante = producto.getVariantes().cab;
        while (variante != null) {
            valoresEscritos.put(variante.getTalla() + "|" + variante.getColor(), String.valueOf(variante.getCantidad()));
            variante = variante.sig;
        }
        notaEdicion.setVisible(true);
        notaEdicion.setManaged(true);
        tipo = producto.getTipoTalla();
        seleccionarTipo(tipo);
        boolean bloqueado = producto.getVendidos() > 0;
        for (Toggle opcion : grupoTipo.getToggles()) ((ToggleButton) opcion).setDisable(bloqueado && opcion.getUserData() != tipo);
        if (bloqueado) {
            iconoCandado.setVisible(true);
            lblNotaTallaje.setText("Solo el tipo de tallaje queda fijo porque ya hay ventas.");
            lblNotaTallaje.getStyleClass().setAll("label", "nota-aviso");
        }
        armarColores();
        armarImagenes();
    }

    private void seleccionarTipo(TipoTalla nuevoTipo) {
        guardarValores();
        tipo = nuevoTipo;
        for (Toggle opcion : grupoTipo.getToggles()) if (opcion.getUserData() == nuevoTipo) opcion.setSelected(true);
        armarTallas(editando == null);
    }

    // ------------------------------------------------------------------ tallas y tabla de stock
    private void armarTallas(boolean marcarComunes) {
        lblNotaTallaje.getStyleClass().setAll("label", "gris");
        switch (tipo) {
            case CALZADO: lblNotaTallaje.setText("Para tenis, guayos y sandalias."); break;
            case TEXTIL: lblNotaTallaje.setText("Para camisetas, pantalones, pantalonetas, chaquetas, licras y medias."); break;
            default: lblNotaTallaje.setText("Para maletines, termos, balones, cuerdas y demás accesorios sin talla.");
        }
        flowTallas.getChildren().clear();
        boolean tallaUnica = tipo == TipoTalla.UNICA;
        boxTallas.setVisible(!tallaUnica);
        boxTallas.setManaged(!tallaUnica);
        if (!tallaUnica) {
            boolean mismoTipo = editando != null && editando.getTipoTalla() == tipo;
            for (String talla : tipo.getTallas()) {
                ToggleButton boton = new ToggleButton(talla);
                boton.getStyleClass().add("talla-admin");
                boolean extrema = talla.equals("XS") || talla.equals("3XL") || talla.equals("7") || talla.equals("12");
                boton.setSelected(mismoTipo ? editando.getTallaActiva(talla) : marcarComunes && !extrema);
                boton.setOnAction(evento -> { guardarValores(); armarMatriz(); });
                flowTallas.getChildren().add(boton);
            }
        }
        armarMatriz();
    }

    private List<String> getTallasMarcadas() {
        List<String> marcadas = new ArrayList<>();
        if (tipo == TipoTalla.UNICA) {
            marcadas.add("UNICA");
            return marcadas;
        }
        for (Node nodo : flowTallas.getChildren()) {
            ToggleButton boton = (ToggleButton) nodo;
            if (boton.isSelected()) marcadas.add(boton.getText());
        }
        return marcadas;
    }

    private void guardarValores() {
        casillas.forEach((clave, casilla) -> valoresEscritos.put(clave, casilla.getText()));
    }

    /** Filas = colores (recorre la lista doble), columnas = tallas marcadas o "Cantidad" si es talla única. */
    private void armarMatriz() {
        matriz.getChildren().clear();
        matriz.getColumnConstraints().clear();
        casillas.clear();
        List<String> tallas = getTallasMarcadas();
        boolean tallaUnica = tipo == TipoTalla.UNICA;
        lblMatriz.setText(tallaUnica ? "Stock por color" : "Stock por talla y color");
        matriz.getColumnConstraints().add(new ColumnConstraints(80));
        for (int columna = 0; columna < tallas.size(); columna++) {
            ColumnConstraints medida = new ColumnConstraints(tallaUnica ? 100 : 34, tallaUnica ? 100 : 46, 60);
            medida.setHalignment(HPos.CENTER);
            matriz.getColumnConstraints().add(medida);
            Label encabezado = new Label(tallaUnica ? "Cantidad" : tallas.get(columna));
            encabezado.getStyleClass().addAll("gris", "negrita");
            matriz.add(encabezado, columna + 1, 0);
        }
        int fila = 1;
        NodoColor color = colores.cab;
        while (color != null) {
            Label nombre = new Label(color.nombre);
            nombre.getStyleClass().add("negrita");
            matriz.add(nombre, 0, fila);
            for (int columna = 0; columna < tallas.size(); columna++) {
                String clave = tallas.get(columna) + "|" + color.nombre;
                TextField casilla = new TextField(valoresEscritos.getOrDefault(clave, "0"));
                casilla.textProperty().addListener((propiedad, antes, ahora) -> {
                    if (!ahora.matches("\\d*")) casilla.setText(ahora.replaceAll("[^0-9]", ""));
                    marcarCero(casilla);
                });
                marcarCero(casilla);
                casillas.put(clave, casilla);
                matriz.add(casilla, columna + 1, fila);
            }
            fila++;
            color = color.sig;
        }
        if (colores.getEsVacia()) {
            Label aviso = new Label("Agrega al menos un color para llenar el stock.");
            aviso.getStyleClass().add("gris");
            matriz.add(aviso, 0, 1, 6, 1);
        }
    }

    private static void marcarCero(TextField casilla) {
        casilla.getStyleClass().remove("cero");
        if (casilla.getText().isEmpty() || casilla.getText().equals("0")) casilla.getStyleClass().add("cero");
    }

    // ------------------------------------------------------------------ colores
    @FXML
    private void agregarColor() {
        String escrito = txtColor.getText().trim();
        if (escrito.isEmpty()) return;
        String nombre = escrito.substring(0, 1).toUpperCase() + escrito.substring(1).toLowerCase();
        guardarValores();
        if (!colores.setAddF(nombre)) {              // la lista no deja repetir colores
            lblError.setText("Ese color ya está agregado.");
            return;
        }
        txtColor.clear();
        lblError.setText("");
        armarColores();
        armarMatriz();
    }

    private void armarColores() {
        boxColores.getChildren().removeIf(nodo -> nodo != boxNuevoColor);
        int posicion = 0;
        NodoColor actual = colores.cab;
        while (actual != null) {
            String nombre = actual.nombre;
            Button etiqueta = new Button(nombre, Iconos.crear("close", 12, "#172B4D"));
            etiqueta.getStyleClass().add("color-chip");
            etiqueta.setTooltip(new Tooltip("Quitar color"));
            etiqueta.setOnAction(evento -> {
                guardarValores();
                colores.setDelxNombre(nombre);
                armarColores();
                armarMatriz();
            });
            boxColores.getChildren().add(posicion++, etiqueta);
            actual = actual.sig;
        }
    }

    // ------------------------------------------------------------------ imágenes
    private void armarImagenes() {
        boxImagenes.getChildren().clear();
        if (!imagenes.getEsVacia()) {
            NodoImagen actual = imagenes.cab;
            do {
                String archivo = actual.archivo;
                StackPane miniatura = new StackPane(Ui.imagen(Imagenes.producto(archivo, 140), 56, 56));
                miniatura.getStyleClass().add("imagen-form");
                miniatura.setPrefSize(64, 64);
                Button quitar = new Button(null, Iconos.crear("close", 10, "#E11D48"));
                quitar.setStyle("-fx-padding: 2; -fx-background-color: white; -fx-border-color: #E2E8F0;");
                StackPane.setAlignment(quitar, Pos.TOP_RIGHT);
                quitar.setOnAction(evento -> { imagenes.setDelxArchivo(archivo); armarImagenes(); });
                miniatura.getChildren().add(quitar);
                if (actual == imagenes.cab) {
                    Label portada = new Label("Portada");
                    portada.getStyleClass().add("badge-oscuro");
                    StackPane.setAlignment(portada, Pos.BOTTOM_CENTER);
                    miniatura.getChildren().add(portada);
                } else {
                    miniatura.setOnMouseClicked(evento -> { imagenes.setPortada(archivo); armarImagenes(); });
                    Tooltip.install(miniatura, new Tooltip("Clic para usarla como portada"));
                }
                boxImagenes.getChildren().add(miniatura);
                actual = actual.sig;
            } while (actual != imagenes.cab);
        }
        if (imagenes.getLongLista() < 8) {
            StackPane agregar = new StackPane(Iconos.crear("plus", 22, "#2583FF"));
            agregar.getStyleClass().add("imagen-nueva");
            agregar.setPrefSize(64, 64);
            agregar.setOnMouseClicked(evento -> subirImagen());
            boxImagenes.getChildren().add(agregar);
        }
    }

    /** Copia la imagen elegida a la carpeta imagenes/productos y guarda solo su nombre. */
    private void subirImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Imagen del producto");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File elegida = selector.showOpenDialog(Navegador.getStage());
        if (elegida == null) return;
        try {
            Path carpeta = Paths.get("imagenes", "productos");
            Files.createDirectories(carpeta);
            String extension = elegida.getName().substring(elegida.getName().lastIndexOf('.')).toLowerCase();
            String nombre = "p_" + System.currentTimeMillis() + extension;
            Files.copy(elegida.toPath(), carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            imagenes.setAddF(nombre);
            armarImagenes();
        } catch (IOException error) {
            lblError.setText("No se pudo copiar la imagen: " + error.getMessage());
        }
    }

    // ------------------------------------------------------------------ guardar
    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        long precio = Formato.leerCop(txtPrecio.getText()), precioAntes = Formato.leerCop(txtAnterior.getText());
        if (nombre.isEmpty() || cmbCategoria.getValue() == null || cmbMarca.getValue() == null || cmbMarca.getValue().isBlank()) {
            lblError.setText("Completa nombre, categoría y marca."); return;
        }
        if (precio <= 0) { lblError.setText("Escribe un precio válido."); return; }
        if (imagenes.getEsVacia()) { lblError.setText("Agrega al menos una imagen."); return; }
        if (colores.getEsVacia()) { lblError.setText("Agrega al menos un color."); return; }
        List<String> tallas = getTallasMarcadas();
        if (tallas.isEmpty()) { lblError.setText("Marca al menos una talla."); return; }
        int totalUnidades = 0;
        for (TextField casilla : casillas.values()) totalUnidades += casilla.getText().isEmpty() ? 0 : Integer.parseInt(casilla.getText());
        if (editando == null && totalUnidades == 0) { lblError.setText("Escribe el stock de al menos una talla."); return; }

        Producto producto = editando;
        if (producto == null) {
            producto = new Producto(Datos.catalogo.getSiguienteId(), nombre, "", "", "", "", tipo, precio, 0, "");
            producto.setNuevo(true);
            Datos.catalogo.setAddF(producto);                  // entra al final de la lista doble circular
        }
        producto.setNombre(ArchivoTexto.getLimpio(nombre));
        producto.setCategoria(cmbCategoria.getValue());
        producto.setMarca(ArchivoTexto.getLimpio(cmbMarca.getValue()));
        producto.setSubcategoria(cmbSub.getValue() == null ? "" : ArchivoTexto.getLimpio(cmbSub.getValue()));
        producto.setGenero(cmbGenero.getValue() == null ? "Unisex" : cmbGenero.getValue());
        producto.setPrecio(precio);
        producto.setPrecioAnterior(Math.max(0, precioAntes));
        producto.setDescripcion(ArchivoTexto.getLimpio(txtDescripcion.getText()));
        producto.setTipoTalla(tipo);
        // Se reemplazan colores, imágenes y stock por lo que quedó en el formulario
        producto.getColores().setVaciar();
        producto.getImagenes().setVaciar();
        producto.getVariantes().setVaciar();
        NodoColor color = colores.cab;
        while (color != null) {
            producto.getColores().setAddF(color.nombre);
            for (String talla : tallas) {
                TextField casilla = casillas.get(talla + "|" + color.nombre);
                int cantidad = casilla == null || casilla.getText().isEmpty() ? 0 : Integer.parseInt(casilla.getText());
                producto.getVariantes().setAddF(new Variante(talla, color.nombre, cantidad));
            }
            color = color.sig;
        }
        if (!imagenes.getEsVacia()) {
            NodoImagen imagen = imagenes.cab;
            do {
                producto.getImagenes().setAddF(imagen.archivo);
                imagen = imagen.sig;
            } while (imagen != imagenes.cab);
        }
        if (!Datos.setGuardarProductos()) { lblError.setText(Datos.error); return; }   // productos.txt y stock.txt
        String mensaje = (editando == null ? "Producto creado: " : "Cambios guardados: ") + producto.getNombre();
        Navegador.admin("AdminProductos");
        Navegador.aviso(mensaje);
    }

    @FXML private void cancelar() { Navegador.admin("AdminProductos"); }
}
