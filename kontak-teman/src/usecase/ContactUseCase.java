package usecase;

import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case kontak teman. Tidak melakukan I/O, hanya melempar checked exception domain.
 */
public class ContactUseCase {
    private final IContactRepository contactRepository;

    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** Mengambil semua data yang tersimpan. */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /** Menambahkan kontak baru setelah validasi. */
    /** Form tambah data baru. */
    public Contact addContact(String name, String phone, String email) throws ValidationException {
        requireText(name, "Nama tidak boleh kosong!");
        requireText(phone, "Nomor telepon tidak boleh kosong!");
        requireText(email, "Email tidak boleh kosong!");
        return contactRepository.save(name, phone, email);
    }

    /**
     * Mengubah nama, telepon, dan/atau email kontak.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     */
    public void updateContact(int id, String name, String phone, String email)
            throws EntityNotFoundException, ValidationException {
        Optional<Contact> found = contactRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }

        requireTextIfPresent(name, "Nama tidak boleh kosong!");
        requireTextIfPresent(phone, "Nomor telepon tidak boleh kosong!");
        requireTextIfPresent(email, "Email tidak boleh kosong!");

        Contact contact = found.get();
        if (name != null) {
            contact.changeName(name);
        }
        if (phone != null) {
            contact.changePhone(phone);
        }
        if (email != null) {
            contact.changeEmail(email);
        }
        contactRepository.update(contact);
    }

    /** Mencari kontak berdasarkan nama (case-insensitive). */
    /** Form pencarian data berdasarkan kata kunci. */
    public List<Contact> searchContacts(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return contactRepository.findBy(c -> c.getName().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan data sesuai kriteria {@link SortOption}. */
    public List<Contact> sortContacts(SortOption option) {
        return contactRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghapus data berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeContact(int id) throws EntityNotFoundException {
        if (!contactRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    private void requireText(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /** Validasi opsional untuk update parsial: null berarti field tidak diubah. */
    private void requireTextIfPresent(String value, String message) throws ValidationException {
        if (value != null) {
            requireText(value, message);
        }
    }
}
