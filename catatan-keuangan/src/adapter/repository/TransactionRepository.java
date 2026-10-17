package adapter.repository;

import domain.entity.Transaction;
import domain.entity.TransactionType;
import domain.repository.ITransactionRepository;

/**
 * Implementasi repository transaksi menggunakan basis generic {@link InMemoryRepository}.
 */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {
    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction save(String description, long amount, TransactionType type) {
        return save(new Transaction(nextId(), description, amount, type));
    }
}
