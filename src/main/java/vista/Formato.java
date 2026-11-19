package vista;

public final class Formato {
    private Formato() {}

    /** 389900 → "$ 389.900" */
    public static String cop(long valor) {
        String conPuntos = String.format(java.util.Locale.US, "%,d", valor).replace(',', '.');
        return "$ " + conPuntos;
    }

    /** "$ 389.900" o "389900" → 389900 (devuelve -1 si no es válido) */
    public static long leerCop(String texto) {
        String digitos = texto.replaceAll("[^0-9]", "");
        return digitos.isEmpty() ? -1 : Long.parseLong(digitos);
    }
}