package controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Fechas reales del sistema en el formato que se guarda en los archivos. */
public final class Fechas {
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DIA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-CO"));

    private Fechas() {}

    /** "08/11/2026" */
    public static String getHoy() { return LocalDate.now().format(DIA); }

    /** "08/11/2026 16:40" (fecha de un pedido) */
    public static String getAhora() { return LocalDateTime.now().format(DIA_HORA); }

    /** "noviembre 2026" (cliente desde…) */
    public static String getMesActual() { return LocalDate.now().format(MES); }

    /** Fecha de hoy con una hora dada (para los datos de ejemplo). */
    public static String getHoyA(String hora) { return getHoy() + " " + hora; }
}
