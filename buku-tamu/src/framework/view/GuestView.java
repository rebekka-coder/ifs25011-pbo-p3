package framework.view;

import adapter.presenter.GuestPresenter;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import static framework.util.InputUtil.CANCEL_INPUT;
import usecase.GuestUseCase;

/**
 * Tampilan konsol aplikasi buku tamu.
 */
public class GuestView {
    private final GuestUseCase useCase;
    private final GuestPresenter presenter;

    public GuestView(GuestUseCase useCase, GuestPresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showGuests(useCase.getAllGuests());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> registerGuest();
                case "2" -> searchGuest();
                case "3" -> removeGuest();
                case CANCEL_INPUT -> running = false;
                default -> presenter.showInvalidChoice();
            }

            if (running) {
                System.out.println();
            }
        }
    }

    /** Mencetak opsi menu ke layar. */
    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Daftarkan");
        System.out.println("2. Cari");
        System.out.println("3. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form pendaftaran tamu baru. */
    private void registerGuest() {
        System.out.println("[Mendaftarkan Tamu]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (name.equals(CANCEL_INPUT)) {
            return;
        }
        String purpose = InputUtil.input("Tujuan Kunjungan (x Jika Batal)");
        if (purpose.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showRegisterSuccess(useCase.registerGuest(name, purpose));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form pencarian data berdasarkan kata kunci. */
    private void searchGuest() {
        System.out.println("[Mencari Tamu]");
        String keyword = InputUtil.input("Nama (x Jika Batal)");
        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(useCase.searchGuests(keyword), keyword);
        }
    }

    /** Form hapus tamu berdasarkan ID; menampilkan pesan gagal jika ID salah atau tidak ditemukan. */
    private void removeGuest() {
        System.out.println("[Menghapus Tamu]");
        String strId = InputUtil.input("[ID Tamu] yang dihapus (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            useCase.removeGuest(strId);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

}
