package modelo;

/** Tipo de tallaje que elige el administrador al crear el producto. */
public enum TipoTalla {
    CALZADO("Calzado", "US 7 – 12", new String[]{"7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "12"}),
    TEXTIL("Textil", "XS – 3XL", new String[]{"XS", "S", "M", "L", "XL", "XXL", "3XL"}),
    UNICA("Talla única", "Accesorios", new String[]{"UNICA"});

    private final String nombre, rango;
    private final String[] tallas;

    TipoTalla(String nombre, String rango, String[] tallas) {
        this.nombre = nombre; this.rango = rango; this.tallas = tallas;
    }

    public String getNombre() { return nombre; }
    public String getRango() { return rango; }
    public String[] getTallas() { return tallas; }
}
