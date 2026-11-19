package vista;

import controlador.*;
import modelo.*;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;

/** 10 · Me gusta. Recorre la pila circular desde el tope: lo último que marcó sale primero. */
public class frmMeGusta {
    @FXML private Hyperlink lnkVolver;
    @FXML private Label lblTotal;
    @FXML private Pagination paginas;
    @FXML private VBox boxVacio;

    @FXML
    private void initialize() {
        refrescar();
    }

    private void refrescar() {
        PilaMeGusta pila = Sesion.getMeGusta();
        ListaResultados guardados = new ListaResultados();
        if (!pila.getEsVacia()) {
            NodoMeGusta actual = pila.tope;
            do {
                Producto producto = Datos.catalogo.getBuscarxId(actual.idProducto);
                if (producto != null && producto.isActivo()) guardados.setAddF(producto);
                actual = actual.sig;
            } while (actual != pila.tope);
        }
        int cantidad = guardados.getLongLista();
        lblTotal.setText(cantidad + (cantidad == 1 ? " producto guardado" : " productos guardados"));
        boxVacio.setVisible(guardados.getEsVacia());
        paginas.setVisible(!guardados.getEsVacia());
        Grilla.paginar(paginas, guardados, this::refrescar);   // al quitar el corazón desaparece de la lista
    }

    @FXML private void volver() { Navegador.tienda("Catalogo"); }
}
