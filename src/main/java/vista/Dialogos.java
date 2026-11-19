package vista;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.Optional;

/** Ventanas emergentes con el estilo de la aplicación (reemplazan a JOptionPane de Swing). */
public final class Dialogos {
    private Dialogos() {}

    public static <R> Dialog<R> nuevo(String titulo, Node contenido, ButtonType... botones) {
        Dialog<R> dialogo = new Dialog<>();
        dialogo.initOwner(Navegador.getStage());
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);
        dialogo.getDialogPane().getStylesheets().add(Navegador.estilos());
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().addAll(botones);
        return dialogo;
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.initOwner(Navegador.getStage());
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.getDialogPane().getStylesheets().add(Navegador.estilos());
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }
}
