package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** 03 · Crear cuenta. */
public class frmRegistro {
    @FXML private TextField txtNombre, txtApellido, txtCorreo, txtTelefono;
    @FXML private PasswordField txtClave, txtClave2;
    @FXML private CheckBox chkTerminos;
    @FXML private Label lblError;

    @FXML
    private void crear() {
        String nombre = txtNombre.getText().trim(), apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim(), clave = txtClave.getText();
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty()) { error("Completa los campos marcados con *."); return; }
        if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) { error("El correo no es válido."); return; }
        if (Datos.usuarios.getBuscarxCorreo(correo) != null) { error("Ya existe una cuenta con ese correo."); return; }
        if (clave.length() < 8) { error("La contraseña debe tener mínimo 8 caracteres."); return; }
        if (!clave.equals(txtClave2.getText())) { error("Las contraseñas no coinciden."); return; }
        if (!chkTerminos.isSelected()) { error("Debes aceptar los términos para continuar."); return; }

        Usuario nuevo = new Usuario(Datos.usuarios.getSiguienteId(), ArchivoTexto.getLimpio(nombre), ArchivoTexto.getLimpio(apellido), ArchivoTexto.getLimpio(correo),
                ArchivoTexto.getLimpio(txtTelefono.getText()), "", Seguridad.getHash(clave), false, Fechas.getMesActual());
        Datos.usuarios.setAddF(nuevo);
        if (!Datos.setGuardarUsuarios()) { error(Datos.error); return; }   // usuarios.txt
        Sesion.setIniciar(nuevo);
        Navegador.tienda("Catalogo");
    }

    private void error(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    @FXML private void irLogin() { Navegador.ir("Login"); }
}
