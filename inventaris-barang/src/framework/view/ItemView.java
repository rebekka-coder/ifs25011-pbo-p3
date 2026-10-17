package framework.view;

import adapter.presenter.ItemPresenter;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import static framework.util.InputUtil.CANCEL_INPUT;
import usecase.ItemUseCase;

/**
 * Tampilan konsol aplikasi inventaris barang.
 */
public class ItemView {
    private final ItemUseCase useCase;
    private final ItemPresenter presenter;

    public ItemView(ItemUseCase useCase, ItemPresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showItems(useCase.getAllItems());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addItem();
                case "2" -> updateStock();
                case "3" -> searchItem();
                case "4" -> sortItem();
                case "5" -> removeItem();
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
        System.out.println("2. Ubah Stok");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah barang. Jumlah stok divalidasi sebelum kategori ditanyakan. */
    private void addItem() {
        System.out.println("[Menambah Barang]");
        String name = InputUtil.input("Nama (x Jika Batal)");
        if (name.equals(CANCEL_INPUT)) {
            return;
        }

        // Jumlah divalidasi lebih dulu (fail-fast) sebelum kategori ditanyakan.
        String quantity = InputUtil.input("Jumlah");
        try {
            useCase.validateQuantity(quantity);
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
            return;
        }

        String category = InputUtil.input("Kategori (x Jika Batal)");
        if (category.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showAddSuccess(useCase.addItem(name, quantity, category));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form ubah stok barang: jumlah kosong berarti stok tidak diubah. */
    private void updateStock() {
        System.out.println("[Mengubah Stok]");
        String strId = InputUtil.input("ID Barang yang diubah (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        // ID divalidasi lebih dulu (fail-fast) sebelum jumlah baru ditanyakan.
        try {
            useCase.validateId(strId);
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
            return;
        }

        String strQuantity = InputUtil.input("Jumlah Baru (Kosongkan jika tidak ingin mengubah)");

        try {
            useCase.updateStock(strId, strQuantity);
            presenter.showUpdateSuccess();
        } catch (EntityNotFoundException e) {
            presenter.showUpdateFailed(e.getId());
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form pencarian data berdasarkan kata kunci. */
    private void searchItem() {
        System.out.println("[Mencari Barang]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(useCase.searchItems(keyword), keyword);
        }
    }

    /** Form pengurutan barang: memilih kriteria lalu menampilkan hasilnya. */
    private void sortItem() {
        System.out.println("[Mengurutkan Barang]");
        System.out.println("Pilihan Pengurutan:");
        System.out.println("1. Nama (A-Z)");
        System.out.println("2. Nama (Z-A)");
        System.out.println("3. Jumlah (Terkecil -> Terbesar)");
        System.out.println("4. Jumlah (Terbesar -> Terkecil)");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showSortedItems(useCase.sortItems(input));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus barang berdasarkan ID; menampilkan pesan gagal jika ID salah atau tidak ditemukan. */
    private void removeItem() {
        System.out.println("[Menghapus Barang]");
        String strId = InputUtil.input("[ID Barang] yang dihapus (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            useCase.removeItem(strId);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }
}
