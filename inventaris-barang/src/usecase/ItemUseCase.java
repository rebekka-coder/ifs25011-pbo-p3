package usecase;

import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case inventaris barang. Tidak melakukan I/O, hanya melempar checked exception domain.
 */
public class ItemUseCase {
    private final IItemRepository itemRepository;

    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** Mengambil semua data yang tersimpan. */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /** Menambahkan barang baru setelah validasi. */
    public Item addItem(String name, int quantity, String category) throws ValidationException {
        requireText(name, "Nama barang tidak boleh kosong!");
        if (quantity <= 0) {
            throw new ValidationException("Jumlah stok tidak valid!");
        }
        requireText(category, "Kategori tidak boleh kosong!");
        return itemRepository.save(name, quantity, category);
    }

    /**
     * Mengubah stok barang; melempar exception jika ID tidak ditemukan atau stok tidak valid.
     * Parameter {@code quantity} bernilai {@code null} berarti stok tidak diubah.
     */
    public void updateStock(int id, Integer quantity) throws EntityNotFoundException, ValidationException {
        Optional<Item> found = itemRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        if (quantity != null && quantity <= 0) {
            throw new ValidationException("Jumlah stok tidak valid!");
        }
        Item item = found.get();
        if (quantity != null) {
            item.changeQuantity(quantity);
        }
        itemRepository.update(item);
    }

    /** Mencari barang berdasarkan nama (case-insensitive). */
    public List<Item> searchItems(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return itemRepository.findBy(i -> i.getName().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan data sesuai kriteria {@link SortOption}. */
    public List<Item> sortItems(SortOption option) {
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghapus data berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeItem(int id) throws EntityNotFoundException {
        if (!itemRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Memastikan teks tidak null/kosong; selain itu melempar {@link ValidationException}. */
    private void requireText(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
