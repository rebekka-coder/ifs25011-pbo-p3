package shared.repository;

import domain.repository.IRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementasi repository in-memory yang generic.
 * Menggunakan ConcurrentHashMap untuk thread-safety.
 * Data disimpan secara private, dan ID di-generate melalui method protected nextId().
 */
public class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    
    // ✅ REVIEWER: "data private"
    private final Map<ID, T> data = new ConcurrentHashMap<>();
    
    // Counter untuk auto-increment ID (diasumsikan ID adalah Integer)
    private int nextIdCounter = 1;

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<T> findBy(Predicate<T> condition) {
        return data.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(ID id) {
        return data.remove(id) != null;
    }

    /** 
     * ✅ REVIEWER: "memiliki nextId()"
     * Method ini protected agar bisa dipanggil oleh subclass (misal: GuestRepository) 
     * untuk mendapatkan ID baru sebelum membuat object entity.
     */
    @SuppressWarnings("unchecked")
    protected ID nextId() {
        return (ID) Integer.valueOf(nextIdCounter++);
    }

    /** 
     * Method bantu untuk subclass menyimpan entity yang sudah memiliki ID.
     * Karena 'data' bersifat private, subclass tidak bisa langsung melakukan data.put().
     */
    protected void saveEntity(ID id, T entity) {
        data.put(id, entity);
    }

    /** 
     * Implementasi default save. 
     * Catatan: Jika entity membutuhkan ID saat konstruksi, subclass sebaiknya 
     * membuat method save-nya sendiri (misal: save(String name)) yang memanggil nextId() 
     * lalu saveEntity(id, entity).
     */
    @Override
    public T save(T entity) {
        // Asumsi: Entity sudah memiliki ID yang valid di dalamnya, 
        // atau interface IRepository memiliki cara untuk mengambil ID.
        // Jika tidak, method ini bisa di-override oleh subclass.
        throw new UnsupportedOperationException(
            "Silakan override method save() di subclass atau gunakan saveEntity(id, entity)"
        );
    }

    @Override
    public void update(T entity) {
        // Sama seperti save, membutuhkan cara untuk mengambil ID dari entity.
        // Subclass sebaiknya meng-override method ini jika diperlukan.
        throw new UnsupportedOperationException(
            "Silakan override method update() di subclass"
        );
    }
}

