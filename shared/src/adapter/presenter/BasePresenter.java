package adapter.presenter;

import java.util.List;

/**
 * Basis presenter generik: menampung output yang sama di semua studi kasus
 * (pencetakan daftar, pilihan menu tidak dikenali, pesan kegagalan validasi).
 * Subclass cukup menentukan format satu data lewat {@link #format(Object)}
 * dan menambahkan pesan khusus studi kasusnya.
 *
 * @param <T> tipe entitas yang ditampilkan
 */
public abstract class BasePresenter<T> {

    /** Memformat satu data menjadi baris teks untuk ditampilkan. */
    protected abstract String format(T item);

    /** Mencetak daftar dengan judul; menampilkan {@code emptyMessage} jika tidak ada data. */
    protected void printList(List<T> items, String header, String emptyMessage) {
        System.out.println(header);
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (T item : items) {
            System.out.println(format(item));
        }
    }

    /** Menampilkan pesan saat pilihan menu tidak dikenali. */
    public void showInvalidChoice() {
        System.out.println("[!] Pilihan tidak dimengerti.");
    }

    /** Menampilkan pesan kegagalan validasi dari domain. */
    public void showValidationError(String message) {
        System.out.println("[!] " + message);
    }
}
