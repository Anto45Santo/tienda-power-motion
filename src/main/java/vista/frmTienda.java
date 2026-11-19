package vista;

import controlador.*;
import modelo.*;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

/** Marco de la tienda: barra superior fija (Categorías, buscador, me gusta, carrito, usuario). */
public class frmTienda {
    @FXML private MenuButton btnCategorias, btnUsuario;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar, btnMeGusta, btnCarrito;
    @FXML private Label lblMeGusta, lblCarrito, lblAviso, lblInicial, lblIniciales, lblNombreMenu, lblCorreoMenu;
    @FXML private StackPane contenido;
    private final PauseTransition ocultarAviso = new PauseTransition(Duration.seconds(2.5));

    @FXML
    private void initialize() {
        armarCategorias();
        armarMenuUsuario();
        actualizarContadores();
        ocultarAviso.setOnFinished(evento -> lblAviso.setVisible(false));
    }

    /** Menú Categorías: Hombre ▸ Calzado ▸ Running … (solo se abre una a la vez). */
    private void armarCategorias() {
        for (String genero : new String[]{"Hombre", "Mujer"}) {
            Menu menuGenero = new Menu(genero);
            Menu calzado = new Menu("Calzado");
            for (String deporte : new String[]{"Running", "Training", "Casual"})
                calzado.getItems().add(opcion(deporte, genero, "Calzado", deporte));
            Menu ropa = new Menu("Ropa");
            for (String prenda : new String[]{"Camisetas", "Chaquetas", "Pantalonetas", "Licras", "Tops", "Buzos"})
                ropa.getItems().add(opcion(prenda, genero, "Ropa", prenda));
            menuGenero.getItems().addAll(calzado, ropa, opcion("Accesorios", genero, "Accesorios", null),
                    new SeparatorMenuItem(), opcion("Ver todo " + genero, genero, null, null));
            btnCategorias.getItems().add(menuGenero);
        }
        Menu accesorios = new Menu("Accesorios");
        for (String tipo : new String[]{"Maletines", "Termos", "Training", "Recuperación"})
            accesorios.getItems().add(opcion(tipo, null, "Accesorios", tipo));
        Menu marcas = new Menu("Marcas");
        for (String marca : new String[]{"Adidas", "Nike", "Puma", "Reebok", "Asics", "Fila", "Skechers"}) {
            MenuItem opcionMarca = new MenuItem(marca);
            opcionMarca.setOnAction(evento -> abrirCatalogo(catalogo -> catalogo.filtrarMarca(marca)));
            marcas.getItems().add(opcionMarca);
        }
        btnCategorias.getItems().addAll(accesorios, marcas, new SeparatorMenuItem(), opcion("Ver todo", null, null, null));
    }

    private MenuItem opcion(String texto, String genero, String categoria, String subcategoria) {
        MenuItem opcion = new MenuItem(texto);
        opcion.setOnAction(evento -> abrirCatalogo(catalogo -> catalogo.filtrarCategoria(genero, categoria, subcategoria)));
        return opcion;
    }

    private void abrirCatalogo(Consumer<frmCatalogo> accion) {
        frmCatalogo catalogo = Navegador.tienda("Catalogo");
        accion.accept(catalogo);
    }

    /** El menú del usuario está en Tienda.fxml; aquí solo se ponen sus datos. */
    private void armarMenuUsuario() {
        Usuario conectado = Sesion.getUsuario();
        btnUsuario.setText(conectado.getNombre());
        lblInicial.setText(conectado.getIniciales().substring(0, 1));
        lblIniciales.setText(conectado.getIniciales());
        lblNombreMenu.setText(conectado.getNombreCompleto());
        lblCorreoMenu.setText(conectado.getCorreo());
    }

    @FXML private void irPerfil() { Navegador.tienda("Perfil"); }
    @FXML private void irCompras() { Navegador.tienda("Compras"); }
    @FXML private void cerrarSesion() { Sesion.setCerrar(); Navegador.ir("Login"); }

    /** Coloca una pantalla en la zona central (debajo del aviso). */
    public void mostrar(Parent pantalla) {
        contenido.getChildren().removeIf(nodo -> nodo != lblAviso);
        contenido.getChildren().add(0, pantalla);
    }

    public void actualizarContadores() {
        int totalMeGusta = Sesion.getMeGusta().getLongPila();
        int totalCarrito = Sesion.getCarrito().getUnidades();
        lblMeGusta.setText(String.valueOf(totalMeGusta));
        lblMeGusta.setVisible(totalMeGusta > 0);
        lblCarrito.setText(String.valueOf(totalCarrito));
        lblCarrito.setVisible(totalCarrito > 0);
    }

    public void aviso(String texto) {
        lblAviso.setText(texto);
        lblAviso.setVisible(true);
        lblAviso.toFront();
        ocultarAviso.playFromStart();
    }

    @FXML private void buscar() {
        String texto = txtBuscar.getText().trim();
        abrirCatalogo(catalogo -> catalogo.buscar(texto));
    }
    @FXML private void irCatalogo() { Navegador.tienda("Catalogo"); }
    @FXML private void irMeGusta() { Navegador.tienda("MeGusta"); }
    @FXML private void irCarrito() { Navegador.tienda("Carrito"); }
}
