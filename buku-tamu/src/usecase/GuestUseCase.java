package usecase;

import domain.entity.Guest;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IGuestRepository;
import java.util.List;

/**
 * Use case buku tamu. Tidak melakukan I/O, hanya melempar checked exception domain.
 */
public class GuestUseCase {
    private final IGuestRepository guestRepository;

    public GuestUseCase(IGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /** Mengambil semua tamu. */
    /** Mengambil semua data yang tersimpan. */
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    /** Mendaftarkan tamu baru setelah validasi. */
    /** Form tambah data baru. */
    public Guest registerGuest(String name, String purpose) throws ValidationException {
        requireText(name, "Nama tamu tidak boleh kosong!");
        requireText(purpose, "Tujuan kunjungan tidak boleh kosong!");
        return guestRepository.save(name, purpose);
    }

    /** Mencari tamu berdasarkan nama (case-insensitive). */
    /** Form pencarian data berdasarkan kata kunci. */
    public List<Guest> searchGuests(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return guestRepository.findBy(g -> g.getName().toLowerCase().contains(lowerKeyword));
    }

    /** Menghapus tamu berdasarkan ID; melempar exception jika tidak ditemukan. */
    /** Menghapus data berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeGuest(int id) throws EntityNotFoundException {
        if (!guestRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Memastikan teks tidak null/kosong; selain itu melempar {@link ValidationException}. */
    private void requireText(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }
}
