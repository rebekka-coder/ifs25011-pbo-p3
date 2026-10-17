package usecase;

import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case kontak teman. Menerima input mentah (String) dari View, melakukan parsing dan validasi,
 * lalu melempar checked exception domain jika gagal. Tidak melakukan I/O.
 */
public class ContactUseCase {
    private final IContactRepository contactRepository;

    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** Mengambil semua kontak. */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /** Menambahkan kontak baru setelah validasi. */
    public Contact addContact(String name, String phone, String email) throws ValidationException {
        InputValidator.requireText(name, "Nama tidak boleh kosong!");
        InputValidator.requireText(phone, "Nomor telepon tidak boleh kosong!");
        InputValidator.requireText(email, "Email tidak boleh kosong!");
        return contactRepository.save(name, phone, email);
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
     * Mengubah nama, telepon, dan/atau email kontak berdasarkan input mentah dari user.
     * Teks kosong pada sebuah field berarti field tersebut tidak diubah.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada kontak dengan ID tersebut
     */
    public void updateContact(String rawId, String name, String phone, String email)
            throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        Optional<Contact> found = contactRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }

        Contact contact = found.get();
        String newName = InputValidator.blankToNull(name);
        String newPhone = InputValidator.blankToNull(phone);
        String newEmail = InputValidator.blankToNull(email);
        if (newName != null) {
            contact.changeName(newName);
        }
        if (newPhone != null) {
            contact.changePhone(newPhone);
        }
        if (newEmail != null) {
            contact.changeEmail(newEmail);
        }
        contactRepository.update(contact);
    }

    /** Mencari kontak berdasarkan nama (case-insensitive). */
    public List<Contact> searchContacts(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return contactRepository.findBy(c -> c.getName().toLowerCase().contains(lowerKeyword));
    }

    /**
     * Mengurutkan kontak sesuai pilihan menu mentah dari user (1 = nama A-Z, 2 = nama Z-A).
     *
     * @throws ValidationException jika pilihan tidak dikenali
     */
    public List<Contact> sortContacts(String choice) throws ValidationException {
        SortOption option = parseSortOption(choice);
        return contactRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /**
     * Menghapus kontak berdasarkan ID mentah dari user.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada kontak dengan ID tersebut
     */
    public void removeContact(String rawId) throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        if (!contactRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Memetakan pilihan menu menjadi {@link SortOption}; melempar {@link ValidationException} jika tidak dikenali. */
    private SortOption parseSortOption(String choice) throws ValidationException {
        return switch (choice) {
            case "1" -> SortOption.NAME_ASC;
            case "2" -> SortOption.NAME_DESC;
            default -> throw new ValidationException("Pilihan tidak valid!");
        };
    }
}
