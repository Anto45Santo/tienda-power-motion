package modelo;

import controlador.ListaCarrito;

/** Nodo de la cola doblemente encadenada circular de pedidos (ColaPedidos). */
public class Pedido {
    private final int numero;
    private final Usuario cliente;
    private final String fecha;
    private EstadoPedido estado;
    private String motivoRechazo = "";
    private String comprobante = "";                       // nombre del archivo en la carpeta comprobantes/
    private String nombreEnvio = "", telefonoEnvio = "", direccionEnvio = "", ciudad = "", departamento = "";
    private final ListaCarrito items = new ListaCarrito();   // lista doble con los productos comprados
    public Pedido sig, ant;

    public Pedido(int numero, Usuario cliente, String fecha, EstadoPedido estado) {
        this.numero = numero; this.cliente = cliente; this.fecha = fecha; this.estado = estado;
        sig = ant = null;
    }

    public long getTotal() { return items.getTotal(); }
    public int getUnidades() { return items.getUnidades(); }
    public boolean getEnProceso() {
        return estado == EstadoPedido.POR_APROBAR || estado == EstadoPedido.APROBADO || estado == EstadoPedido.ENVIADO;
    }

    public int getNumero() { return numero; }
    public Usuario getCliente() { return cliente; }
    public String getFecha() { return fecha; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public ListaCarrito getItems() { return items; }

    public String getComprobante() { return comprobante; }
    public void setComprobante(String comprobante) { this.comprobante = comprobante; }
    public String getNombreEnvio() { return nombreEnvio; }
    public String getTelefonoEnvio() { return telefonoEnvio; }
    public String getDireccionEnvio() { return direccionEnvio; }
    public String getCiudad() { return ciudad; }
    public String getDepartamento() { return departamento; }

    /** Datos de entrega que el cliente escribe al finalizar la compra. */
    public void setEnvio(String nombreEnvio, String telefonoEnvio, String direccionEnvio, String ciudad, String departamento) {
        this.nombreEnvio = nombreEnvio; this.telefonoEnvio = telefonoEnvio; this.direccionEnvio = direccionEnvio;
        this.ciudad = ciudad; this.departamento = departamento;
    }

    public String getDireccionCompleta() {
        return direccionEnvio.isEmpty() ? "—" : direccionEnvio + ", " + ciudad + " (" + departamento + ")";
    }
}
