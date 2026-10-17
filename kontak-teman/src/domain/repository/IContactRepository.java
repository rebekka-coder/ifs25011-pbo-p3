package domain.repository;

import domain.entity.Contact;

/**
 * Port spesifik Contact yang memperluas generic {@link IRepository}.
 */
public interface IContactRepository extends IRepository<Contact, Integer> {
    /** Menyimpan kontak baru dengan ID otomatis. */
    Contact save(String name, String phone, String email);
}
