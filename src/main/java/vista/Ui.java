package vista;

import controlador.*;
import modelo.*;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/** Pequeñas piezas visuales que se repiten en varias pantallas. */
public final class Ui {
    private Ui() {}

    public static HBox estrellas(double valor, double tamano) {
        HBox fila = new HBox(2);
        fila.setAlignment(Pos.CENTER_LEFT);
        for (int numero = 1; numero <= 5; numero++) {
            boolean llena = valor >= numero - 0.25;
            fila.getChildren().add(Iconos.crear("star", tamano, llena ? "#F59E0B" : "#CBD5E1", true));
        }
        return fila;
    }

    public static StackPane avatar(String iniciales, double radio, String estilo) {
        Circle circulo = new Circle(radio);
        circulo.getStyleClass().add(estilo == null ? "avatar" : estilo);
        Label texto = new Label(iniciales);
        texto.getStyleClass().add("avatar-texto");
        texto.setStyle("-fx-font-size: " + Math.round(radio * 0.8) + "px;");
        return new StackPane(circulo, texto);
    }

    public static Label estado(EstadoPedido estado) {
        Label etiqueta = new Label("● " + estado.getTexto());
        etiqueta.getStyleClass().addAll("estado", estado.getEstilo());
        return etiqueta;
    }

    public static Label pill(String texto, String estilo) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().addAll("estado", estilo);
        return etiqueta;
    }

    /** Foto de producto centrada sobre fondo claro. */
    public static StackPane foto(String imagen, double ancho, double alto) {
        ImageView vista = imagen(Imagenes.producto(imagen, Math.max(ancho, alto) * 2), ancho * 0.9, alto * 0.9);
        StackPane marco = new StackPane(vista);
        marco.getStyleClass().add("fondo-foto");
        marco.setMinSize(ancho, alto);
        marco.setPrefSize(ancho, alto);
        marco.setMaxSize(ancho, alto);
        return marco;
    }

    public static ImageView imagen(Image imagen, double ancho, double alto) {
        ImageView vista = new ImageView(imagen);
        vista.setFitWidth(ancho);
        vista.setFitHeight(alto);
        vista.setPreserveRatio(true);
        vista.setSmooth(true);
        return vista;
    }

    /** Espacio que empuja lo siguiente hacia la derecha dentro de un HBox. */
    public static Node espacio() {
        Region relleno = new Region();
        HBox.setHgrow(relleno, Priority.ALWAYS);
        return relleno;
    }
}
