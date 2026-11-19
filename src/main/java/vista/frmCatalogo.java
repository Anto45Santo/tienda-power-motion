package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Comparator;

/**
 * 04 · Catálogo (lo primero que ve el cliente después del login).
 * Recorre el catálogo (lista doble circular), deja los productos que cumplen los filtros
 * en una ListaResultados ordenada y la pagina de 10 en 10.
 */
public class frmCatalogo {
    @FXML private HBox boxChips;
    @FXML private Button btnFiltroActivo, btnFiltros, btnCerrarFiltros, btnVerResultados;
    @FXML private Label lblTotal;
    @FXML private ComboBox<String> cmbOrden;
    @FXML private Pagination paginas;
    @FXML private StackPane capaFiltros;
    @FXML private VBox gruposFiltros;

    // Filtros rápidos y del menú Categorías
    private String filtroRapido = "Todo";
    private String filtroGenero, filtroCategoria, filtroSubcategoria, filtroMarca, textoBuscado = "";
    // Filtros del panel lateral (están en Catalogo.fxml): cada grupo es un FlowPane con ToggleButton
    @FXML private FlowPane grupoMarcas, grupoTipos, grupoTallas, grupoTallasCalzado, grupoColores;
    @FXML private TextField txtMinimo, txtMaximo;
    private final ToggleGroup grupoRapido = new ToggleGroup();

    @FXML
    private void initialize() {
        // Los filtros rápidos, el orden y el panel de filtros están en Catalogo.fxml
        for (Node nodo : boxChips.getChildren()) {
            ToggleButton chip = (ToggleButton) nodo;
            chip.setToggleGroup(grupoRapido);
            chip.setOnAction(evento -> {
                if (!chip.isSelected()) chip.setSelected(true);     // siempre queda uno elegido
                filtroRapido = chip.getText();
                filtroGenero = filtroCategoria = filtroSubcategoria = filtroMarca = null;
                textoBuscado = "";
                aplicar();
            });
        }
        cmbOrden.setOnAction(evento -> aplicar());
        for (FlowPane grupo : new FlowPane[]{grupoMarcas, grupoTipos, grupoTallas, grupoTallasCalzado, grupoColores})
            for (Node nodo : grupo.getChildren()) ((ToggleButton) nodo).setOnAction(evento -> contarResultados());
        txtMinimo.textProperty().addListener((propiedad, antes, ahora) -> contarResultados());
        txtMaximo.textProperty().addListener((propiedad, antes, ahora) -> contarResultados());
        aplicar();
        contarResultados();
    }

    // ------------------------------------------------------------ desde la barra superior
    public void filtrarCategoria(String genero, String categoria, String subcategoria) {
        filtroGenero = genero; filtroCategoria = categoria; filtroSubcategoria = subcategoria; filtroMarca = null; textoBuscado = "";
        String texto = null;
        if (genero != null || categoria != null)
            texto = (genero == null ? "" : genero + " › ") + (categoria == null ? "Todo" : categoria)
                    + (subcategoria == null ? "" : " › " + subcategoria);
        mostrarFiltroActivo(texto);
        aplicar();
    }

    public void filtrarMarca(String marca) {
        filtroGenero = filtroCategoria = filtroSubcategoria = null; filtroMarca = marca; textoBuscado = "";
        mostrarFiltroActivo("Marca: " + marca);
        aplicar();
    }

    public void buscar(String texto) {
        filtroGenero = filtroCategoria = filtroSubcategoria = filtroMarca = null;
        textoBuscado = texto.toLowerCase();
        mostrarFiltroActivo(texto.isEmpty() ? null : "Búsqueda: “" + texto + "”");
        aplicar();
    }

    private void mostrarFiltroActivo(String texto) {
        boolean hayFiltro = texto != null;
        btnFiltroActivo.setText(hayFiltro ? texto : "");
        btnFiltroActivo.setGraphic(hayFiltro ? Iconos.crear("close", 14, "#2583FF") : null);
        btnFiltroActivo.setVisible(hayFiltro);
        btnFiltroActivo.setManaged(hayFiltro);
        boxChips.setVisible(!hayFiltro);
        boxChips.setManaged(!hayFiltro);
    }

    @FXML
    private void quitarFiltroActivo() {
        filtroGenero = filtroCategoria = filtroSubcategoria = filtroMarca = null;
        textoBuscado = "";
        mostrarFiltroActivo(null);
        aplicar();
    }

