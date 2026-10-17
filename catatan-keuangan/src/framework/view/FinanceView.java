package framework.view;

import adapter.presenter.FinancePresenter;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import framework.util.InputUtil;
import static framework.util.InputUtil.CANCEL_INPUT;
import usecase.FinanceUseCase;

/**
 * Tampilan konsol aplikasi catatan keuangan.
 * Menerima input user, memanggil use case, dan menangani exception domain.
 */
public class FinanceView {
    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;
    }

    /** Menampilkan menu utama dan loop interaksi user. */
    public void show() {
        boolean running = true;
        while (running) {
            presenter.showTransactions(useCase.getAllTransactions(), useCase.getBalance());
            printMenu();

            String input = InputUtil.input("Pilih");
            System.out.println();
            switch (input) {
                case "1" -> addTransaction(TransactionType.INCOME);
                case "2" -> addTransaction(TransactionType.EXPENSE);
                case "3" -> searchTransaction();
                case "4" -> sortTransaction();
                case "5" -> presenter.showBalance(useCase.getBalance());
                case "6" -> removeTransaction();
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
        System.out.println("1. Tambah Pemasukan");
        System.out.println("2. Tambah Pengeluaran");
        System.out.println("3. Cari");
        System.out.println("4. Urutkan");
        System.out.println("5. Lihat Saldo");
        System.out.println("6. Hapus");
        System.out.println("x. Keluar");
    }

    /** Form tambah pemasukan/pengeluaran. */
    private void addTransaction(TransactionType type) {
        System.out.println(type == TransactionType.INCOME ? "[Tambah Pemasukan]" : "[Tambah Pengeluaran]");
        String description = InputUtil.input("Keterangan (x Jika Batal)");
        if (description.equals(CANCEL_INPUT)) {
            return;
        }
        String strAmount = InputUtil.input("Jumlah");

        try {
            presenter.showAddSuccess(useCase.addTransaction(description, strAmount, type));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }

    /** Form hapus transaksi berdasarkan ID; menampilkan pesan gagal jika ID salah atau tidak ditemukan. */
    private void removeTransaction() {
        System.out.println("[Hapus Transaksi]");
        String strId = InputUtil.input("ID Transaksi (x Jika Batal)");
        if (strId.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            useCase.removeTransaction(strId);
            presenter.showRemoveSuccess();
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        } catch (EntityNotFoundException e) {
            presenter.showRemoveFailed(e.getId());
        }
    }

    /** Form cari transaksi berdasarkan keterangan. */
    private void searchTransaction() {
        System.out.println("[Cari Transaksi]");
        String keyword = InputUtil.input("Kata Kunci (x Jika Batal)");
        if (!keyword.equals(CANCEL_INPUT)) {
            presenter.showSearchResults(useCase.searchTransactions(keyword), keyword);
        }
    }

    /** Form urutkan transaksi: memilih kriteria lalu menampilkan hasilnya. */
    private void sortTransaction() {
        System.out.println("[Urutkan Transaksi]");
        System.out.println("1. Jumlah (Terkecil)");
        System.out.println("2. Jumlah (Terbesar)");
        System.out.println("3. Pemasukan Dulu");
        System.out.println("4. Pengeluaran Dulu");
        System.out.println("x. Batal");

        String input = InputUtil.input("Pilih");
        System.out.println();
        if (input.equals(CANCEL_INPUT)) {
            return;
        }

        try {
            presenter.showSortedTransactions(useCase.sortTransactions(input));
        } catch (ValidationException e) {
            presenter.showValidationError(e.getMessage());
        }
    }
}
