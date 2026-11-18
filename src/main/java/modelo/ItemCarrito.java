package modelo;

/** Nodo de la lista doble del carrito y de los productos de cada pedido (ListaCarrito). */
public class ItemCarrito {
    private final Producto producto;
    private final String talla, color;
    private int cantidad;
    private long precioCompra;   // precio con el que se compró (0 = usar el precio actual del producto)
    public ItemCarrito sig, ant;

    public ItemCarrito(Producto producto, String talla, String color, int cantidad) {
        this.producto = producto; this.talla = talla; this.color = color; this.cantidad = cantidad;
        sig = ant = null;
    }

    /** Un nodo nuevo con los mismos datos (para pasar del carrito al pedido sin compartir enlaces). */
    public ItemCarrito getCopia() {
        ItemCarrito copia = new ItemCarrito(producto, talla, color, cantidad);
        copia.setPrecioCompra(producto.getPrecio());   // el pedido conserva el precio aunque luego cambie
        return copia;
    }

    public long getPrecioUnitario() { return precioCompra > 0 ? precioCompra : producto.getPrecio(); }
    public long getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(long precioCompra) { this.precioCompra = precioCompra; }

    public boolean getEsIgual(Producto otro, String otraTalla, String otroColor) {
        return producto.getId() == otro.getId() && talla.equals(otraTalla) && color.equals(otroColor);
    }

    public long getSubtotal() { return getPrecioUnitario() * cantidad; }
    public long getSubtotalAnterior() {
        long base = producto.getPrecioAnterior() > 0 ? producto.getPrecioAnterior() : producto.getPrecio();
        return base * cantidad;
    }
    public int getDisponibles() { return producto.getStockDe(talla, color); }
    public String getTallaTexto() { return talla.equals("UNICA") ? "Talla única" : "Talla " + talla; }
    public Producto getProducto() { return producto; }
    public String getTalla() { return talla; }
    public String getColor() { return color; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}

