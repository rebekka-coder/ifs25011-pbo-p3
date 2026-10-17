package adapter.presenter;

import domain.entity.Transaction;
import domain.entity.TransactionType;
import java.util.List;

/**
 * Presenter yang memformat data dari use case menjadi output layar.
 * Entity tidak memiliki toString() untuk tampilan; format ada di sini.
 */
public class FinancePresenter extends BasePresenter<Transaction> {

    @Override
    protected String format(Transaction transaction) {
        String type = transaction.getType() == TransactionType.INCOME ? "[Pemasukan]" : "[Pengeluaran]";
        return String.format("[%d] %s | Rp %d | %s",
                transaction.getId(), transaction.getDescription(), transaction.getAmount(), type);
    }

    /** Menampilkan daftar semua transaksi beserta saldo. */
    public void showTransactions(List<Transaction> transactions, long balance) {
        printList(transactions, "Daftar Transaksi:", "- Belum ada transaksi!");
        System.out.printf("Saldo: Rp %d%n", balance);
    }

    /** Menampilkan hasil pencarian berdasarkan kata kunci. */
    public void showSearchResults(List<Transaction> transactions, String keyword) {
        printList(transactions, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    /** Menampilkan daftar data yang sudah diurutkan. */
    public void showSortedTransactions(List<Transaction> transactions) {
        printList(transactions, "Daftar Transaksi (Terurut):", "- Belum ada transaksi!");
    }

    /** Menampilkan pesan sukses setelah data ditambahkan. */
    public void showAddSuccess(Transaction transaction) {
        System.out.printf("Berhasil menambah transaksi: %s%n", format(transaction));
    }

    /** Menampilkan saldo saat ini. */
    public void showBalance(long balance) {
        System.out.printf("Saldo saat ini: Rp %d%n", balance);
    }

    /** Menampilkan pesan sukses setelah data dihapus. */
    public void showRemoveSuccess() {
        System.out.println("Berhasil menghapus transaksi.");
    }

    /** Menampilkan pesan gagal saat data dengan ID tertentu tidak dapat dihapus. */
    public void showRemoveFailed(int id) {
        System.out.printf("[!] Gagal menghapus transaksi dengan ID: %d.%n", id);
    }
}
