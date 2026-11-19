package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/** 07 · Detalle de producto. */
public class frmProducto {
    @FXML private Hyperlink lnkVolver, lnkGuia, lnkOpiniones;
    @FXML private ImageView imgGrande;
    @FXML private Label lblDescuento, lblMarca, lblNombre, lblPrecio, lblPorcentaje, lblColor, lblTalla, lblDisponibles,
            lblMensaje, lblDescripcion, lblPromedio, lblTotalOpiniones;
    @FXML private Text txtAntes;
    @FXML private Button btnAnterior, btnSiguiente, btnCarrito, btnOpinar;
    @FXML private ToggleButton btnDeseos;
    @FXML private HBox boxMiniaturas, boxCalificacion, boxColores, boxTallas, boxEstrellasResumen, boxResenas;
    @FXML private Spinner<Integer> spnCantidad;

    private Producto producto;
    private NodoImagen fotoActual;            // nodo actual de la lista doble circular de imágenes
    private final ToggleGroup grupoColor = new ToggleGroup(), grupoTalla = new ToggleGroup();

    @FXML
    private void initialize() {
        // Los iconos y los textos de ejemplo están en Producto.fxml; mostrar() pone los datos reales
        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
    }

    /** Lo llama la tarjeta del catálogo al abrir el producto. */
    public void mostrar(Producto elegido) {
        producto = elegido;
        lblMarca.setText(producto.getMarca().toUpperCase());
        lblNombre.setText(producto.getNombre());
        lblDescripcion.setText(producto.getDescripcion());

        boxCalificacion.getChildren().setAll(Ui.estrellas(producto.getPromedio(), 20));
        if (producto.getTotalResenas() > 0) {
            Label promedio = new Label(String.format("%.1f", producto.getPromedio()));
            promedio.getStyleClass().add("negrita");
            promedio.setStyle("-fx-font-size: 15px;");
            boxCalificacion.getChildren().add(promedio);
        }
        Label extra = new Label((producto.getTotalResenas() == 0 ? "Sin opiniones aún" : "(" + producto.getTotalResenas() + " opiniones)")
                + " · Vendidos: " + producto.getVendidos());
        extra.getStyleClass().add("gris");
        boxCalificacion.getChildren().add(extra);

        lblPrecio.setText(Formato.cop(producto.getPrecio()));
        boolean tieneDescuento = producto.getDescuento() > 0;
        txtAntes.setText(tieneDescuento ? Formato.cop(producto.getPrecioAnterior()) : "");
        lblPorcentaje.setText(tieneDescuento ? producto.getDescuento() + "% OFF" : "");
        lblDescuento.setText("-" + producto.getDescuento() + "%");
        lblDescuento.setVisible(tieneDescuento);

        armarGaleria();
        armarColores();
        actualizarDeseo();
        armarOpiniones();
    }

    // ------------------------------------------------------------------ galería
    private void armarGaleria() {
        fotoActual = producto.getImagenes().cab;
        boolean variasFotos = producto.getImagenes().getLongLista() > 1;
        btnAnterior.setVisible(variasFotos);
        btnSiguiente.setVisible(variasFotos);
        boxMiniaturas.getChildren().clear();
        if (variasFotos) {
            NodoImagen actual = producto.getImagenes().cab;
            do {                                                  // una sola vuelta a la lista circular
                NodoImagen nodo = actual;
                StackPane miniatura = new StackPane(Ui.imagen(Imagenes.producto(nodo.archivo, 160), 82, 52));
                miniatura.getStyleClass().add("miniatura");
                miniatura.setPrefSize(98, 58);
                miniatura.setUserData(nodo);
                miniatura.setOnMouseClicked(evento -> { fotoActual = nodo; mostrarFoto(); });
                boxMiniaturas.getChildren().add(miniatura);
                actual = actual.sig;
            } while (actual != producto.getImagenes().cab);
        }
        mostrarFoto();
    }

    private void mostrarFoto() {
        imgGrande.setImage(fotoActual == null ? null : Imagenes.producto(fotoActual.archivo, 600));
        for (Node miniatura : boxMiniaturas.getChildren()) {
            miniatura.getStyleClass().remove("activa");
            if (miniatura.getUserData() == fotoActual) miniatura.getStyleClass().add("activa");
        }
    }

    @FXML private void fotoSiguiente() { fotoActual = fotoActual.sig; mostrarFoto(); }   // de la última pasa a la primera
    @FXML private void fotoAnterior() { fotoActual = fotoActual.ant; mostrarFoto(); }    // de la primera pasa a la última

    // ------------------------------------------------------------------ colores, tallas y cantidad
    private void armarColores() {
        boxColores.getChildren().clear();
        NodoColor actual = producto.getColores().cab;
        while (actual != null) {
            ToggleButton boton = new ToggleButton(actual.nombre);
            boton.getStyleClass().add("color");
            boton.setToggleGroup(grupoColor);
            boton.setOnAction(evento -> { if (!boton.isSelected()) boton.setSelected(true); armarTallas(); });
            boxColores.getChildren().add(boton);
            actual = actual.sig;
        }
        if (!boxColores.getChildren().isEmpty()) ((ToggleButton) boxColores.getChildren().get(0)).setSelected(true);
        armarTallas();
    }

