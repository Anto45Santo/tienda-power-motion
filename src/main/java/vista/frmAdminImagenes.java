package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;

public class frmAdminImagenes {
    private static final String CARPETA_BANNERS = ArchivoTexto.CARPETA_IMAGENES + "/banners";
    @FXML private HBox boxFotos, boxGuias;
    @FXML private Button btnAgregar;

    @FXML
    private void initialize() {
        armarFotos();          // las guías de tallas están escritas en AdminImagenes.fxml
    }

    /** Recorre una vuelta la lista doble circular de fotos de la bienvenida. */
    private void armarFotos() {
        boxFotos.getChildren().clear();
        if (Datos.fotosBienvenida.getEsVacia()) return;
        int posicion = 1;
        NodoImagen actual = Datos.fotosBienvenida.cab;
        do {
            boxFotos.getChildren().add(tarjetaFoto(actual.archivo, posicion++));
            actual = actual.sig;
        } while (actual != Datos.fotosBienvenida.cab);
    }

    private VBox tarjetaFoto(String archivo, int posicion) {
        ImageView vistaFoto = new ImageView(Imagenes.banner(archivo, 600, 400));
        vistaFoto.setFitWidth(290);
        vistaFoto.setFitHeight(150);
        vistaFoto.setPreserveRatio(false);
        vistaFoto.setClip(new Rectangle(290, 150));
        Label orden = new Label("Posición " + posicion);
        orden.getStyleClass().add("negrita");
        Hyperlink cambiar = new Hyperlink("Cambiar");
        cambiar.setOnAction(evento -> {           // la nueva foto ocupa la misma posición del nodo
            String nueva = copiarFoto();
            if (nueva == null) return;
            Datos.fotosBienvenida.getBuscarxArchivo(archivo).archivo = nueva;
            ArchivoTexto.setBorrarImagen(CARPETA_BANNERS, archivo);
            Datos.setGuardarFotos();
            armarFotos();
            Navegador.aviso("Foto de la posición " + posicion + " cambiada");
        });
        Hyperlink eliminar = new Hyperlink("Eliminar");
        eliminar.getStyleClass().add("link-rojo");
        eliminar.setOnAction(evento -> {
            if (Datos.fotosBienvenida.getLongLista() == 1) { Navegador.aviso("Debe quedar al menos una foto."); return; }
            Datos.fotosBienvenida.setDelxArchivo(archivo);
            ArchivoTexto.setBorrarImagen(CARPETA_BANNERS, archivo);   // solo borra las que copió la aplicación
            Datos.setGuardarFotos();                                    // fotos.txt
            armarFotos();
            Navegador.aviso("Foto eliminada de la rotación");
        });
        HBox pie = Celdas.fila(10, orden, Ui.espacio(), cambiar, eliminar);
        pie.setPadding(new Insets(0, 10, 0, 10));
        VBox tarjeta = new VBox(8, vistaFoto, pie);
        tarjeta.getStyleClass().add("tarjeta-foto");
        return tarjeta;
    }

    @FXML
    private void agregarFoto() {
        String nueva = copiarFoto();
        if (nueva == null) return;
        Datos.fotosBienvenida.setAddF(nueva);                 // queda al final de la lista circular
        Datos.setGuardarFotos();
        armarFotos();
        Navegador.aviso("Foto agregada al final de la rotación");
    }

    /** Elige una imagen y la copia a imagenes/banners. Devuelve el nombre con el que quedó o null. */
    private String copiarFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Foto de la bienvenida (recomendado 1920 × 800 px)");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png"));
        File archivo = selector.showOpenDialog(Navegador.getStage());
        if (archivo == null) return null;
        try {
            return ArchivoTexto.setCopiarImagen(archivo, CARPETA_BANNERS, "banner");
        } catch (java.io.IOException problema) {
            Navegador.aviso("No se pudo copiar la imagen: " + problema.getMessage());
            return null;
        }
    }
}
