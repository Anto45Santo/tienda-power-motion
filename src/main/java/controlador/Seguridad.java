package controlador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Las contraseñas no se guardan en texto plano: se guarda su resumen SHA-256. */
public final class Seguridad {
    private Seguridad() {}

    public static String getHash(String clave) {
        try {
            MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");
            byte[] resumen = algoritmo.digest(clave.getBytes(StandardCharsets.UTF_8));
            StringBuilder texto = new StringBuilder();
            for (byte valor : resumen) texto.append(String.format("%02x", valor));
            return texto.toString();
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 no disponible", error);
        }
    }

    /** true si la clave escrita corresponde al resumen guardado. */
    public static boolean getCoincide(String claveEscrita, String hashGuardado) {
        return getHash(claveEscrita).equals(hashGuardado);
    }
}
