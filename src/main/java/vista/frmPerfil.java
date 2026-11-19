package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class frmPerfil {
    @FXML private Hyperlink lnkVolver;
    @FXML private HBox cabecera;
    @FXML private Label lblNombre, lblCorreo, lblDesde, lblCompras, lblMeGusta, lblMensaje, lblIniciales;
    @FXML private TextField txtNombre, txtApellido, txtCorreo, txtTelefono, txtDireccion;
    @FXML private Button btnClave;
    private Usuario conectado;

    @FXML
    private void initialize() {
        conectado = Sesion.getUsuario();
        lblIniciales.setText(conectado.getIniciales());   // el avatar y los iconos están en Perfil.fxml
        lblDesde.setText("Cliente desde " + conectado.getDesde());
        lblCompras.setText(String.valueOf(Datos.pedidos.getContarxCliente(conectado)));
        lblMeGusta.setText(String.valueOf(Sesion.getMeGusta().getLongPila()));
        txtNombre.setText(conectado.getNombre());
        txtApellido.setText(conectado.getApellido());
        txtCorreo.setText(conectado.getCorreo());
        txtTelefono.setText(conectado.getTelefono());
        txtDireccion.setText(conectado.getDireccion());
        mostrarCabecera();
    }

    private void mostrarCabecera() {
        lblNombre.setText(conectado.getNombreCompleto());
        lblCorreo.setText(conectado.getCorreo());
    }

    @FXML
    private void guardar() {
        String correo = txtCorreo.getText().trim();
        if (txtNombre.getText().isBlank() || txtApellido.getText().isBlank() || correo.isEmpty()) {
            lblMensaje.setText("Nombre, apellido y correo son obligatorios."); return;
        }
        Usuario otro = Datos.usuarios.getBuscarxCorreo(correo);
        if (otro != null && otro != conectado) {
            lblMensaje.setText("Ese correo ya lo usa otra cuenta."); return;
        }
        conectado.setNombre(ArchivoTexto.getLimpio(txtNombre.getText()));
        conectado.setApellido(ArchivoTexto.getLimpio(txtApellido.getText()));
        conectado.setCorreo(ArchivoTexto.getLimpio(correo));
        conectado.setTelefono(ArchivoTexto.getLimpio(txtTelefono.getText()));
        conectado.setDireccion(ArchivoTexto.getLimpio(txtDireccion.getText()));
        if (!Datos.setGuardarUsuarios()) { lblMensaje.setText(Datos.error); return; }   // usuarios.txt
        lblMensaje.setText("");
        mostrarCabecera();
        Navegador.aviso("Datos guardados");
    }

    @FXML
    private void cambiarClave() {
        PasswordField actual = new PasswordField(), nueva = new PasswordField(), repetir = new PasswordField();
        VBox formulario = new VBox(6, new Label("Contraseña actual"), actual, new Label("Nueva contraseña (mínimo 8)"), nueva,
                new Label("Repite la nueva contraseña"), repetir);
        formulario.setPrefWidth(360);
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        Dialogos.nuevo("Cambiar contraseña", formulario, guardar, ButtonType.CANCEL).showAndWait().ifPresent(respuesta -> {
            if (respuesta != guardar) return;
            // en usuarios.txt solo está el hash SHA-256: se compara el hash de lo que se escribió
            if (!Seguridad.getCoincide(actual.getText(), conectado.getClave())) Navegador.aviso("La contraseña actual no es correcta.");
            else if (nueva.getText().length() < 8 || !nueva.getText().equals(repetir.getText())) Navegador.aviso("Revisa la nueva contraseña.");
            else {
                conectado.setClave(Seguridad.getHash(nueva.getText()));
                Datos.setGuardarUsuarios();
                Navegador.aviso("Contraseña actualizada");
            }
        });
    }

    @FXML
    private void eliminarCuenta() {
        boolean pendientes = Datos.pedidos.getTieneEnProceso(conectado);
        if (pendientes) { Navegador.aviso("No puedes eliminar la cuenta mientras tengas compras en proceso."); return; }
        if (Datos.pedidos.getContarxCliente(conectado) > 0) {   // los pedidos guardados necesitan al cliente
            Navegador.aviso("Tu cuenta tiene compras registradas; pide al administrador que la dé de baja."); return;
        }
        if (Dialogos.confirmar("Eliminar mi cuenta", "Se borrarán tus datos y no se puede deshacer. ¿Continuar?")) {
            Sesion.setBorrarDatosDe(conectado.getId());     // sus líneas de megusta.txt y carrito.txt
            Datos.usuarios.setDelxId(conectado.getId());
            Datos.setGuardarUsuarios();
            cerrarSesion();
        }
    }

    @FXML private void cerrarSesion() { Sesion.setCerrar(); Navegador.ir("Login"); }
    @FXML private void volver() { Navegador.tienda("Catalogo"); }
}
