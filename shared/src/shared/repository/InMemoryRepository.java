package shared.repository;

import domain.repository.IRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementasi repository in-memory yang generic.
 * Menggunakan ConcurrentHashMap untuk thread-safety.
 */
public class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    private final Map<ID, T> data = new ConcurrentHashMap<>();
    private int nextId = 1;

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
    public T save(T entity) {
        // Asumsi entity punya method getId() - sesuaikan dengan interface
        ID id = generateId();
        data.put(id, entity);
        return entity;
    }

    @Override
    public void update(T entity) {
        ID id = getId(entity);
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("Entity dengan ID " + id + " tidak ditemukan");
        }
        data.put(id, entity);
    }

    @Override
    public boolean deleteById(ID id) {
        return data.remove(id) != null;
    }

    /** Generate ID baru secara otomatis. */
    protected ID generateId() {
        @SuppressWarnings("unchecked")
        ID id = (ID) Integer.valueOf(nextId++);
        return id;
    }

    /** Ambil ID dari entity. Override jika entity punya method getId() sendiri. */
    protected ID getId(T entity) {
        // Sesuaikan dengan cara entity menyimpan ID
        throw new UnsupportedOperationException("Override method ini untuk entity spesifik");
    }
}