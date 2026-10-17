package domain.entity;

/**
 * Entity inti yang merepresentasikan satu barang inventaris.
 */
public class Item {
    /** ID unik barang, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Nama barang. */
    private final String name;

    /** Jumlah stok barang. */
    private int quantity;

    /** Kategori barang. */
    private final String category;

    public Item(int id, String name, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    /** Mengubah jumlah stok barang. */
    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }
}
