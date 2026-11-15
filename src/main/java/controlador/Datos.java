package controlador;

import modelo.*;

import java.io.IOException;

/**
 * Estructuras de la tienda y su almacenamiento en archivos.
 * Al abrir la aplicación se leen los .txt de la carpeta datos/ (ArchivoTexto). Si todavía no existen
 * (primera vez que se ejecuta), se crean los datos de ejemplo y se guardan en los archivos.
 * Cada vez que algo cambia, la vista llama al método setGuardar... que corresponde.
 */
public final class Datos {
    public static final ListaProductos catalogo = new ListaProductos();       // lista doble circular
    public static final ListaUsuarios usuarios = new ListaUsuarios();         // lista doble
    public static final ColaPedidos pedidos = new ColaPedidos();              // cola doble circular
    public static final ListaImagenes fotosBienvenida = new ListaImagenes();  // lista doble circular

    private Datos() {}

    /** Último error al leer o escribir un archivo (lo muestra la vista). */
    public static String error = "";

    public static void setCargar() {
        if (!catalogo.getEsVacia()) return;
        if (ArchivoTexto.getExistenDatos()) {
            try {
                ArchivoTexto.cargarUsuarios(usuarios);
                ArchivoTexto.cargarProductos(catalogo);
                ArchivoTexto.cargarResenas(catalogo);
                ArchivoTexto.cargarPedidos(pedidos, usuarios, catalogo);
                ArchivoTexto.cargarFotos(fotosBienvenida);
                return;
            } catch (IOException | RuntimeException problema) {
                error = "No se pudieron leer los archivos: " + problema.getMessage();
                System.err.println(error);
                setVaciarTodo();
                return;
            }
        }
        setCrearEjemplo();
        setGuardarTodo();
    }

