package framework.util;

import java.util.Scanner;

/**
 * Utility untuk membaca input string dari keyboard.
 * Berada di layer framework karena bergantung pada {@link System#in}.
 */
public class InputUtil {
    /** Input yang dipakai user untuk membatalkan operasi atau keluar dari aplikasi. */
    public static final String CANCEL_INPUT = "x";

    /** Scanner bersama untuk seluruh aplikasi (satu instance cukup). */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Menampilkan prompt dan membaca satu baris input dari user.
     * Jika input habis (EOF), dikembalikan {@link #CANCEL_INPUT} agar aplikasi keluar dengan rapi.
     *
     * @param info label yang ditampilkan sebelum input
     * @return teks yang diketik user
     */
    public static String input(String info) {
        System.out.print(info + " : ");
        if (!scanner.hasNextLine()) {
            System.out.println();
            return CANCEL_INPUT;
        }
        return scanner.nextLine();
    }
}