    private String getColor() {
        Toggle elegido = grupoColor.getSelectedToggle();
        return elegido == null ? null : ((ToggleButton) elegido).getText();
    }

    private String getTalla() {
        if (producto.getTipoTalla() == TipoTalla.UNICA) return "UNICA";
        Toggle elegida = grupoTalla.getSelectedToggle();
        return elegida == null ? null : ((ToggleButton) elegida).getText();
    }

    private void armarTallas() {
        lblColor.setText("Color: " + getColor());
        boxTallas.getChildren().clear();
        grupoTalla.getToggles().clear();
        boolean tallaUnica = producto.getTipoTalla() == TipoTalla.UNICA;
        lblTalla.setText(tallaUnica ? "Talla: Única" : producto.getTipoTalla() == TipoTalla.CALZADO ? "Talla (US):" : "Talla:");
        lnkGuia.setVisible(!tallaUnica);
        if (!tallaUnica) {
            for (String talla : producto.getTipoTalla().getTallas()) {
                if (!producto.getTallaActiva(talla)) continue;             // solo las tallas que maneja el producto
                ToggleButton boton = new ToggleButton(talla);
                boton.getStyleClass().add("talla");
                boton.setToggleGroup(grupoTalla);
                boton.setDisable(producto.getStockDe(talla, getColor()) == 0);   // sin unidades: tachada
                boton.setOnAction(evento -> actualizarCantidad());
                boxTallas.getChildren().add(boton);
            }
        }
        actualizarCantidad();
    }

