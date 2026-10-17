package usecase;

import domain.entity.Item;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IItemRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case inventaris barang. Menerima input mentah (String) dari View, melakukan parsing dan validasi,
 * lalu melempar checked exception domain jika gagal. Tidak melakukan I/O.
 */
public class ItemUseCase {
    private static final String INVALID_QUANTITY_MESSAGE = "Jumlah stok tidak valid!";

    private final IItemRepository itemRepository;

    public ItemUseCase(IItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** Mengambil semua barang. */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Memeriksa teks jumlah stok (angka dan > 0) tanpa menyimpan apa pun.
     * Dipakai View agar kesalahan jumlah bisa dilaporkan sebelum field berikutnya ditanyakan.
     *
     * @throws ValidationException jika jumlah bukan angka atau tidak positif
     */
    public void validateQuantity(String rawQuantity) throws ValidationException {
        parseQuantity(rawQuantity);
    }

    /**
     * Menambahkan barang baru setelah parsing jumlah dan validasi.
     *
     * @param rawQuantity jumlah stok sebagai teks mentah dari user
     * @throws ValidationException jika jumlah tidak valid, atau nama/kategori kosong
     */
    public Item addItem(String name, String rawQuantity, String category) throws ValidationException {
        int quantity = parseQuantity(rawQuantity);
        InputValidator.requireText(name, "Nama barang tidak boleh kosong!");
        InputValidator.requireText(category, "Kategori tidak boleh kosong!");
        return itemRepository.save(name, quantity, category);
    }

    /**
     * Memeriksa teks ID (angka) tanpa mengubah apa pun.
     * Dipakai View agar ID yang salah format langsung ditolak sebelum field berikutnya ditanyakan.
     *
     * @throws ValidationException jika {@code rawId} bukan angka
     */
    public void validateId(String rawId) throws ValidationException {
        InputValidator.parseId(rawId);
    }

    /**
     * Mengubah stok barang berdasarkan input mentah dari user.
     * {@code rawQuantity} kosong berarti stok tidak diubah.
     *
     * @throws ValidationException     jika ID bukan angka, atau jumlah tidak valid
     * @throws EntityNotFoundException jika tidak ada barang dengan ID tersebut
     */
    public void updateStock(String rawId, String rawQuantity)
            throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        String quantityText = InputValidator.blankToNull(rawQuantity);
        Integer quantity = quantityText == null ? null : parseQuantity(quantityText);

        Optional<Item> found = itemRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
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

    /**
     * Mengurutkan barang sesuai pilihan menu mentah dari user
     * (1 = nama A-Z, 2 = nama Z-A, 3 = jumlah terkecil, 4 = jumlah terbesar).
     *
     * @throws ValidationException jika pilihan tidak dikenali
     */
    public List<Item> sortItems(String choice) throws ValidationException {
        SortOption option = parseSortOption(choice);
        return itemRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /**
     * Menghapus barang berdasarkan ID mentah dari user.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada barang dengan ID tersebut
     */
    public void removeItem(String rawId) throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        if (!itemRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mengonversi teks menjadi jumlah stok (angka dan > 0). */
    private int parseQuantity(String rawQuantity) throws ValidationException {
        int quantity = InputValidator.parseInt(rawQuantity, INVALID_QUANTITY_MESSAGE);
        if (quantity <= 0) {
            throw new ValidationException(INVALID_QUANTITY_MESSAGE);
        }
        return quantity;
    }

    /** Memetakan pilihan menu menjadi {@link SortOption}; melempar {@link ValidationException} jika tidak dikenali. */
    private SortOption parseSortOption(String choice) throws ValidationException {
        return switch (choice) {
            case "1" -> SortOption.NAME_ASC;
            case "2" -> SortOption.NAME_DESC;
            case "3" -> SortOption.QUANTITY_ASC;
            case "4" -> SortOption.QUANTITY_DESC;
            default -> throw new ValidationException("Pilihan tidak valid!");
        };
    }
}
