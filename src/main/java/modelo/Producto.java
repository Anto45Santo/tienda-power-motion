package modelo;

import controlador.ListaColores;
import controlador.ListaImagenes;
import controlador.ListaVariantes;
import controlador.PilaResenas;

/**
 * Nodo de la lista doblemente encadenada circular del catálogo (ListaProductos).
 * Cada producto tiene además sus propias estructuras: imágenes, colores, variantes y reseñas.
 */
public class Producto {
    private int id;
    private String nombre, marca, categoria, subcategoria, genero, descripcion;
    private TipoTalla tipoTalla;
    private long precio, precioAnterior;
    private boolean nuevo, activo = true;
    private int vendidos;
    private final ListaImagenes imagenes = new ListaImagenes();     // lista doble circular (galería)
    private final ListaColores colores = new ListaColores();        // lista doble
    private final ListaVariantes variantes = new ListaVariantes();  // lista doble (stock talla × color)
    private final PilaResenas resenas = new PilaResenas();          // pila circular (la más reciente arriba)
    public Producto sig, ant;

    public Producto(int id, String nombre, String marca, String categoria, String subcategoria, String genero,
                    TipoTalla tipoTalla, long precio, long precioAnterior, String descripcion) {
        this.id = id; this.nombre = nombre; this.marca = marca; this.categoria = categoria;
        this.subcategoria = subcategoria; this.genero = genero; this.tipoTalla = tipoTalla;
        this.precio = precio; this.precioAnterior = precioAnterior; this.descripcion = descripcion;
        sig = ant = null;
    }

    public int getDescuento() {
        if (precioAnterior <= precio) return 0;
        return (int) Math.round(100.0 * (precioAnterior - precio) / precioAnterior);
    }

    public int getStockDe(String talla, String color) {
        Variante encontrada = variantes.getBuscar(talla, color);
        return encontrada == null ? 0 : encontrada.getCantidad();
    }

    public int getStockTotal() { return variantes.getTotalUnidades(); }
    public boolean getTallaActiva(String talla) { return variantes.getTieneTalla(talla); }
    public double getPromedio() { return resenas.getPromedioVisibles(); }
    public int getTotalResenas() { return resenas.getContarVisibles(); }
    public String getPortada() { return imagenes.getEsVacia() ? null : imagenes.cab.archivo; }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getSubcategoria() { return subcategoria; }
    public void setSubcategoria(String subcategoria) { this.subcategoria = subcategoria; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public TipoTalla getTipoTalla() { return tipoTalla; }
    public void setTipoTalla(TipoTalla tipoTalla) { this.tipoTalla = tipoTalla; }
    public long getPrecio() { return precio; }
    public void setPrecio(long precio) { this.precio = precio; }
    public long getPrecioAnterior() { return precioAnterior; }
    public void setPrecioAnterior(long precioAnterior) { this.precioAnterior = precioAnterior; }
    public boolean isNuevo() { return nuevo; }
    public void setNuevo(boolean nuevo) { this.nuevo = nuevo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getVendidos() { return vendidos; }
    public void setVendidos(int vendidos) { this.vendidos = vendidos; }
    public ListaImagenes getImagenes() { return imagenes; }
    public ListaColores getColores() { return colores; }
    public ListaVariantes getVariantes() { return variantes; }
    public PilaResenas getResenas() { return resenas; }
}
