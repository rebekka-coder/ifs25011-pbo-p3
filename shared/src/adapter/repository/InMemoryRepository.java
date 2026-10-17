package adapter.repository;

import domain.repository.IRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Basis repository in-memory yang generik. Satu-satunya implementasi penyimpanan
 * yang dipakai semua studi kasus, sehingga tidak ada kode penyimpanan yang disalin ulang.
 *
 * <p>Rancangan:
 * <ul>
 *   <li>{@code data} bersifat {@code private}; subclass hanya berinteraksi lewat
 *       method {@code IRepository} dan {@link #nextId()}.</li>
 *   <li>Data disimpan di {@link LinkedHashMap} berkunci ID: pencarian per ID O(1),
 *       urutan tampil tetap sesuai urutan penyimpanan.</li>
 *   <li>ID tidak boleh kembar: {@link #save(Object)} menolak entitas dengan ID yang sudah ada.</li>
 *   <li>Daftar yang dikembalikan tidak dapat dimodifikasi dari luar (salinan defensif).</li>
 * </ul>
 *
 * @param <T>  tipe entitas domain
 * @param <ID> tipe identifier unik entitas
 */
public abstract class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    /** Penyimpanan entitas berdasarkan ID, menjaga urutan penyimpanan. */
    private final Map<ID, T> data = new LinkedHashMap<>();

    /** Fungsi untuk membaca ID dari sebuah entitas. */
    private final Function<T, ID> idExtractor;

    /** ID terakhir yang dihasilkan {@link #nextId()}; ID tidak dipakai ulang walau data dihapus. */
    private int lastGeneratedId = 0;

    /**
     * @param idExtractor fungsi pengambil ID dari entitas, mis. {@code Contact::getId}
     */
    protected InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor wajib diisi");
    }

    /**
     * Menghasilkan ID bilangan bulat berikutnya (mulai dari 1) untuk entitas baru.
     * Dipakai oleh repository turunan yang ber-ID {@code Integer}.
     */
    protected int nextId() {
        return ++lastGeneratedId;
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException jika entitas dengan ID yang sama sudah tersimpan
     */
    @Override
    public T save(T entity) {
        ID id = idOf(entity);
        if (data.containsKey(id)) {
            throw new IllegalArgumentException("Data dengan ID " + id + " sudah ada.");
        }
        data.put(id, entity);
        return entity;
    }

    @Override
    public boolean deleteById(ID id) {
        return data.remove(id) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException jika belum ada entitas dengan ID tersebut
     */
    @Override
    public void update(T entity) {
        ID id = idOf(entity);
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("Data dengan ID " + id + " tidak ditemukan.");
        }
        data.put(id, entity);
    }

    @Override
    public List<T> findBy(Predicate<T> predicate) {
        return data.values().stream()
                .filter(predicate)
                .toList();
    }

    /** Membaca ID entitas; menolak entitas {@code null}. */
    private ID idOf(T entity) {
        return idExtractor.apply(Objects.requireNonNull(entity, "Entitas tidak boleh null"));
    }
}
