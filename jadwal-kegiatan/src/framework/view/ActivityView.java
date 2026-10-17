package framework.view;

import adapter.presenter.ActivityPresenter;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import static framework.util.InputUtil.CANCEL_INPUT;
import usecase.ActivityUseCase;

/**
 * Tampilan konsol aplikasi jadwal kegiatan.
 */
public class ActivityView {
    private final ActivityUseCase useCase;
    private final ActivityPresenter presenter;

    public ActivityView(ActivityUseCase useCase, ActivityPresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showActivities(useCase.getAllActivities());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addActivity();
                case "2" -> updateActivity();
                case "3" -> searchActivity();
                case "4" -> sortActivity();
                case "5" -> removeActivity();
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
        System.out.println("1. Tambah");
        System.out.println("2. Ubah");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah kegiatan baru. */
    private void addActivity() {
        System.out.println("[Menambah Kegiatan]");
        String title = InputUtil.input("Judul (x Jika Batal)");
        if (title.equals(CANCEL_INPUT)) {
            return;
        }
        String day = InputUtil.input("Hari (x Jika Batal)");
        if (day.equals(CANCEL_INPUT)) {
            return;
        }
        String time = InputUtil.input("Waktu (x Jika Batal)");
        if (time.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showAddSuccess(useCase.addActivity(title, day, time));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form ubah kegiatan: input kosong berarti field tidak diubah. */
    private void updateActivity() {
        System.out.println("[Mengubah Kegiatan]");
        String strId = InputUtil.input("ID Kegiatan yang diubah (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        // ID divalidasi lebih dulu (fail-fast) sebelum field baru ditanyakan.
        try {
            useCase.validateId(strId);
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
            return;
        }

        String newTitle = InputUtil.input("Judul Baru (Kosongkan jika tidak ingin mengubah)");
        String newDay = InputUtil.input("Hari Baru (Kosongkan jika tidak ingin mengubah)");
        String newTime = InputUtil.input("Waktu Baru (Kosongkan jika tidak ingin mengubah)");

        try {
            useCase.updateActivity(strId, newTitle, newDay, newTime);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form pencarian data berdasarkan kata kunci. */
    private void searchActivity() {
        System.out.println("[Mencari Kegiatan]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(useCase.searchActivities(keyword), keyword);
        }
    }

    /** Form pengurutan kegiatan: memilih kriteria lalu menampilkan hasilnya. */
    private void sortActivity() {
        System.out.println("[Mengurutkan Kegiatan]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Hari (Senin -> Minggu)");
        System.out.println("2. Waktu (Awal -> Akhir)");
        System.out.println("3. Judul (A-Z)");
        System.out.println("4. Judul (Z-A)");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showSortedActivities(useCase.sortActivities(input));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus kegiatan berdasarkan ID; menampilkan pesan gagal jika ID salah atau tidak ditemukan. */
    private void removeActivity() {
        System.out.println("[Menghapus Kegiatan]");
        String strId = InputUtil.input("[ID Kegiatan] yang dihapus (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            useCase.removeActivity(strId);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}