    /** Copia el comprobante de ejemplo del proyecto a la carpeta comprobantes/ (solo la primera vez). */
    private static String setComprobanteEjemplo(int numero) {
        String nombre = "PM" + numero + "_ejemplo.png";
        try (java.io.InputStream origen = Datos.class.getResourceAsStream("/img/comprobante_ejemplo.png")) {
            java.io.File carpeta = new java.io.File(ArchivoTexto.CARPETA_COMPROBANTES);
            if (!carpeta.exists()) carpeta.mkdirs();
            if (origen != null) java.nio.file.Files.copy(origen, new java.io.File(carpeta, nombre).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return nombre;
        } catch (IOException problema) {
            return "";
        }
    }

    private static void setVaciarTodo() {
        catalogo.cab = null;
        usuarios.cab = usuarios.ult = null;
        pedidos.inicio = null;
        fotosBienvenida.setVaciar();
    }

    // ----------------------------------------------------------------- guardar (true si se pudo)

    public static boolean setGuardarUsuarios() {
        try { ArchivoTexto.setGuardarUsuarios(usuarios); return true; }
        catch (IOException problema) { return setFallo(problema); }
    }

    /** productos.txt + stock.txt */
    public static boolean setGuardarProductos() {
        try { ArchivoTexto.setGuardarProductos(catalogo); return true; }
        catch (IOException problema) { return setFallo(problema); }
    }

    public static boolean setGuardarResenas() {
        try { ArchivoTexto.setGuardarResenas(catalogo); return true; }
        catch (IOException problema) { return setFallo(problema); }
    }

    /** pedidos.txt + detalle_pedidos.txt */
    public static boolean setGuardarPedidos() {
        try { ArchivoTexto.setGuardarPedidos(pedidos); return true; }
        catch (IOException problema) { return setFallo(problema); }
    }

    public static boolean setGuardarFotos() {
        try { ArchivoTexto.setGuardarFotos(fotosBienvenida); return true; }
        catch (IOException problema) { return setFallo(problema); }
    }

    public static boolean setGuardarTodo() {
        return setGuardarUsuarios() & setGuardarProductos() & setGuardarResenas() & setGuardarPedidos() & setGuardarFotos();
    }

    private static boolean setFallo(IOException problema) {
        error = "No se pudo guardar: " + problema.getMessage();
        System.err.println(error);
        return false;
    }

    // ----------------------------------------------------------------- datos de ejemplo (solo la primera vez)

    private static void setCrearEjemplo() {

        fotosBienvenida.setAddF("hero_runner.jpg");
        fotosBienvenida.setAddF("women_drink.jpg");
        fotosBienvenida.setAddF("yoga_beach.jpg");

        Producto tenis = calzado(1, "Tenis Running Ultra Glide 3 Hombre", "Adidas", "Running", "Hombre", 389900, 559900,
                "Tenis livianos para correr, con mediasuela de doble densidad que devuelve energía y capellada en malla técnica transpirable.",
                new String[]{"run_blue.png", "run_blue_b.png", "run_black.png", "run_red.png"});
        tenis.setVendidos(340);
        resena(tenis, "Laura M.", 5, "02/11/2026", "Los uso para correr 10K y la amortiguación es increíble. La talla es exacta.");
        resena(tenis, "Carlos R.", 4, "28/10/2026", "Muy livianos y con buena respuesta en asfalto. Recomendados.");
        resena(tenis, "Daniela P.", 5, "20/10/2026", "El color es aún más bonito en persona. Llegaron bien empacados.");

        textil(2, "Camiseta Dry-Fit Performance Azul", "Fila", "Camisetas", "Hombre", 89900, 129900,
                "Tela dry-fit que absorbe el sudor y seca rápido.", new String[]{"tee_blue.png", "tee_black.png"});
        textil(3, "Chaqueta Fleece Training Azul", "Reebok", "Chaquetas", "Hombre", 249900, 349900,
                "Chaqueta térmica liviana para entrenar en clima frío.", new String[]{"jacket_blue.png", "jacket_black.png"});
        textil(4, "Pantaloneta Go Run 2 en 1", "Skechers", "Pantalonetas", "Hombre", 119900, 169900,
                "Pantaloneta con licra interna y bolsillo trasero.", new String[]{"shorts_blue.png", "shorts_black.png"});
        textil(5, "Licra Deportiva Tiro Alto Mujer", "Fila", "Licras", "Mujer", 139900, 0,
                "Licra de compresión con tiro alto que no se transparenta.", new String[]{"leggings_blue.png", "leggings_black.png"}).setNuevo(true);
        calzado(6, "Tenis Training Flex Pro Negro", "Nike", "Training", "Hombre", 429900, 0,
                "Tenis estables para entrenamiento funcional.", new String[]{"run_black.png", "run_blue.png"}).setNuevo(true);
        textil(7, "Buzo con Capucha Dry Training", "Puma", "Buzos", "Hombre", 189900, 249900,
                "Buzo liviano con capucha para calentar antes de entrenar.", new String[]{"hoodie_teal.png"});
        textil(8, "Top Deportivo Impacto Alto", "Adidas", "Tops", "Mujer", 149900, 0,
                "Top con soporte alto para running y saltos.", new String[]{"bra_blue.png"});
        calzado(9, "Tenis Slip-On Flow Mujer", "Asics", "Casual", "Mujer", 299900, 0,
                "Tenis sin cordones, cómodos para el día a día.", new String[]{"slip_blue.png", "slip_white.png"}).setNuevo(true);
        unica(10, "Maletín Deportivo Duffel 40 L", "Nike", "Maletines", 179900, 239900,
                "Maletín con ruedas y bolsillo separado para tenis.", "duffel.png", new String[]{"Negro", "Gris"}, new int[]{22, 0});
        unica(11, "Termo Deportivo Tritán 1 L", "Power Motion", "Termos", 69900, 0,
                "Termo libre de BPA con tapa antiderrame.", "bottle.png", new String[]{"Azul", "Negro"}, new int[]{30, 12});
        unica(12, "Cuerda para Saltar Speed Pro", "Adidas", "Training", 49900, 69900,
                "Cuerda con rodamientos y mangos de espuma.", "rope.png", new String[]{"Negro"}, new int[]{40});
        textil(13, "Camiseta Básica Training Negra", "Puma", "Camisetas", "Hombre", 69900, 0,
                "Camiseta de algodón técnico.", new String[]{"tee_black.png"});
        textil(14, "Camiseta Mujer Dry Azul", "Fila", "Camisetas", "Mujer", 79900, 99900,
                "Camiseta de corte entallado para correr.", new String[]{"wtee_blue.png"});
        textil(15, "Chaqueta Mujer Rompevientos", "Reebok", "Chaquetas", "Mujer", 229900, 0,
                "Rompevientos impermeable y liviano.", new String[]{"wjacket_blue.png"});
        textil(16, "Esqueleto Running Mujer", "Nike", "Tops", "Mujer", 89900, 0,
                "Esqueleto fresco con espalda deportiva.", new String[]{"wtank_blue.png"});
        textil(17, "Esqueleto Basketball Hombre", "Adidas", "Basketball", "Hombre", 99900, 129900,
                "Esqueleto de malla para baloncesto.", new String[]{"tank_blue.png"});
        unica(18, "Rodillo de Espuma Recovery", "Power Motion", "Recuperación", 89900, 0,
                "Rodillo para masaje y estiramiento.", "roller.png", new String[]{"Azul"}, new int[]{15});

        Usuario andres = new Usuario(1, "Andrés", "Gómez", "andres.gomez@correo.com", "300 123 4567",
                "Cra. 15 # 93 - 47, Apto 502, Bogotá", Seguridad.getHash("12345678"), false, "marzo 2025");
        usuarios.setAddF(andres);
        usuarios.setAddF(new Usuario(2, "José", "Lozano", "admin@powermotion.co", "321 435 5407", "", Seguridad.getHash("admin123"), true, "enero 2025"));
        String[][] otros = {{"Laura", "Martínez", "laura.m@correo.com", "311 555 0192", "junio 2025"},
                {"Carlos", "Rodríguez", "carlos.r@correo.com", "315 220 8841", "agosto 2025"},
                {"Daniela", "Pérez", "dani.perez@correo.com", "320 778 1123", "septiembre 2025"},
                {"Mateo", "Gómez", "mateo.g@correo.com", "301 908 3321", "octubre 2025"},
                {"Sofía", "Herrera", "sofia.h@correo.com", "317 664 0090", "febrero 2026"}};
        int siguienteId = 3;
        for (String[] datos : otros)
            usuarios.setAddF(new Usuario(siguienteId++, datos[0], datos[1], datos[2], datos[3], "", Seguridad.getHash("12345678"), false, datos[4]));

        // Se encolan en orden de llegada: el más antiguo queda en el inicio de la cola
        pedido(10215, andres, "11/09/2026 10:20", EstadoPedido.ENTREGADO, 5, "M", "Azul", 11, "UNICA", "Azul");
        pedido(10391, andres, "02/10/2026 18:45", EstadoPedido.ENTREGADO, 6, "9", "Negro");
        pedido(10477, andres, "22/10/2026 09:12", EstadoPedido.RECHAZADO, 8, "M", "Azul").setMotivoRechazo("Comprobante ilegible");
        pedido(10482, andres, "24/10/2026 14:30", EstadoPedido.ENVIADO, 1, "9", "Azul", 2, "M", "Azul", 10, "UNICA", "Negro");
        pedido(10487, usuario(7), "25/10/2026 11:58", EstadoPedido.APROBADO, 2, "L", "Azul");
        pedido(10489, usuario(5), Fechas.getHoyA("11:05"), EstadoPedido.POR_APROBAR, 5, "S", "Azul", 8, "M", "Azul");
        pedido(10490, usuario(4), Fechas.getHoyA("15:12"), EstadoPedido.POR_APROBAR, 6, "10", "Negro");
        pedido(10491, usuario(3), Fechas.getHoyA("16:40"), EstadoPedido.POR_APROBAR, 1, "9", "Azul", 4, "M", "Azul");
    }

    // ----------------------------------------------------------------- ayudas
    private static Producto base(int id, String nombre, String marca, String categoria, String subcategoria, String genero,
                                 TipoTalla tipo, long precio, long precioAntes, String descripcion, String[] imagenes) {
        Producto nuevo = new Producto(id, nombre, marca, categoria, subcategoria, genero, tipo, precio, precioAntes, descripcion);
        for (String imagen : imagenes) nuevo.getImagenes().setAddF(imagen);
        catalogo.setAddF(nuevo);
        return nuevo;
    }

    /** Registra los colores y el stock (tallas × colores) de un producto. */
    private static void stock(Producto producto, String[] colores, String[] tallas, int[][] cantidades) {
        for (int fila = 0; fila < colores.length; fila++) {
            producto.getColores().setAddF(colores[fila]);
            for (int columna = 0; columna < tallas.length; columna++)
                producto.getVariantes().setAddF(new Variante(tallas[columna], colores[fila], cantidades[fila][columna]));
        }
    }

    private static Producto calzado(int id, String nombre, String marca, String subcategoria, String genero,
                                    long precio, long precioAntes, String descripcion, String[] imagenes) {
        Producto nuevo = base(id, nombre, marca, "Calzado", subcategoria, genero, TipoTalla.CALZADO, precio, precioAntes, descripcion, imagenes);
        stock(nuevo, new String[]{"Azul", "Negro", "Blanco"}, new String[]{"7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11"},
                new int[][]{{4, 6, 8, 6, 3, 2, 2, 1}, {2, 5, 0, 4, 1, 0, 3, 2}, {6, 3, 2, 7, 5, 4, 1, 0}});
        return nuevo;
    }

    private static Producto textil(int id, String nombre, String marca, String subcategoria, String genero,
                                   long precio, long precioAntes, String descripcion, String[] imagenes) {
        Producto nuevo = base(id, nombre, marca, "Ropa", subcategoria, genero, TipoTalla.TEXTIL, precio, precioAntes, descripcion, imagenes);
        stock(nuevo, new String[]{"Azul", "Negro", "Blanco"}, new String[]{"S", "M", "L", "XL", "XXL"},
                new int[][]{{12, 18, 15, 9, 4}, {10, 14, 0, 6, 2}, {8, 11, 9, 0, 3}});
        return nuevo;
    }

    private static Producto unica(int id, String nombre, String marca, String subcategoria, long precio, long precioAntes,
                                  String descripcion, String imagen, String[] colores, int[] cantidades) {
        Producto nuevo = base(id, nombre, marca, "Accesorios", subcategoria, "Unisex", TipoTalla.UNICA, precio, precioAntes,
                descripcion, new String[]{imagen});
        int[][] porColor = new int[colores.length][1];
        for (int fila = 0; fila < colores.length; fila++) porColor[fila][0] = cantidades[fila];
        stock(nuevo, colores, new String[]{"UNICA"}, porColor);
        return nuevo;
    }

    private static void resena(Producto producto, String autor, int estrellas, String fecha, String texto) {
        producto.getResenas().setAddBase(new Resena(producto, autor, estrellas, fecha, texto));  // de la más reciente a la más antigua
    }

    public static Producto producto(int id) { return catalogo.getBuscarxId(id); }
    public static Usuario usuario(int id) { return usuarios.getBuscarxId(id); }

    /** Pedido de ejemplo: los datos van de a tres (idProducto, talla, color). */
    private static Pedido pedido(int numero, Usuario cliente, String fecha, EstadoPedido estado, Object... productos) {
        Pedido nuevo = new Pedido(numero, cliente, fecha, estado);
        for (int posicion = 0; posicion < productos.length; posicion += 3) {
            Producto comprado = producto((Integer) productos[posicion]);
            ItemCarrito item = new ItemCarrito(comprado, (String) productos[posicion + 1], (String) productos[posicion + 2], 1);
            item.setPrecioCompra(comprado.getPrecio());
            nuevo.getItems().setAddF(item);
        }
        nuevo.setComprobante(estado == EstadoPedido.RECHAZADO ? "" : setComprobanteEjemplo(numero));
        nuevo.setEnvio(cliente.getNombreCompleto(), cliente.getTelefono(),
                cliente.getDireccion().isBlank() ? "Calle 45 # 12 - 30" : cliente.getDireccion(), "Bogotá", "Cundinamarca");
        pedidos.setEncolar(nuevo);
        return nuevo;
    }
}
