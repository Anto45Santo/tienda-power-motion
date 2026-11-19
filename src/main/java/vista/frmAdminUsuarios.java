package vista;

import controlador.*;
import modelo.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class frmAdminUsuarios {
    // Indicadores y columnas están en AdminUsuarios.fxml
    @FXML private Label lblUsuarios, lblConCompras, lblMeGusta, lblAdmins;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, Usuario> colUsuario, colTelefono, colRol, colCompras, colDesde, colAcciones;

    @FXML
    private void initialize() {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);   // las columnas llenan el ancho de la tabla
        Celdas.llenar(colUsuario, usuario -> Celdas.fila(10,
                Ui.avatar(usuario.getIniciales(), 15, "avatar-oscuro"), Celdas.dosLineas(usuario.getNombreCompleto(), usuario.getCorreo())));
        Celdas.llenar(colTelefono, usuario -> Celdas.texto(usuario.getTelefono(), null));
        Celdas.llenar(colRol, usuario -> usuario.isAdmin()
                ? Ui.pill("● Admin", "estado-admin") : Ui.pill("● Cliente", "estado-neutro"));
        Celdas.llenar(colCompras, usuario ->
                Celdas.texto(String.valueOf(Datos.pedidos.getContarxCliente(usuario)), "negrita"));
        Celdas.llenar(colDesde, usuario -> Celdas.texto(usuario.getDesde(), null));
        Celdas.llenar(colAcciones, usuario -> {
            Button ver = new Button(null, Iconos.crear("eye", 16, "#2583FF"));
            ver.getStyleClass().addAll("btn-accion", "btn-ver");
            ver.setTooltip(new Tooltip("Ver ficha"));
            ver.setOnAction(evento -> ficha(usuario));
            boolean puedeEliminar = Datos.pedidos.getContarxCliente(usuario) == 0 && !usuario.isAdmin();
            Button borrar = new Button(null, Iconos.crear("trash", 16, puedeEliminar ? "#E11D48" : "#CBD5E1"));
            borrar.getStyleClass().addAll("btn-accion", puedeEliminar ? "btn-rechazar" : "btn-gris");
            borrar.setDisable(!puedeEliminar);
            borrar.setOnAction(evento -> {
                if (Dialogos.confirmar("Eliminar usuario", "¿Eliminar a " + usuario.getNombreCompleto() + "?")) {
                    Sesion.setBorrarDatosDe(usuario.getId());
                    Datos.usuarios.setDelxId(usuario.getId());
                    Datos.setGuardarUsuarios();                     // usuarios.txt
                    refrescar();
                }
            });
            return Celdas.fila(6, ver, borrar);
        });
        txtBuscar.textProperty().addListener((propiedad, anterior, nuevo) -> refrescar());
        refrescar();
    }

    /** Recorre la lista doble de usuarios: cuenta indicadores y arma las filas de la tabla. */
    private void refrescar() {
        int total = 0, conCompras = 0;
        String texto = txtBuscar.getText().toLowerCase().trim();
        ObservableList<Usuario> filas = FXCollections.observableArrayList();
        Usuario actual = Datos.usuarios.cab;
        while (actual != null) {
            total++;
            if (Datos.pedidos.getContarxCliente(actual) > 0) conCompras++;
            if (texto.isEmpty() || (actual.getNombreCompleto() + " " + actual.getCorreo()).toLowerCase().contains(texto)) filas.add(actual);
            actual = actual.sig;
        }
        lblUsuarios.setText(String.valueOf(total));
        lblConCompras.setText(String.valueOf(conCompras));
        lblMeGusta.setText(String.valueOf(ArchivoTexto.getContarMeGusta()));
        lblAdmins.setText(String.valueOf(Datos.usuarios.getContarAdmins()));
        tabla.setItems(filas);
    }

    /** Ficha del usuario: el administrador puede restablecer la contraseña. */
    private void ficha(Usuario usuario) {
        GridPane datos = new GridPane();
        datos.setHgap(14);
        datos.setVgap(8);
        String[][] campos = {{"Nombre", usuario.getNombreCompleto()}, {"Correo", usuario.getCorreo()}, {"Teléfono", usuario.getTelefono()},
                {"Dirección", usuario.getDireccion().isEmpty() ? "—" : usuario.getDireccion()},
                {"Compras", String.valueOf(Datos.pedidos.getContarxCliente(usuario))}};
        for (int fila = 0; fila < campos.length; fila++) {
            datos.add(Celdas.texto(campos[fila][0], "gris"), 0, fila);
            datos.add(Celdas.texto(campos[fila][1], "negrita"), 1, fila);
        }
        ButtonType restablecer = new ButtonType("Restablecer contraseña", ButtonBar.ButtonData.LEFT);
        Dialogos.nuevo(usuario.getNombreCompleto(), datos, restablecer, ButtonType.CLOSE).showAndWait().ifPresent(boton -> {
            if (boton == restablecer) {
                usuario.setClave(Seguridad.getHash("12345678"));
                Datos.setGuardarUsuarios();
                Navegador.aviso("Contraseña restablecida a 12345678. Pide al usuario que la cambie.");
            }
        });
    }
}
