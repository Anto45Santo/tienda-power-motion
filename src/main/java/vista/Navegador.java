package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Cambia de pantalla dentro de la misma ventana.
 *   ir("Login")             → reemplaza toda la ventana (Inicio, Login, Registro)
 *   tienda("Catalogo")      → muestra la pantalla debajo de la barra de la tienda
 *   admin("AdminPedidos")   → muestra la pantalla a la derecha del menú del administrador
 * Los .fxml y sus .css están en src/main/resources/vista/
 */
public final class Navegador {
    public static final double ANCHO = 1200, ALTO = 688;
    private static Stage ventana;
    private static Scene escena;
    private static frmTienda tienda;
    private static frmAdmin admin;

    private Navegador() {}

    public static void iniciar(Stage ventanaPrincipal) {
        ventana = ventanaPrincipal;
        escena = new Scene(new StackPane(), ANCHO, ALTO);
        escena.getStylesheets().add(estilos());
        ventana.setScene(escena);
        Sesion.alCambiar = Navegador::actualizarBarra;    // contadores de me gusta y carrito
    }

    public static Stage getStage() { return ventana; }

    public static FXMLLoader cargar(String fxml) {
        FXMLLoader cargador = new FXMLLoader(Navegador.class.getResource(fxml + ".fxml"));
        try {
            cargador.load();
        } catch (IOException error) {
            throw new RuntimeException("No se pudo abrir " + fxml + ".fxml", error);
        }
        return cargador;
    }

    public static <T> T ir(String fxml) {
        FXMLLoader cargador = cargar(fxml);
        escena.setRoot(cargador.getRoot());
        tienda = null;
        admin = null;
        return cargador.getController();
    }

    public static <T> T tienda(String fxml) {
        if (tienda == null) {
            FXMLLoader marco = cargar("Tienda");
            tienda = marco.getController();
            escena.setRoot(marco.getRoot());
            admin = null;
        }
        FXMLLoader cargador = cargar(fxml);
        tienda.mostrar((Parent) cargador.getRoot());
        return cargador.getController();
    }

    public static <T> T admin(String fxml) {
        if (admin == null) {
            FXMLLoader marco = cargar("Admin");
            admin = marco.getController();
            escena.setRoot(marco.getRoot());
            tienda = null;
        }
        FXMLLoader cargador = cargar(fxml);
        admin.mostrar((Parent) cargador.getRoot(), fxml);
        return cargador.getController();
    }

    public static void actualizarBarra() {
        if (tienda != null) tienda.actualizarContadores();
        if (admin != null) admin.actualizarContadores();
    }

    /** Mensaje corto en la parte inferior (tienda o admin). */
    public static void aviso(String texto) {
        if (tienda != null) tienda.aviso(texto);
        if (admin != null) admin.aviso(texto);
    }

    public static frmAdmin getAdmin() { return admin; }

    /** Hoja de estilos común (también para los diálogos). */
    public static String estilos() {
        return Navegador.class.getResource("estilos.css").toExternalForm();
    }
}
