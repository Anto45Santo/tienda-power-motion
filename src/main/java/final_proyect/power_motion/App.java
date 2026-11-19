package final_proyect.power_motion;

import controlador.Datos;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import vista.Navegador;

/** Punto de entrada. La primera pantalla es la bienvenida (vista/Inicio.fxml). */
public class App extends Application {

    @Override
    public void start(Stage ventana) {
        for (String fuente : new String[]{"Inter_Regular", "Inter_SemiBold", "Inter_Bold", "Inter_ExtraBold"}) {
            var fontStream = App.class.getResourceAsStream("/fonts/" + fuente + ".ttf");
            if (fontStream != null) {
                Font.loadFont(fontStream, 13);
            }
        }

        Datos.setCargar();

        Navegador.iniciar(ventana);
        Navegador.ir("Inicio");

        ventana.setTitle("Power Motion");
        var iconStream = App.class.getResourceAsStream("/img/icono.png");
        if (iconStream != null) {
            ventana.getIcons().add(new Image(iconStream));
        }
        ventana.setMinWidth(1100);
        ventana.setMinHeight(680);
        ventana.show();

        if (!Datos.error.isEmpty()) {
            Navegador.aviso(Datos.error);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}