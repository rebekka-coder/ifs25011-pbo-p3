package adapter.presenter;

import domain.entity.Contact;
import java.util.List;

/**
 * Presenter yang memformat data kontak menjadi output layar.
 */
public class ContactPresenter extends BasePresenter<Contact> {

    @Override
    protected String format(Contact c) {
        return String.format("[%d] %s | Telp: %s | Email: %s", c.getId(), c.getName(), c.getPhone(), c.getEmail());
    }

    /** Menampilkan seluruh data yang tersimpan. */
    public void showContacts(List<Contact> list) {
        printList(list, "Daftar Kontak:", "- Data kontak belum tersedia!");
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Contact> list, String keyword) {
        printList(list, "Hasil Pencarian: \"" + keyword + "\"", "- Kontak tidak ditemukan!");
    }

    /** Menampilkan daftar data yang sudah diurutkan. */
    public void showSortedContacts(List<Contact> list) {
        printList(list, "Daftar Kontak (Terurut):", "- Data kontak belum tersedia!");
    }

    /** Menampilkan pesan sukses setelah data ditambahkan. */
    public void showAddSuccess(Contact c) {
        System.out.printf("Berhasil menambah kontak: %s%n", format(c));
    }

    /** Menampilkan pesan sukses setelah data diubah. */
    public void showUpdateSuccess() {
        System.out.println("Berhasil mengubah kontak.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat diubah. */
    public void showUpdateFailed(int id) {
        System.out.printf("[!] Gagal mengubah kontak dengan ID: %d.%n", id);
    }

    /** Menampilkan pesan sukses setelah data dihapus. */
    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus kontak.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat dihapus. */
    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus kontak dengan ID: %d.%n", id);
    }
}
