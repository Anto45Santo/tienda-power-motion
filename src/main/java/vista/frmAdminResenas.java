package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class frmAdminResenas {
    @FXML private HBox boxPestanas;
    @FXML private VBox lista;
    private final ToggleGroup grupo = new ToggleGroup();
    private String pestana = "Todas";

    @FXML
    private void initialize() {
        // Las pestañas están en AdminResenas.fxml: el texto de cada una es el filtro
        for (javafx.scene.Node nodo : boxPestanas.getChildren()) {
            ToggleButton chip = (ToggleButton) nodo;
            chip.setToggleGroup(grupo);
            chip.setOnAction(evento -> { if (!chip.isSelected()) chip.setSelected(true); pestana = chip.getText(); refrescar(); });
        }
        refrescar();
    }

    /** Recorre el catálogo (lista doble circular) y, dentro de cada producto, su pila circular de reseñas. */
    private void refrescar() {
        lista.getChildren().clear();
        if (!Datos.catalogo.getEsVacia()) {
            Producto producto = Datos.catalogo.cab;
            do {
                if (!producto.getResenas().getEsVacia()) {
                    Resena resena = producto.getResenas().tope;
                    do {
                        boolean mostrar = pestana.equals("Todas") || (pestana.equals("Visibles") == resena.isVisible());
                        if (mostrar) lista.getChildren().add(fila(resena));
                        resena = resena.sig;
                    } while (resena != producto.getResenas().tope);
                }
                producto = producto.sig;
            } while (producto != Datos.catalogo.cab);
        }
        if (lista.getChildren().isEmpty()) lista.getChildren().add(Celdas.texto("  No hay reseñas en esta pestaña.", "gris"));
        Navegador.actualizarBarra();
    }

    private HBox fila(Resena resena) {
        Label autor = Celdas.texto(resena.getAutor(), "negrita");
        Label nombreProducto = Celdas.texto("· " + resena.getProducto().getNombre(), "gris");
        Label texto = new Label(resena.getTexto());
        texto.setWrapText(true);
        VBox info = new VBox(4, Celdas.fila(6, autor, nombreProducto),
                Celdas.fila(8, Ui.estrellas(resena.getEstrellas(), 13), Celdas.texto(resena.getFecha(), "gris-claro")), texto);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label estado = resena.isVisible() ? Ui.pill("● Visible", "estado-aprobado") : Ui.pill("● Oculta", "estado-neutro");
        Button alternar = new Button(resena.isVisible() ? "Ocultar" : "Mostrar", Iconos.crear("eye", 16, "#0B1220"));
        alternar.getStyleClass().add("btn-borde");
        alternar.setOnAction(evento -> { resena.setVisible(!resena.isVisible()); Datos.setGuardarResenas(); refrescar(); });
        Button eliminar = new Button(null, Iconos.crear("trash", 16, "#E11D48"));
        eliminar.getStyleClass().addAll("btn-accion", "btn-rechazar");
        eliminar.setOnAction(evento -> {
            if (Dialogos.confirmar("Eliminar reseña", "¿Eliminar la reseña de " + resena.getAutor() + "?")) {
                resena.getProducto().getResenas().setDelxNodo(resena);
                Datos.setGuardarResenas();                                // resenas.txt
                refrescar();
            }
        });
        HBox filaResena = new HBox(Ui.foto(resena.getProducto().getPortada(), 48, 48), info, estado, alternar, eliminar);
        filaResena.getStyleClass().add("fila-resena");
        return filaResena;
    }
}
