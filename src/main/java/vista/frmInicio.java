package vista;

import controlador.*;
import modelo.*;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** 01 · Bienvenida. Las fotos rotan recorriendo la lista doble circular (actual = actual.sig). */
public class frmInicio {
    @FXML private ImageView imgFoto;
    @FXML private VBox beneficios;
    private NodoImagen fotoActual;
    private Timeline rotacion;

    @FXML
    private void initialize() {
        // Los beneficios y la primera foto están en Inicio.fxml; aquí empieza la rotación de fotos
        fotoActual = Datos.fotosBienvenida.cab;
        mostrarFoto();
        // Cada 5 segundos pasa a la siguiente; al ser circular vuelve sola a la primera.
        rotacion = new Timeline(new KeyFrame(Duration.seconds(5), evento -> {
            fotoActual = fotoActual.sig;
            FadeTransition aparecer = new FadeTransition(Duration.millis(400), imgFoto);
            aparecer.setFromValue(0.3);
            aparecer.setToValue(1);
            mostrarFoto();
            aparecer.play();
        }));
        rotacion.setCycleCount(Timeline.INDEFINITE);
        rotacion.play();
    }

    private void mostrarFoto() {
        if (fotoActual != null) imgFoto.setImage(Imagenes.banner(fotoActual.archivo, 1400, 900));
    }

    @FXML private void irLogin() { rotacion.stop(); Navegador.ir("Login"); }
    @FXML private void irRegistro() { rotacion.stop(); Navegador.ir("Registro"); }
}
