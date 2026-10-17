package adapter.presenter;

import domain.entity.Guest;
import java.util.List;

/**
 * Presenter yang memformat data tamu menjadi output layar.
 */
public class GuestPresenter extends BasePresenter<Guest> {

    @Override
    protected String format(Guest guest) {
        return String.format("[%d] %s | Tujuan: %s", guest.getId(), guest.getName(), guest.getPurpose());
    }

    /** Menampilkan seluruh data yang tersimpan. */
    public void showGuests(List<Guest> guests) {
        printList(guests, "Daftar Tamu:", "- Data tamu belum tersedia!");
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Guest> guests, String keyword) {
        printList(guests, "Hasil Pencarian: \"" + keyword + "\"", "- Tamu tidak ditemukan!");
    }

    /** Menampilkan pesan sukses setelah data ditambahkan. */
    public void showRegisterSuccess(Guest guest) {
        System.out.printf("Berhasil mendaftarkan tamu: %s%n", format(guest));
    }

    /** Menampilkan pesan sukses setelah data dihapus. */
    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus tamu.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat dihapus. */
    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus tamu dengan ID: %d.%n", id);
    }
}
