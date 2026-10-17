package usecase;

import domain.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Helper parsing dan validasi input mentah (String) dari user, dipakai bersama oleh semua use case.
 * Tidak melakukan I/O; setiap kegagalan dilaporkan lewat {@link ValidationException}
 * yang pesannya siap ditampilkan oleh View.
 */
public final class InputValidator {
    private static final String INVALID_ID_MESSAGE = "ID tidak valid!";

    private InputValidator() {
    }

    /** Mengonversi teks menjadi ID (bilangan bulat); melempar {@link ValidationException} jika bukan angka. */
    public static int parseId(String raw) throws ValidationException {
        return parseInt(raw, INVALID_ID_MESSAGE);
    }

    /** Mengonversi teks menjadi {@code int}; melempar {@link ValidationException} dengan {@code message} jika gagal. */
    public static int parseInt(String raw, String message) throws ValidationException {
        try {
            return Integer.parseInt(requireNonNull(raw, message).trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(message);
        }
    }

    /** Mengonversi teks menjadi {@code long}; melempar {@link ValidationException} dengan {@code message} jika gagal. */
    public static long parseLong(String raw, String message) throws ValidationException {
        try {
            return Long.parseLong(requireNonNull(raw, message).trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(message);
        }
    }

    /**
     * Memastikan teks tidak null/kosong.
     *
     * @return teks yang sudah dirapikan (spasi di awal/akhir dibuang)
     * @throws ValidationException dengan {@code message} jika teks null atau hanya berisi spasi
     */
    public static String requireText(String value, String message) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(message);
        }
        return value.trim();
    }

    /**
     * Memastikan teks tidak kosong dan cocok seluruhnya dengan {@code pattern}.
     *
     * @return teks yang sudah dirapikan (spasi di awal/akhir dibuang)
     * @throws ValidationException dengan {@code message} jika teks kosong atau formatnya tidak cocok
     */
    public static String requireFormat(String value, Pattern pattern, String message) throws ValidationException {
        String text = requireText(value, message);
        if (!pattern.matcher(text).matches()) {
            throw new ValidationException(message);
        }
        return text;
    }

    /**
     * Merapikan teks untuk update parsial: teks kosong/null menjadi {@code null}
     * (artinya field tidak diubah), selain itu dikembalikan tanpa spasi di awal/akhir.
     */
    public static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /** Memastikan input mentah tidak null; null diperlakukan sebagai input tidak valid. */
    private static String requireNonNull(String raw, String message) throws ValidationException {
        if (raw == null) {
            throw new ValidationException(message);
        }
        return raw;
    }
}
