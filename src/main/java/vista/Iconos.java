package vista;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.HashMap;
import java.util.Map;

/**
 * Iconos de trazo (24 x 24) como SVGPath. Uso: Iconos.crear("heart", 20, "#2583FF")
 * Se usan para lo que se crea desde el código (filas de tablas, reseñas, chips de colores…).
 * Los iconos fijos de cada pantalla están escritos directamente en su .fxml con los mismos trazos.
 */
public final class Iconos {
    private static final Map<String, String> D = new HashMap<>();
    static {
        D.put("search", "M4 10.8a6.8 6.8 0 1 0 13.6 0a6.8 6.8 0 1 0 -13.6 0 M15.6 15.6 20 20");
        D.put("user", "M8.4 8a3.6 3.6 0 1 0 7.2 0a3.6 3.6 0 1 0 -7.2 0 M4.8 20c.6-4 3-6 7.2-6s6.6 2 7.2 6");
        D.put("heart", "M12 20s-7.5-4.6-7.5-10.2A4.2 4.2 0 0 1 12 7.3a4.2 4.2 0 0 1 7.5 2.5C19.5 15.4 12 20 12 20z");
        D.put("bag", "M5 8h14l1 12H4L5 8Z M9 9V6a3 3 0 0 1 6 0v3");
        D.put("menu", "M4 7h16M4 12h16M4 17h16");
        D.put("sliders", "M4 7h10M18 7h2M4 17h4M12 17h8 M14 7a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 M8 17a2 2 0 1 0 4 0a2 2 0 1 0 -4 0");
        D.put("close", "M6 6l12 12M18 6 6 18");
        D.put("check", "m5 12 5 5 9-10");
        D.put("eye", "M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z M9 12a3 3 0 1 0 6 0a3 3 0 1 0 -6 0");
        D.put("edit", "M4 20h4L19 9l-4-4L4 16z");
        D.put("trash", "M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13");
        D.put("plus", "M12 5v14M5 12h14");
        D.put("upload", "M12 16V4M7 9l5-5 5 5M4 20h16");
        D.put("copy", "M10 8H18a2 2 0 0 1 2 2V18a2 2 0 0 1 -2 2H10a2 2 0 0 1 -2 -2V10a2 2 0 0 1 2 -2z M16 8V5a1 1 0 0 0-1-1H5a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h3");
        D.put("logout", "M10 4H5v16h5M15 8l4 4-4 4M19 12H9");
        D.put("box", "M3 7l9-4 9 4v10l-9 4-9-4z M3 7l9 4 9-4M12 11v10");
        D.put("tag", "M3 12V4h8l10 10-8 8z M6.2 8.5a1.3 1.3 0 1 0 2.6 0a1.3 1.3 0 1 0 -2.6 0");
        D.put("image", "M5 4H19a2 2 0 0 1 2 2V18a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V6a2 2 0 0 1 2 -2z M7 10a2 2 0 1 0 4 0a2 2 0 1 0 -4 0 M3 18l6-6 5 5 3-3 3 3");
        D.put("chat", "M4 5h16v11H9l-5 4z M8 10h8M8 13h5");
        D.put("users", "M5.8 8a3.2 3.2 0 1 0 6.4 0a3.2 3.2 0 1 0 -6.4 0 M3 20c.5-3.5 2.6-5.5 6-5.5s5.5 2 6 5.5 M14.5 9a2.5 2.5 0 1 0 5 0a2.5 2.5 0 1 0 -5 0 M16.5 14.5c2.5 0 4 1.7 4.5 4.5");
        D.put("lock", "M7 11H17a2 2 0 0 1 2 2V19a2 2 0 0 1 -2 2H7a2 2 0 0 1 -2 -2V13a2 2 0 0 1 2 -2z M8 11V8a4 4 0 0 1 8 0v3");
        D.put("mail", "M5 5H19a2 2 0 0 1 2 2V17a2 2 0 0 1 -2 2H5a2 2 0 0 1 -2 -2V7a2 2 0 0 1 2 -2z M3 7l9 6 9-6");
        D.put("phone", "M5 4h4l2 5-2.5 1.5a11 11 0 0 0 5 5L15 13l5 2v4a2 2 0 0 1-2 2A16 16 0 0 1 3 6a2 2 0 0 1 2-2");
        D.put("pin", "M12 21s-7-6-7-11a7 7 0 0 1 14 0c0 5-7 11-7 11z M9.5 10a2.5 2.5 0 1 0 5 0a2.5 2.5 0 1 0 -5 0");
        D.put("star", "M12 3.2l2.7 5.6 6.1.8-4.5 4.2 1.1 6.1L12 17l-5.4 2.9 1.1-6.1-4.5-4.2 6.1-.8z");
        D.put("left", "m15 6-6 6 6 6");
        D.put("right", "m9 6 6 6-6 6");
        D.put("down", "m6 9 6 6 6-6");
        D.put("truck", "M3 6h11v10H3z M14 10h4l3 3v3h-7 M5.3 18a1.7 1.7 0 1 0 3.4 0a1.7 1.7 0 1 0 -3.4 0 M15.3 18a1.7 1.7 0 1 0 3.4 0a1.7 1.7 0 1 0 -3.4 0");
        D.put("refresh", "M20 11a8 8 0 1 0-2.3 5.7 M20 5v6h-6");
        D.put("shield", "M12 3l7 3v5c0 5-3 8-7 10-4-2-7-5-7-10V6z M9 12l2 2 4-4");
        D.put("dollar", "M12 3v18M16.5 7.5c-.6-1.6-2.4-2.5-4.5-2.5-2.5 0-4.5 1.3-4.5 3.4 0 4.6 9.2 2.3 9.2 7.1 0 2.2-2 3.5-4.7 3.5-2.4 0-4.3-1-4.9-2.9");
        D.put("clock", "M3.5 12a8.5 8.5 0 1 0 17 0a8.5 8.5 0 1 0 -17 0 M12 7.5V12l3 2");
        D.put("alert", "M12 4 2.5 20h19z M12 10v4.5M12 17.2v.1");
        D.put("ruler", "M3 17 17 3l4 4L7 21z M7 13l2 2M10 10l2 2M13 7l2 2");
        D.put("send", "M21 3 10 14M21 3l-7 18-4-7-7-4z");
        D.put("calendar", "M5.5 5H18.5a2 2 0 0 1 2 2V18a2 2 0 0 1 -2 2H5.5a2 2 0 0 1 -2 -2V7a2 2 0 0 1 2 -2z M3.5 10h17M8 3v4M16 3v4");
    }

