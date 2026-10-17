package usecase;

import domain.entity.SortOption;
import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.ITransactionRepository;
import java.util.List;

/**
 * Use case yang menangani logika bisnis pencatatan keuangan.
 * Menerima input mentah (String) dari View, melakukan parsing dan validasi,
 * lalu melempar checked exception domain jika gagal. Tidak melakukan I/O.
 */
public class FinanceUseCase {
    private static final String INVALID_AMOUNT_MESSAGE = "Jumlah tidak valid!";

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Menambahkan transaksi baru setelah parsing jumlah dan validasi.
     *
     * @param rawAmount jumlah uang sebagai teks mentah dari user
     * @throws ValidationException jika jumlah bukan angka atau tidak positif, atau keterangan kosong
     */
    public Transaction addTransaction(String description, String rawAmount, TransactionType type)
            throws ValidationException {
        long amount = InputValidator.parseLong(rawAmount, INVALID_AMOUNT_MESSAGE);
        String cleanDescription = InputValidator.requireText(description, "Keterangan tidak boleh kosong!");
        if (amount <= 0) {
            throw new ValidationException(INVALID_AMOUNT_MESSAGE);
        }
        return transactionRepository.save(cleanDescription, amount, type);
    }

    /**
     * Menghapus transaksi berdasarkan ID mentah dari user.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada transaksi dengan ID tersebut
     */
    public void removeTransaction(String rawId) throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        if (!transactionRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        return transactionRepository.findBy(
                transaction -> TextMatcher.containsIgnoreCase(transaction.getDescription(), keyword));
    }

    /**
     * Mengurutkan transaksi sesuai pilihan menu mentah dari user
     * (1 = jumlah terkecil, 2 = jumlah terbesar, 3 = pemasukan dulu, 4 = pengeluaran dulu).
     *
     * @throws ValidationException jika pilihan tidak dikenali
     */
    public List<Transaction> sortTransactions(String choice) throws ValidationException {
        SortOption option = parseSortOption(choice);
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghitung saldo = total pemasukan - total pengeluaran. */
    public long getBalance() {
        return transactionRepository.findAll().stream()
                .mapToLong(Transaction::signedAmount)
                .sum();
    }

    /** Memetakan pilihan menu menjadi {@link SortOption}; melempar {@link ValidationException} jika tidak dikenali. */
    private SortOption parseSortOption(String choice) throws ValidationException {
        return switch (choice) {
            case "1" -> SortOption.AMOUNT_ASC;
            case "2" -> SortOption.AMOUNT_DESC;
            case "3" -> SortOption.INCOME_FIRST;
            case "4" -> SortOption.EXPENSE_FIRST;
            default -> throw new ValidationException("Pilihan tidak valid!");
        };
    }
}
