package usecase;

import java.util.Locale;

/**
 * Helper pencocokan teks untuk fitur pencarian, dipakai bersama oleh semua use case
 * agar aturan pencarian (tidak peka huruf besar/kecil) hanya ditulis satu kali.
 */
public final class TextMatcher {
    private TextMatcher() {
    }

    /**
     * Memeriksa apakah {@code text} mengandung {@code keyword}, tanpa membedakan huruf besar/kecil.
     * Memakai {@link Locale#ROOT} agar hasilnya sama di semua pengaturan bahasa komputer.
     *
     * @return {@code true} jika cocok; {@code false} jika salah satu argumen null
     */
    public static boolean containsIgnoreCase(String text, String keyword) {
        if (text == null || keyword == null) {
            return false;
        }
        return text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }
}
