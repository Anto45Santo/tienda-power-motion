package vista;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Function;

/** Ayudas para las tablas (TableView) y tarjetas del administrador. */
public final class Celdas {
    private Celdas() {}

    /**
     * Configura una columna que ya está en el FXML (título y ancho vienen del FXML):
     * cada celda muestra el nodo que construye la fábrica a partir de la fila.
     */
    public static <S> void llenar(TableColumn<S, S> columna, Function<S, Node> fabrica) {
        columna.setCellValueFactory(fila -> new SimpleObjectProperty<>(fila.getValue()));
        columna.setCellFactory(columnaActual -> new TableCell<S, S>() {
            @Override
            protected void updateItem(S dato, boolean vacia) {
                super.updateItem(dato, vacia);
                setText(null);
                setGraphic(vacia || dato == null ? null : fabrica.apply(dato));
            }
        });
    }

    public static Label texto(String contenido, String estilo) {
        Label etiqueta = new Label(contenido);
        if (estilo != null) etiqueta.getStyleClass().add(estilo);
        return etiqueta;
    }

    public static VBox dosLineas(String arriba, String abajo) {
        Label superior = texto(arriba, "negrita"), inferior = texto(abajo, "gris-claro");
        inferior.setStyle("-fx-font-size: 11px;");
        return new VBox(1, superior, inferior);
    }

    public static HBox fila(double separacion, Node... nodos) {
        HBox fila = new HBox(separacion, nodos);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

}
