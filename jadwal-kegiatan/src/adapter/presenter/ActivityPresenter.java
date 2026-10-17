package adapter.presenter;

import domain.entity.Activity;
import java.util.List;

/**
 * Presenter yang memformat data kegiatan menjadi output layar.
 */
public class ActivityPresenter extends BasePresenter<Activity> {

    @Override
    protected String format(Activity a) {
        return String.format("[%d] %s | Hari: %s | Pukul: %s", a.getId(), a.getTitle(), a.getDay(), a.getTime());
    }

    /** Menampilkan seluruh data yang tersimpan. */
    public void showActivities(List<Activity> list) {
        printList(list, "Daftar Kegiatan:", "- Data kegiatan belum tersedia!");
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Activity> list, String keyword) {
        printList(list, "Hasil Pencarian: \"" + keyword + "\"", "- Kegiatan tidak ditemukan!");
    }

    /** Menampilkan daftar data yang sudah diurutkan. */
    public void showSortedActivities(List<Activity> list) {
        printList(list, "Daftar Kegiatan (Terurut):", "- Data kegiatan belum tersedia!");
    }

    /** Menampilkan pesan sukses setelah data ditambahkan. */
    public void showAddSuccess(Activity a) {
        System.out.printf("Berhasil menambah kegiatan: %s%n", format(a));
    }

    /** Menampilkan pesan sukses setelah data diubah. */
    public void showUpdateSuccess() {
        System.out.println("Berhasil mengubah kegiatan.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat diubah. */
    public void showUpdateFailed(int id) {
        System.out.printf("[!] Gagal mengubah kegiatan dengan ID: %d.%n", id);
    }

    /** Menampilkan pesan sukses setelah data dihapus. */
    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus kegiatan.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat dihapus. */
    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus kegiatan dengan ID: %d.%n", id);
    }
}
