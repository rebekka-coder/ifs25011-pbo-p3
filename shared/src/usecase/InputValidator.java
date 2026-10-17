package usecase;

import domain.exception.ValidationException;

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

    /** Memastikan input mentah tidak null; null diperlakukan sebagai input tidak valid. */
    private static String requireNonNull(String raw, String message) throws ValidationException {
        if (raw == null) {
            throw new ValidationException(message);
        }
        return raw;
    }

    /** Memastikan teks tidak null/kosong; selain itu melempar {@link ValidationException}. */
    public static void requireText(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /** Mengubah teks kosong/null menjadi {@code null}; {@code null} berarti field tidak diubah pada update parsial. */
    public static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
