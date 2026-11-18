package modelo;

public enum EstadoPedido {
    POR_APROBAR("Por aprobar", "estado-pendiente"),
    APROBADO("Aprobado", "estado-aprobado"),
    ENVIADO("Enviado", "estado-enviado"),
    ENTREGADO("Entregado", "estado-aprobado"),
    RECHAZADO("Rechazado", "estado-rechazado");

    private final String texto, estilo;
    EstadoPedido(String texto, String estilo) { this.texto = texto; this.estilo = estilo; }
    public String getTexto() { return texto; }
    public String getEstilo() { return estilo; }
}
