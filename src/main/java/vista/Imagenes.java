package vista;

import controlador.*;
import modelo.*;

import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga imágenes. Primero busca en la carpeta externa "imagenes/" (las que sube el administrador);
 * si no existe, usa las de ejemplo incluidas en el proyecto (resources/img).
 */
public final class Imagenes {
    private static final Map<String, Image> CACHE = new HashMap<>();
    /** Carpeta de imágenes dentro del proyecto (src/main/resources/img). */
    private static final String RECURSOS = "/img/";

    private Imagenes() {}

    public static Image producto(String nombre, double tamano) {
        return cargar("productos", nombre, tamano, tamano);
    }

    public static Image banner(String nombre, double ancho, double alto) {
        return cargar("banners", nombre, ancho, alto);
    }

    private static Image cargar(String carpeta, String nombre, double ancho, double alto) {
        if (nombre == null) return null;
        String clave = carpeta + "/" + nombre + "@" + (int) ancho + "x" + (int) alto;
        return CACHE.computeIfAbsent(clave, nueva -> {
            File externo = new File("imagenes/" + carpeta + "/" + nombre);
            if (externo.exists()) return new Image(externo.toURI().toString(), ancho, alto, true, true, true);
            InputStream archivo = Imagenes.class.getResourceAsStream(RECURSOS + carpeta + "/" + nombre);
            return archivo == null ? null : new Image(archivo, ancho, alto, true, true);
        });
    }
}
