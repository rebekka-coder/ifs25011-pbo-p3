package adapter.presenter;

import domain.entity.Item;
import java.util.List;

/**
 * Presenter yang memformat data barang menjadi output layar.
 */
public class ItemPresenter extends BasePresenter<Item> {

    @Override
    protected String format(Item item) {
        return String.format("[%d] %s | Stok: %d | Kategori: %s",
                item.getId(), item.getName(), item.getQuantity(), item.getCategory());
    }

    /** Menampilkan seluruh data yang tersimpan. */
    public void showItems(List<Item> items) {
        printList(items, "Daftar Barang:", "- Data barang belum tersedia!");
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Item> items, String keyword) {
        printList(items, "Hasil Pencarian: \"" + keyword + "\"", "- Barang tidak ditemukan!");
    }

    /** Menampilkan daftar data yang sudah diurutkan. */
    public void showSortedItems(List<Item> items) {
        printList(items, "Daftar Barang (Terurut):", "- Data barang belum tersedia!");
    }

    /** Menampilkan pesan sukses setelah data ditambahkan. */
    public void showAddSuccess(Item item) {
        System.out.printf("Berhasil menambah barang: %s%n", format(item));
    }

    /** Menampilkan pesan sukses setelah data diubah. */
    public void showUpdateSuccess() {
        System.out.println("Berhasil mengubah stok barang.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat diubah. */
    public void showUpdateFailed(int id) {
        System.out.printf("[!] Gagal mengubah stok barang dengan ID: %d.%n", id);
    }

    /** Menampilkan pesan sukses setelah data dihapus. */
    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus barang.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat dihapus. */
    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus barang dengan ID: %d.%n", id);
    }
}
