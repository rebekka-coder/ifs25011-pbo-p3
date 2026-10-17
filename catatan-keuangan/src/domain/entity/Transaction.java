package domain.entity;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain, bebas dari urusan tampilan maupun penyimpanan.
 */
public class Transaction {
    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Keterangan transaksi. */
    private final String description;

    /** Jumlah uang (selalu positif). */
    private final long amount;

    /** Jenis transaksi: pemasukan atau pengeluaran. */
    private final TransactionType type;

    public Transaction(int id, String description, long amount, TransactionType type) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    /** Jumlah bertanda untuk perhitungan saldo: positif untuk pemasukan, negatif untuk pengeluaran. */
    public long signedAmount() {
        return type == TransactionType.INCOME ? amount : -amount;
    }
}
