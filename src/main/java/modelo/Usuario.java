package modelo;

/** Nodo de la lista doble de usuarios (ListaUsuarios). */
public class Usuario {
    private final int id;
    private String nombre, apellido, correo, telefono, direccion, clave;
    private final String desde;
    private final boolean admin;
    public Usuario sig, ant;

    public Usuario(int id, String nombre, String apellido, String correo, String telefono,
                   String direccion, String clave, boolean admin, String desde) {
        this.id = id; this.nombre = nombre; this.apellido = apellido; this.correo = correo; this.telefono = telefono;
        this.direccion = direccion; this.clave = clave; this.admin = admin; this.desde = desde;
        sig = ant = null;
    }

    public String getNombreCompleto() { return nombre + " " + apellido; }
    public String getNombreCorto() { return nombre + " " + apellido.charAt(0) + "."; }
    public String getIniciales() { return ("" + nombre.charAt(0) + apellido.charAt(0)).toUpperCase(); }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    /** Resumen SHA-256 de la contraseña (nunca la contraseña en texto plano). */
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public boolean isAdmin() { return admin; }
    public String getDesde() { return desde; }
}