    private Iconos() {}

    /** Icono de contorno. */
    public static Node crear(String nombre, double tamano, String color) {
        return crear(nombre, tamano, color, false);
    }

    /**
     * Icono; si relleno = true se pinta lleno (corazón activo, estrellas).
     * El trazo va dentro de un cuadro invisible de 24 x 24: así todos los iconos
     * ocupan el mismo espacio y quedan centrados aunque su dibujo sea más pequeño.
     */
    public static Node crear(String nombre, double tamano, String color, boolean relleno) {
        SVGPath trazo = new SVGPath();
        trazo.setContent(D.getOrDefault(nombre, ""));
        trazo.setStroke(Color.web(color));
        trazo.setStrokeWidth(1.9);
        trazo.setStrokeLineCap(StrokeLineCap.ROUND);
        trazo.setStrokeLineJoin(StrokeLineJoin.ROUND);
        trazo.setFill(relleno ? Color.web(color) : Color.TRANSPARENT);

        Rectangle cuadro = new Rectangle(0, 0, 24, 24);   // marco fijo del icono (no se ve)
        cuadro.setFill(Color.TRANSPARENT);
        cuadro.setMouseTransparent(true);

        Group dibujo = new Group(cuadro, trazo);
        double escala = tamano / 24.0;
        dibujo.setScaleX(escala);
        dibujo.setScaleY(escala);

        Group icono = new Group(dibujo);       // el Group exterior toma el tamaño ya escalado
        icono.getStyleClass().add("icono");
        return icono;
    }
}
