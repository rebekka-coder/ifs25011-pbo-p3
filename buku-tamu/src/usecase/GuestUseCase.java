package usecase;

import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IGuestRepository;
import java.util.List;

/**
 * Use case buku tamu. Menerima input mentah (String) dari View, melakukan parsing dan validasi,
 * lalu melempar checked exception domain jika gagal. Tidak melakukan I/O.
 */
public class GuestUseCase {
    private final IGuestRepository guestRepository;

    public GuestUseCase(IGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /** Mengambil semua tamu. */
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    /** Mendaftarkan tamu baru setelah validasi. */
    public Guest registerGuest(String name, String purpose) throws ValidationException {
        String cleanName = InputValidator.requireText(name, "Nama tamu tidak boleh kosong!");
        String cleanPurpose = InputValidator.requireText(purpose, "Tujuan kunjungan tidak boleh kosong!");
        return guestRepository.save(cleanName, cleanPurpose);
    }

    /** Mencari tamu berdasarkan nama (case-insensitive). */
    public List<Guest> searchGuests(String keyword) {
        return guestRepository.findBy(guest -> TextMatcher.containsIgnoreCase(guest.getName(), keyword));
    }

    /**
     * Menghapus tamu berdasarkan ID mentah dari user.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada tamu dengan ID tersebut
     */
    public void removeGuest(String rawId) throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        if (!guestRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }
}
