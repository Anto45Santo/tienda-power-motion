package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

/** 02 · Iniciar sesión. */
public class frmLogin {
    @FXML private HBox boxCorreo, boxClave;
    @FXML private TextField txtCorreo, txtClaveVisible;
    @FXML private PasswordField txtClave;
    @FXML private ToggleButton btnVer;
    @FXML private Label lblError;

    @FXML
    private void initialize() {
        // Los iconos del correo, la contraseña y el ojo están en Login.fxml
        txtClaveVisible.textProperty().bindBidirectional(txtClave.textProperty());
    }

    /** Ojo: muestra u oculta la contraseña cambiando entre PasswordField y TextField. */
    @FXML
    private void verClave() {
        boolean mostrar = btnVer.isSelected();
        txtClaveVisible.setVisible(mostrar);
        txtClave.setVisible(!mostrar);
    }

    @FXML
    private void ingresar() {
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();
        if (correo.isEmpty() || clave.isEmpty()) { error("Escribe tu correo y tu contraseña."); return; }
        Usuario encontrado = Datos.usuarios.getValidar(correo, clave);   // recorre la lista doble de usuarios
        if (encontrado == null) { error("Correo o contraseña incorrectos."); return; }
        Sesion.setIniciar(encontrado);
        if (encontrado.isAdmin()) Navegador.admin("AdminPedidos");
        else Navegador.tienda("Catalogo");          // el cliente ve el catálogo de inmediato
    }

    private void error(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    @FXML private void irRegistro() { Navegador.ir("Registro"); }
    @FXML private void volver() { Navegador.ir("Inicio"); }
}
