package vista;

import controlador.*;
import modelo.*;

import javafx.geometry.Insets;
import javafx.scene.control.Pagination;
import javafx.scene.layout.TilePane;

/**
 * Paginación del catálogo: 10 tarjetas por página (5 × 2).
 * Cada página empieza en el nodo de su posición y avanza con "sig" por la lista doble circular.
 */
public final class Grilla {
    public static final int POR_PAGINA = 10;
    private Grilla() {}

    public static void paginar(Pagination paginacion, ListaResultados resultados, Runnable alCambiarMeGusta) {
        int totalPaginas = Math.max(1, (int) Math.ceil(resultados.getLongLista() / (double) POR_PAGINA));
        paginacion.setPageCount(totalPaginas);
        paginacion.setCurrentPageIndex(0);
        paginacion.setPageFactory(numeroPagina -> {
            TilePane cuadricula = new TilePane(12, 12);
            cuadricula.setPrefColumns(5);
            cuadricula.setPadding(new Insets(0, 0, 4, 0));
            if (resultados.getEsVacia()) return cuadricula;
            int desde = numeroPagina * POR_PAGINA;
            int hasta = Math.min(desde + POR_PAGINA, resultados.getLongLista());
            NodoResultado actual = resultados.getNodoEn(desde);
            for (int posicion = desde; posicion < hasta; posicion++) {
                cuadricula.getChildren().add(new TarjetaProducto(actual.producto, alCambiarMeGusta));
                actual = actual.sig;
            }
            return cuadricula;
        });
    }
}