    private void actualizarCantidad() {
        String talla = getTalla();
        int disponibles = talla == null ? 0 : producto.getStockDe(talla, getColor());
        SpinnerValueFactory.IntegerSpinnerValueFactory valores = (SpinnerValueFactory.IntegerSpinnerValueFactory) spnCantidad.getValueFactory();
        valores.setMax(Math.max(1, disponibles));
        valores.setValue(1);
        spnCantidad.setDisable(disponibles == 0);
        if (talla == null) lblDisponibles.setText("Elige una talla");
        else if (disponibles == 0) lblDisponibles.setText("Agotado en este color");
        else lblDisponibles.setText(disponibles + " disponibles" + (talla.equals("UNICA") ? "" : " en talla " + talla));
        btnCarrito.setDisable(talla != null && disponibles == 0);
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);
    }

    @FXML
    private void agregarAlCarrito() {
        if (getTalla() == null) { mensaje("Elige una talla."); return; }
        if (Sesion.setAgregarAlCarrito(producto, getTalla(), getColor(), spnCantidad.getValue()))
            Navegador.aviso("Agregado al carrito: " + producto.getNombre());
        else mensaje("Ya tienes en el carrito todas las unidades disponibles de esta talla.");
    }

    private void mensaje(String texto) {
        lblMensaje.setText(texto);
        lblMensaje.setVisible(true);
        lblMensaje.setManaged(true);
    }

    // ------------------------------------------------------------------ lista de deseos (me gusta)
    @FXML
    private void cambiarDeseo() {
        Sesion.setAlternarMeGusta(producto);
        actualizarDeseo();
    }

    private void actualizarDeseo() {
        boolean activo = Sesion.getLeGusta(producto);
        btnDeseos.setSelected(activo);
        btnDeseos.setText(activo ? "En tu lista de deseos" : "Añadir a lista de deseos");
        // el CSS rellena el corazón cuando el botón está seleccionado
    }

    // ------------------------------------------------------------------ opiniones (pila circular)
    private void armarOpiniones() {
        int total = producto.getTotalResenas();
        lblPromedio.setText(total == 0 ? "–" : String.format("%.1f", producto.getPromedio()));
        boxEstrellasResumen.getChildren().setAll(Ui.estrellas(producto.getPromedio(), 15));
        lblTotalOpiniones.setText(total + " opiniones");
        lnkOpiniones.setText(total == 0 ? "" : "Ver las " + total + " opiniones");
        boxResenas.getChildren().clear();
        // Desde el tope: las dos reseñas visibles más recientes
        Resena actual = producto.getResenas().tope;
        int mostradas = 0;
        if (actual != null) {
            do {
                if (actual.isVisible()) {
                    boxResenas.getChildren().add(tarjetaResena(actual));
                    mostradas++;
                }
                actual = actual.sig;
            } while (actual != producto.getResenas().tope && mostradas < 2);
        }
        if (mostradas == 0) {
            Label vacio = new Label("Aún no hay opiniones de este producto.");
            vacio.getStyleClass().add("gris");
            boxResenas.getChildren().add(vacio);
        }
        boolean puedeOpinar = Datos.pedidos.getComproYRecibio(Sesion.getUsuario(), producto);
        btnOpinar.setDisable(!puedeOpinar);
        if (!puedeOpinar) btnOpinar.setTooltip(new Tooltip("Podrás opinar cuando recibas este producto."));
    }

    private VBox tarjetaResena(Resena resena) {
        Label autor = new Label(resena.getAutor());
        autor.getStyleClass().add("negrita");
        Label fecha = new Label(resena.getFecha());
        fecha.getStyleClass().add("gris-claro");
        HBox cabecera = new HBox(8, Ui.avatar(resena.getAutor().substring(0, 1), 13, "avatar-oscuro"),
                new VBox(autor, fecha), Ui.espacio(), Ui.estrellas(resena.getEstrellas(), 13));
        cabecera.setAlignment(Pos.CENTER_LEFT);
        Label texto = new Label(resena.getTexto());
        texto.setWrapText(true);
        VBox tarjeta = new VBox(cabecera, texto);
        tarjeta.getStyleClass().add("resena");
        HBox.setHgrow(tarjeta, Priority.ALWAYS);
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        tarjeta.setPrefWidth(10);
        return tarjeta;
    }

    @FXML
    private void verOpiniones() {
        VBox lista = new VBox(10);
        Resena actual = producto.getResenas().tope;
        if (actual != null) {
            do {
                if (actual.isVisible()) lista.getChildren().add(tarjetaResena(actual));
                actual = actual.sig;
            } while (actual != producto.getResenas().tope);
        }
        ScrollPane desplazable = new ScrollPane(lista);
        desplazable.setFitToWidth(true);
        desplazable.setPrefSize(560, 380);
        Dialogos.nuevo("Opiniones · " + producto.getNombre(), desplazable, ButtonType.CLOSE).showAndWait();
    }

    @FXML
    private void escribirOpinion() {
        ToggleGroup grupoEstrellas = new ToggleGroup();
        HBox estrellas = new HBox(6);
        for (int numero = 1; numero <= 5; numero++) {
            ToggleButton boton = new ToggleButton(numero + " ★");
            boton.getStyleClass().add("chip");
            boton.setToggleGroup(grupoEstrellas);
            boton.setUserData(numero);
            estrellas.getChildren().add(boton);
        }
        TextArea comentario = new TextArea();
        comentario.setPromptText("Cuéntanos qué te pareció");
        comentario.setPrefRowCount(4);
        VBox formulario = new VBox(10, new Label("Calificación"), estrellas, new Label("Comentario"), comentario);
        formulario.setPrefWidth(440);
        ButtonType publicar = new ButtonType("Publicar", ButtonBar.ButtonData.OK_DONE);
        Dialogos.nuevo("Escribir opinión", formulario, publicar, ButtonType.CANCEL).showAndWait().ifPresent(respuesta -> {
            if (respuesta == publicar && grupoEstrellas.getSelectedToggle() != null && !comentario.getText().isBlank()) {
                Resena nueva = new Resena(producto, Sesion.getUsuario().getNombreCorto(),
                        (Integer) grupoEstrellas.getSelectedToggle().getUserData(), Fechas.getHoy(),
                        ArchivoTexto.getLimpio(comentario.getText()));
                producto.getResenas().setPush(nueva);     // queda en el tope de la pila
                Datos.setGuardarResenas();                // reescribe resenas.txt
                armarOpiniones();
                Navegador.aviso("¡Gracias! Tu opinión fue publicada.");
            }
        });
    }

    @FXML
    private void verGuia() {
        boolean calzado = producto.getTipoTalla() == TipoTalla.CALZADO;
        String[] cabecera = calzado ? new String[]{"US", "7", "8", "9", "10", "11"} : new String[]{"Talla", "S", "M", "L", "XL", "XXL"};
        String[] medida = calzado ? new String[]{"cm", "25", "26", "27", "28", "29"} : new String[]{"Pecho cm", "90", "96", "102", "108", "114"};
        GridPane tabla = new GridPane();
        tabla.setHgap(2);
        tabla.setVgap(2);
        for (int columna = 0; columna < cabecera.length; columna++) {
            Label arriba = new Label(cabecera[columna]), abajo = new Label(medida[columna]);
            for (Label celda : new Label[]{arriba, abajo}) {
                celda.setMinWidth(70);
                celda.setAlignment(Pos.CENTER);
                celda.setStyle("-fx-padding: 8; -fx-border-color: #E2E8F0;");
            }
            arriba.setStyle(arriba.getStyle() + "-fx-background-color: #E8F1FF; -fx-font-weight: 700;");
            tabla.add(arriba, columna, 0);
            tabla.add(abajo, columna, 1);
        }
        Dialogos.nuevo("Guía de tallas · " + (calzado ? "Calzado" : "Textil"), tabla, ButtonType.CLOSE).showAndWait();
    }

    @FXML private void volver() { Navegador.tienda("Catalogo"); }
}
