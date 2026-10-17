package adapter.repository;

import domain.entity.Guest;
import domain.repository.IGuestRepository;

/**
 * Implementasi repository tamu menggunakan basis generic {@link InMemoryRepository}.
 */
public class GuestRepository extends InMemoryRepository<Guest, Integer> implements IGuestRepository {
    public GuestRepository() {
        super(Guest::getId);
    }

    @Override
    public Guest save(String name, String purpose) {
        return save(new Guest(nextId(), name, purpose));
    }
}
