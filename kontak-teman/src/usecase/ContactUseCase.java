package usecase;

import domain.entity.Contact;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IContactRepository;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Use case kontak teman. Menerima input mentah (String) dari View, melakukan parsing dan validasi,
 * lalu melempar checked exception domain jika gagal. Tidak melakukan I/O.
 */
public class ContactUseCase {
    /** Telepon: 8-15 digit angka, boleh diawali tanda "+" (mis. 081234567890 atau +6281234567890). */
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?\\d{8,15}");

    /** Email sederhana: bagian-lokal@domain.tld (mis. budi@email.com). */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w.%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+");

    private static final String INVALID_PHONE_MESSAGE =
            "Nomor telepon tidak valid! Gunakan 8-15 digit angka (boleh diawali +).";
    private static final String INVALID_EMAIL_MESSAGE = "Format email tidak valid!";
    private static final String MIN_ONE_FIELD_MESSAGE = "Minimal satu field harus diisi untuk update!";

    private final IContactRepository contactRepository;

    public ContactUseCase(IContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /** Mengambil semua kontak. */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /**
     * Menambahkan kontak baru setelah validasi.
     *
     * @throws ValidationException jika ada field yang kosong, atau format telepon/email salah
     */
    public Contact addContact(String name, String phone, String email) throws ValidationException {
        String cleanName = InputValidator.requireText(name, "Nama tidak boleh kosong!");
        String cleanPhone = InputValidator.requireText(phone, "Nomor telepon tidak boleh kosong!");
        String cleanEmail = InputValidator.requireText(email, "Email tidak boleh kosong!");
        validatePhoneFormat(cleanPhone);
        validateEmailFormat(cleanEmail);
        return contactRepository.save(cleanName, cleanPhone, cleanEmail);
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
     * Teks kosong pada sebuah field berarti field tersebut tidak diubah, tetapi
     * minimal satu field harus diisi. Semua field divalidasi lebih dulu sehingga
     * kontak tidak pernah berubah sebagian.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka, semua field kosong,
     *                                 atau format telepon/email yang diisi salah
     * @throws EntityNotFoundException jika tidak ada kontak dengan ID tersebut
     */
    public void updateContact(String rawId, String name, String phone, String email)
            throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        String newName = InputValidator.blankToNull(name);
        String newPhone = InputValidator.blankToNull(phone);
        String newEmail = InputValidator.blankToNull(email);
        if (newName == null && newPhone == null && newEmail == null) {
            throw new ValidationException(MIN_ONE_FIELD_MESSAGE);
        }
        if (newPhone != null) {
            validatePhoneFormat(newPhone);
        }
        if (newEmail != null) {
            validateEmailFormat(newEmail);
        }

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
        return contactRepository.findBy(contact -> TextMatcher.containsIgnoreCase(contact.getName(), keyword));
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

    /** Memastikan nomor telepon (sudah dirapikan) sesuai format. */
    private void validatePhoneFormat(String phone) throws ValidationException {
        InputValidator.requireFormat(phone, PHONE_PATTERN, INVALID_PHONE_MESSAGE);
    }

    /** Memastikan email (sudah dirapikan) sesuai format. */
    private void validateEmailFormat(String email) throws ValidationException {
        InputValidator.requireFormat(email, EMAIL_PATTERN, INVALID_EMAIL_MESSAGE);
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
