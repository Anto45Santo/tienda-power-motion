package modelo;

import controlador.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Manejo de ficheros de la tienda (Tema 5): File, FileReader + BufferedReader para leer línea a línea
 * y FileWriter + PrintWriter para escribir. Cada línea es un registro con los campos separados por ";".
 *
 * Para no dañar un archivo si la aplicación se cierra mientras escribe, primero se escribe un archivo
 * temporal y luego se borra el original y se renombra el temporal (como en el ejemplo del docente).
 *
 * Archivos (carpeta datos/):
 *   usuarios.txt          id;nombre;apellido;correo;telefono;direccion;hashClave;rol;desde
 *   productos.txt         id;nombre;marca;categoria;subcategoria;genero;tipoTalla;precio;precioAnterior;
 *                         descripcion;nuevo;activo;vendidos;imagenes(separadas por ,);colores(separados por ,)
 *   stock.txt             idProducto;talla;color;cantidad
 *   resenas.txt           idProducto;autor;estrellas;fecha;visible;texto
 *   pedidos.txt           numero;idUsuario;fecha;estado;motivoRechazo;comprobante;nombre;telefono;direccion;ciudad;departamento
 *   detalle_pedidos.txt   numero;idProducto;talla;color;cantidad;precioCompra
 *   megusta.txt           idUsuario;idProducto
 *   carrito.txt           idUsuario;idProducto;talla;color;cantidad
 *   fotos.txt             archivo
 * Imágenes: imagenes/productos/, imagenes/banners/ y comprobantes/
 */
public final class ArchivoTexto {
    public static final String CARPETA_DATOS = "datos";
    public static final String CARPETA_IMAGENES = "imagenes";
    public static final String CARPETA_COMPROBANTES = "comprobantes";
    private static final String SEPARADOR = ";";

    private ArchivoTexto() {}

    // ================================================================ utilidades de ficheros