    // ------------------------------------------------------------ filtrar y ordenar
    private boolean getCumple(Producto producto) {
        if (!producto.isActivo()) return false;
        switch (filtroRapido) {
            case "Calzado": case "Ropa": case "Accesorios":
                if (!producto.getCategoria().equals(filtroRapido)) return false;
                break;
            case "Hombre": case "Mujer":
                if (!producto.getGenero().equals(filtroRapido) && !producto.getGenero().equals("Unisex")) return false;
                break;
            case "Ofertas":
                if (producto.getDescuento() == 0) return false;
                break;
            default:
                break;
        }
        if (filtroGenero != null && !producto.getGenero().equals(filtroGenero) && !producto.getGenero().equals("Unisex")) return false;
        if (filtroCategoria != null && !producto.getCategoria().equals(filtroCategoria)) return false;
        if (filtroSubcategoria != null && !producto.getSubcategoria().equals(filtroSubcategoria)) return false;
        if (filtroMarca != null && !producto.getMarca().equals(filtroMarca)) return false;
        if (!textoBuscado.isEmpty()) {
            String datos = (producto.getNombre() + " " + producto.getMarca() + " " + producto.getSubcategoria()).toLowerCase();
            if (!datos.contains(textoBuscado)) return false;
        }
        // Panel lateral: dentro de cada grupo basta con que cumpla una de las opciones marcadas
        if (getHayMarcados(grupoMarcas) && !getEstaMarcado(grupoMarcas, producto.getMarca())) return false;
        if (getHayMarcados(grupoTipos) && !getEstaMarcado(grupoTipos, producto.getCategoria())) return false;
        if ((getHayMarcados(grupoTallas) || getHayMarcados(grupoTallasCalzado)) && !getTieneTallaMarcada(producto)) return false;
        if (getHayMarcados(grupoColores) && !getTieneColorMarcado(producto)) return false;
        long minimo = Formato.leerCop(txtMinimo.getText()), maximo = Formato.leerCop(txtMaximo.getText());
        if (minimo > 0 && producto.getPrecio() < minimo) return false;
        if (maximo > 0 && producto.getPrecio() > maximo) return false;
        return true;
    }

    private boolean getTieneTallaMarcada(Producto producto) {
        Variante actual = producto.getVariantes().cab;
        while (actual != null) {
            if (actual.getCantidad() > 0
                    && (getEstaMarcado(grupoTallas, actual.getTalla()) || getEstaMarcado(grupoTallasCalzado, actual.getTalla())))
                return true;
            actual = actual.sig;
        }
        return false;
    }

    private boolean getTieneColorMarcado(Producto producto) {
        NodoColor actual = producto.getColores().cab;
        while (actual != null) {
            if (getEstaMarcado(grupoColores, actual.nombre)) return true;
            actual = actual.sig;
        }
        return false;
    }

    private Comparator<Producto> getCriterio() {
        switch (cmbOrden.getValue()) {
            case "Más recientes": return Comparator.comparingInt(Producto::getId).reversed();
            case "Menor precio": return Comparator.comparingLong(Producto::getPrecio);
            case "Mayor precio": return Comparator.comparingLong(Producto::getPrecio).reversed();
            case "Mayor descuento": return Comparator.comparingInt(Producto::getDescuento).reversed();
            default: return null;                 // destacados: el orden del catálogo
        }
    }

    /** Recorre el catálogo (do-while por ser circular) y llena la lista de resultados. */
    private ListaResultados getResultados(Comparator<Producto> criterio) {
        ListaResultados resultados = new ListaResultados();
        if (Datos.catalogo.getEsVacia()) return resultados;
        Producto actual = Datos.catalogo.cab;
        do {
            if (getCumple(actual)) resultados.setAddOrdenado(actual, criterio);
            actual = actual.sig;
        } while (actual != Datos.catalogo.cab);
        return resultados;
    }

    private void aplicar() {
        ListaResultados resultados = getResultados(getCriterio());
        int cantidad = resultados.getLongLista();
        lblTotal.setText(cantidad + (cantidad == 1 ? " producto" : " productos"));
        Grilla.paginar(paginas, resultados, null);
        btnFiltros.getStyleClass().remove("activo");
        if (getFiltrosPanelActivos()) btnFiltros.getStyleClass().add("activo");
    }

    // ------------------------------------------------------------ panel de filtros
    private static boolean getHayMarcados(FlowPane grupo) {
        for (Node nodo : grupo.getChildren()) if (((ToggleButton) nodo).isSelected()) return true;
        return false;
    }

    private static boolean getEstaMarcado(FlowPane grupo, String valor) {
        for (Node nodo : grupo.getChildren()) {
            ToggleButton chip = (ToggleButton) nodo;
            if (chip.isSelected() && chip.getText().equals(valor)) return true;
        }
        return false;
    }

    private boolean getFiltrosPanelActivos() {
        return getHayMarcados(grupoMarcas) || getHayMarcados(grupoTipos) || getHayMarcados(grupoTallas)
                || getHayMarcados(grupoTallasCalzado) || getHayMarcados(grupoColores)
                || !txtMinimo.getText().isBlank() || !txtMaximo.getText().isBlank();
    }

    private void contarResultados() {
        int cantidad = getResultados(null).getLongLista();
        btnVerResultados.setText("Ver " + cantidad + (cantidad == 1 ? " resultado" : " resultados"));
    }

    @FXML private void abrirFiltros() { contarResultados(); capaFiltros.setVisible(true); }
    @FXML private void cerrarFiltros() { capaFiltros.setVisible(false); }
    @FXML private void aplicarFiltros() { aplicar(); cerrarFiltros(); }

    @FXML
    private void limpiarFiltros() {
        for (FlowPane grupo : new FlowPane[]{grupoMarcas, grupoTipos, grupoTallas, grupoTallasCalzado, grupoColores})
            for (Node nodo : grupo.getChildren()) ((ToggleButton) nodo).setSelected(false);
        txtMinimo.clear();
        txtMaximo.clear();
        contarResultados();
    }
}
