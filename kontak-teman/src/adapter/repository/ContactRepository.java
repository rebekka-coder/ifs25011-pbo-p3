package adapter.repository;

import domain.entity.Contact;
import domain.repository.IContactRepository;

/**
 * Implementasi repository kontak menggunakan basis generic {@link InMemoryRepository}.
 */
public class ContactRepository extends InMemoryRepository<Contact, Integer> implements IContactRepository {
    private int idCounter = 0;

    public ContactRepository() {
        super(Contact::getId);
    }

    @Override
    public Contact save(String name, String phone, String email) {
        return save(new Contact(nextId(), name, phone, email));
    }

    private int nextId() {
        return ++idCounter;
    }
}