    private static File getArchivo(String nombre) {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists()) carpeta.mkdirs();
        return new File(carpeta, nombre);
    }

    /** Quita los caracteres que dañarían el formato del archivo (el separador y los saltos de línea). */
    public static String getLimpio(String texto) {
        if (texto == null) return "";
        return texto.replace(SEPARADOR, ",").replace("\r", " ").replace("\n", " ").trim();
    }

    /** Abre un PrintWriter sobre un archivo temporal (FileWriter en modo "desde cero"). */
    private static PrintWriter getEscritor(File temporal) throws IOException {
        return new PrintWriter(new FileWriter(temporal, StandardCharsets.UTF_8, false));
    }

    /** Borra el original y renombra el temporal con el nombre del original. */
    private static void setReemplazar(File temporal, File original) throws IOException {
        if (original.exists() && !original.delete()) throw new IOException("No se pudo actualizar " + original.getName());
        if (!temporal.renameTo(original)) throw new IOException("No se pudo renombrar " + temporal.getName());
    }

    private static File getTemporal(File original) {
        return new File(original.getParentFile(), original.getName() + ".tmp");
    }

    private static BufferedReader getLector(File archivo) throws IOException {
        return new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8));
    }

    /** true si ya se guardaron datos alguna vez (la primera vez se crean los datos de ejemplo). */
    public static boolean getExistenDatos() {
        return getArchivo("usuarios.txt").exists() && getArchivo("productos.txt").exists();
    }

    // ================================================================ usuarios

    public static void setGuardarUsuarios(ListaUsuarios usuarios) throws IOException {
        File original = getArchivo("usuarios.txt"), temporal = getTemporal(original);
        try (PrintWriter escritor = getEscritor(temporal)) {
            Usuario actual = usuarios.cab;
            while (actual != null) {
                escritor.println(actual.getId() + SEPARADOR + getLimpio(actual.getNombre()) + SEPARADOR
                        + getLimpio(actual.getApellido()) + SEPARADOR + getLimpio(actual.getCorreo()) + SEPARADOR
                        + getLimpio(actual.getTelefono()) + SEPARADOR + getLimpio(actual.getDireccion()) + SEPARADOR
                        + actual.getClave() + SEPARADOR + (actual.isAdmin() ? "ADMIN" : "CLIENTE") + SEPARADOR
                        + getLimpio(actual.getDesde()));
                actual = actual.sig;
            }
        }
        setReemplazar(temporal, original);
    }

    public static void cargarUsuarios(ListaUsuarios usuarios) throws IOException {
        File archivo = getArchivo("usuarios.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR, -1);
                if (campos.length < 9) continue;                 // línea dañada: se ignora
                usuarios.setAddF(new Usuario(Integer.parseInt(campos[0]), campos[1], campos[2], campos[3], campos[4],
                        campos[5], campos[6], campos[7].equals("ADMIN"), campos[8]));
            }
        }
    }

    // ================================================================ productos y stock

    /** Escribe productos.txt y stock.txt recorriendo el catálogo (lista doble circular). */
    public static void setGuardarProductos(ListaProductos catalogo) throws IOException {
        File original = getArchivo("productos.txt"), temporal = getTemporal(original);
        File originalStock = getArchivo("stock.txt"), temporalStock = getTemporal(originalStock);
        try (PrintWriter escritor = getEscritor(temporal); PrintWriter escritorStock = getEscritor(temporalStock)) {
            if (!catalogo.getEsVacia()) {
                Producto producto = catalogo.cab;
                do {
                    escritor.println(producto.getId() + SEPARADOR + getLimpio(producto.getNombre()) + SEPARADOR
                            + getLimpio(producto.getMarca()) + SEPARADOR + getLimpio(producto.getCategoria()) + SEPARADOR
                            + getLimpio(producto.getSubcategoria()) + SEPARADOR + getLimpio(producto.getGenero()) + SEPARADOR
                            + producto.getTipoTalla().name() + SEPARADOR + producto.getPrecio() + SEPARADOR
                            + producto.getPrecioAnterior() + SEPARADOR + getLimpio(producto.getDescripcion()) + SEPARADOR
                            + producto.isNuevo() + SEPARADOR + producto.isActivo() + SEPARADOR + producto.getVendidos() + SEPARADOR
                            + getTextoImagenes(producto.getImagenes()) + SEPARADOR + getTextoColores(producto.getColores()));
                    Variante variante = producto.getVariantes().cab;
                    while (variante != null) {
                        escritorStock.println(producto.getId() + SEPARADOR + variante.getTalla() + SEPARADOR
                                + getLimpio(variante.getColor()) + SEPARADOR + variante.getCantidad());
                        variante = variante.sig;
                    }
                    producto = producto.sig;
                } while (producto != catalogo.cab);
            }
        }
        setReemplazar(temporal, original);
        setReemplazar(temporalStock, originalStock);
    }

    private static String getTextoImagenes(ListaImagenes imagenes) {
        StringBuilder texto = new StringBuilder();
        if (imagenes.getEsVacia()) return "";
        NodoImagen actual = imagenes.cab;
        do {
            if (texto.length() > 0) texto.append(",");
            texto.append(getLimpio(actual.archivo).replace(",", ""));
            actual = actual.sig;
        } while (actual != imagenes.cab);
        return texto.toString();
    }

    private static String getTextoColores(ListaColores colores) {
        StringBuilder texto = new StringBuilder();
        NodoColor actual = colores.cab;
        while (actual != null) {
            if (texto.length() > 0) texto.append(",");
            texto.append(getLimpio(actual.nombre).replace(",", ""));
            actual = actual.sig;
        }
        return texto.toString();
    }

    public static void cargarProductos(ListaProductos catalogo) throws IOException {
        File archivo = getArchivo("productos.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR, -1);
                if (campos.length < 15) continue;
                Producto producto = new Producto(Integer.parseInt(campos[0]), campos[1], campos[2], campos[3], campos[4], campos[5],
                        TipoTalla.valueOf(campos[6]), Long.parseLong(campos[7]), Long.parseLong(campos[8]), campos[9]);
                producto.setNuevo(Boolean.parseBoolean(campos[10]));
                producto.setActivo(Boolean.parseBoolean(campos[11]));
                producto.setVendidos(Integer.parseInt(campos[12]));
                for (String imagen : campos[13].split(",")) if (!imagen.isBlank()) producto.getImagenes().setAddF(imagen.trim());
                for (String color : campos[14].split(",")) if (!color.isBlank()) producto.getColores().setAddF(color.trim());
                catalogo.setAddF(producto);
            }
        }
        File archivoStock = getArchivo("stock.txt");
        if (!archivoStock.exists()) return;
        try (BufferedReader lector = getLector(archivoStock)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR, -1);
                if (campos.length < 4) continue;
                Producto producto = catalogo.getBuscarxId(Integer.parseInt(campos[0]));
                if (producto != null) producto.getVariantes().setAddF(new Variante(campos[1], campos[2], Integer.parseInt(campos[3])));
            }
        }
    }

    // ================================================================ reseñas

    /** Recorre cada pila de reseñas desde el tope hasta la base. */
    public static void setGuardarResenas(ListaProductos catalogo) throws IOException {
        File original = getArchivo("resenas.txt"), temporal = getTemporal(original);
        try (PrintWriter escritor = getEscritor(temporal)) {
            if (!catalogo.getEsVacia()) {
                Producto producto = catalogo.cab;
                do {
                    PilaResenas resenas = producto.getResenas();
                    if (!resenas.getEsVacia()) {
                        Resena resena = resenas.tope;
                        do {
                            escritor.println(producto.getId() + SEPARADOR + getLimpio(resena.getAutor()) + SEPARADOR
                                    + resena.getEstrellas() + SEPARADOR + getLimpio(resena.getFecha()) + SEPARADOR
                                    + resena.isVisible() + SEPARADOR + getLimpio(resena.getTexto()));
                            resena = resena.sig;
                        } while (resena != resenas.tope);
                    }
                    producto = producto.sig;
                } while (producto != catalogo.cab);
            }
        }
        setReemplazar(temporal, original);
    }

    /** Las reseñas se leen en el mismo orden en que se guardaron (tope → base), por eso se agregan a la base. */
    public static void cargarResenas(ListaProductos catalogo) throws IOException {
        File archivo = getArchivo("resenas.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR, 6);
                if (campos.length < 6) continue;
                Producto producto = catalogo.getBuscarxId(Integer.parseInt(campos[0]));
                if (producto == null) continue;
                Resena resena = new Resena(producto, campos[1], Integer.parseInt(campos[2]), campos[3], campos[5]);
                resena.setVisible(Boolean.parseBoolean(campos[4]));
                producto.getResenas().setAddBase(resena);
            }
        }
    }

    // ================================================================ pedidos

    /** Escribe pedidos.txt y detalle_pedidos.txt recorriendo la cola desde el inicio (el más antiguo primero). */
    public static void setGuardarPedidos(ColaPedidos pedidos) throws IOException {
        File original = getArchivo("pedidos.txt"), temporal = getTemporal(original);
        File originalDetalle = getArchivo("detalle_pedidos.txt"), temporalDetalle = getTemporal(originalDetalle);
        try (PrintWriter escritor = getEscritor(temporal); PrintWriter escritorDetalle = getEscritor(temporalDetalle)) {
            if (!pedidos.getEsVacia()) {
                Pedido pedido = pedidos.inicio;
                do {
                    escritor.println(pedido.getNumero() + SEPARADOR + pedido.getCliente().getId() + SEPARADOR
                            + pedido.getFecha() + SEPARADOR + pedido.getEstado().name() + SEPARADOR
                            + getLimpio(pedido.getMotivoRechazo()) + SEPARADOR + getLimpio(pedido.getComprobante()) + SEPARADOR
                            + getLimpio(pedido.getNombreEnvio()) + SEPARADOR + getLimpio(pedido.getTelefonoEnvio()) + SEPARADOR
                            + getLimpio(pedido.getDireccionEnvio()) + SEPARADOR + getLimpio(pedido.getCiudad()) + SEPARADOR
                            + getLimpio(pedido.getDepartamento()));
                    ItemCarrito item = pedido.getItems().cab;
                    while (item != null) {
                        escritorDetalle.println(pedido.getNumero() + SEPARADOR + item.getProducto().getId() + SEPARADOR
                                + item.getTalla() + SEPARADOR + getLimpio(item.getColor()) + SEPARADOR + item.getCantidad()
                                + SEPARADOR + item.getPrecioUnitario());
                        item = item.sig;
                    }
                    pedido = pedido.sig;
                } while (pedido != pedidos.inicio);
            }
        }
        setReemplazar(temporal, original);
        setReemplazar(temporalDetalle, originalDetalle);
    }

    public static void cargarPedidos(ColaPedidos pedidos, ListaUsuarios usuarios, ListaProductos catalogo) throws IOException {
        File archivo = getArchivo("pedidos.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR, -1);
                if (campos.length < 11) continue;
                Usuario cliente = usuarios.getBuscarxId(Integer.parseInt(campos[1]));
                if (cliente == null) continue;
                Pedido pedido = new Pedido(Integer.parseInt(campos[0]), cliente, campos[2], EstadoPedido.valueOf(campos[3]));
                pedido.setMotivoRechazo(campos[4]);
                pedido.setComprobante(campos[5]);
                pedido.setEnvio(campos[6], campos[7], campos[8], campos[9], campos[10]);
                pedidos.setEncolar(pedido);
            }
        }
        File archivoDetalle = getArchivo("detalle_pedidos.txt");
        if (!archivoDetalle.exists()) return;
        try (BufferedReader lector = getLector(archivoDetalle)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEPARADOR, -1);
                if (campos.length < 6) continue;
                Pedido pedido = pedidos.getBuscarxNumero(Integer.parseInt(campos[0]));
                Producto producto = catalogo.getBuscarxId(Integer.parseInt(campos[1]));
                if (pedido == null || producto == null) continue;
                ItemCarrito item = new ItemCarrito(producto, campos[2], campos[3], Integer.parseInt(campos[4]));
                item.setPrecioCompra(Long.parseLong(campos[5]));
                pedido.getItems().setAddF(item);
            }
        }
    }

    // ================================================================ fotos de la bienvenida

    public static void setGuardarFotos(ListaImagenes fotos) throws IOException {
        File original = getArchivo("fotos.txt"), temporal = getTemporal(original);
        try (PrintWriter escritor = getEscritor(temporal)) {
            if (!fotos.getEsVacia()) {
                NodoImagen actual = fotos.cab;
                do {
                    escritor.println(getLimpio(actual.archivo));
                    actual = actual.sig;
                } while (actual != fotos.cab);
            }
        }
        setReemplazar(temporal, original);
    }

    public static void cargarFotos(ListaImagenes fotos) throws IOException {
        File archivo = getArchivo("fotos.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) if (!linea.isBlank()) fotos.setAddF(linea.trim());
        }
    }

    // ================================================================ me gusta (un archivo para todos los usuarios)

    /** Reescribe megusta.txt: conserva las líneas de los demás usuarios y pone las de este usuario. */
    public static void setGuardarMeGusta(int idUsuario, PilaMeGusta meGusta) throws IOException {
        File original = getArchivo("megusta.txt"), temporal = getTemporal(original);
        try (PrintWriter escritor = getEscritor(temporal)) {
            if (original.exists()) {
                try (BufferedReader lector = getLector(original)) {
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        if (linea.isBlank()) continue;
                        if (!linea.split(SEPARADOR)[0].equals(String.valueOf(idUsuario))) escritor.println(linea);
                    }
                }
            }
            if (!meGusta.getEsVacia()) {
                NodoMeGusta actual = meGusta.tope;               // del tope a la base
                do {
                    escritor.println(idUsuario + SEPARADOR + actual.idProducto);
                    actual = actual.sig;
                } while (actual != meGusta.tope);
            }
        }
        setReemplazar(temporal, original);
    }

    public static void cargarMeGusta(int idUsuario, PilaMeGusta meGusta) throws IOException {
        File archivo = getArchivo("megusta.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] campos = linea.split(SEPARADOR);
                if (campos.length == 2 && campos[0].equals(String.valueOf(idUsuario)))
                    meGusta.setAddBase(Integer.parseInt(campos[1]));
            }
        }
    }

    /** Total de "me gusta" de todos los usuarios (número de líneas de megusta.txt). */
    public static int getContarMeGusta() {
        File archivo = getArchivo("megusta.txt");
        if (!archivo.exists()) return 0;
        int contador = 0;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) if (!linea.isBlank()) contador++;
        } catch (IOException error) {
            return 0;
        }
        return contador;
    }

    // ================================================================ carrito (se conserva al cerrar la app)

    public static void setGuardarCarrito(int idUsuario, ListaCarrito carrito) throws IOException {
        File original = getArchivo("carrito.txt"), temporal = getTemporal(original);
        try (PrintWriter escritor = getEscritor(temporal)) {
            if (original.exists()) {
                try (BufferedReader lector = getLector(original)) {
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        if (linea.isBlank()) continue;
                        if (!linea.split(SEPARADOR)[0].equals(String.valueOf(idUsuario))) escritor.println(linea);
                    }
                }
            }
            ItemCarrito actual = carrito.cab;
            while (actual != null) {
                escritor.println(idUsuario + SEPARADOR + actual.getProducto().getId() + SEPARADOR + actual.getTalla()
                        + SEPARADOR + getLimpio(actual.getColor()) + SEPARADOR + actual.getCantidad());
                actual = actual.sig;
            }
        }
        setReemplazar(temporal, original);
    }

    public static void cargarCarrito(int idUsuario, ListaCarrito carrito, ListaProductos catalogo) throws IOException {
        File archivo = getArchivo("carrito.txt");
        if (!archivo.exists()) return;
        try (BufferedReader lector = getLector(archivo)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] campos = linea.split(SEPARADOR, -1);
                if (campos.length < 5 || !campos[0].equals(String.valueOf(idUsuario))) continue;
                Producto producto = catalogo.getBuscarxId(Integer.parseInt(campos[1]));
                if (producto == null || !producto.isActivo()) continue;
                int disponibles = producto.getStockDe(campos[2], campos[3]);
                int cantidad = Math.min(Integer.parseInt(campos[4]), disponibles);   // el stock pudo cambiar
                if (cantidad > 0) carrito.setAddF(new ItemCarrito(producto, campos[2], campos[3], cantidad));
            }
        }
    }

    // ================================================================ imágenes y comprobantes

    /**
     * Copia una imagen elegida por el usuario a la carpeta indicada con un nombre único
     * y devuelve ese nombre (es lo que se guarda en los .txt).
     */
    public static String setCopiarImagen(File origen, String carpeta, String prefijo) throws IOException {
        File destinoCarpeta = new File(carpeta);
        if (!destinoCarpeta.exists()) destinoCarpeta.mkdirs();
        String nombre = origen.getName();
        String extension = nombre.contains(".") ? nombre.substring(nombre.lastIndexOf('.')).toLowerCase() : ".png";
        String nuevoNombre = prefijo + "_" + System.currentTimeMillis() + extension;
        Files.copy(origen.toPath(), new File(destinoCarpeta, nuevoNombre).toPath(), StandardCopyOption.REPLACE_EXISTING);
        return nuevoNombre;
    }

    /** Borra una imagen copiada por la aplicación (las de ejemplo del proyecto no se tocan). */
    public static void setBorrarImagen(String carpeta, String nombre) {
        if (nombre == null || nombre.isBlank()) return;
        File archivo = new File(carpeta, nombre);
        if (archivo.exists()) archivo.delete();
    }
}
