package usecase;

import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case jadwal kegiatan. Menerima input mentah (String) dari View, melakukan parsing dan validasi,
 * lalu melempar checked exception domain jika gagal. Tidak melakukan I/O.
 */
public class ActivityUseCase {
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua kegiatan. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Menambahkan kegiatan baru setelah validasi. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        InputValidator.requireText(title, "Judul kegiatan tidak boleh kosong!");
        InputValidator.requireText(day, "Hari tidak boleh kosong!");
        InputValidator.requireText(time, "Waktu tidak boleh kosong!");
        return activityRepository.save(title, day, time);
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
     * Mengubah judul, hari, dan/atau waktu kegiatan berdasarkan input mentah dari user.
     * Teks kosong pada sebuah field berarti field tersebut tidak diubah.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada kegiatan dengan ID tersebut
     */
    public void updateActivity(String rawId, String title, String day, String time)
            throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        Optional<Activity> found = activityRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }

        Activity activity = found.get();
        String newTitle = InputValidator.blankToNull(title);
        String newDay = InputValidator.blankToNull(day);
        String newTime = InputValidator.blankToNull(time);
        if (newTitle != null) {
            activity.changeTitle(newTitle);
        }
        if (newDay != null) {
            activity.changeDay(newDay);
        }
        if (newTime != null) {
            activity.changeTime(newTime);
        }
        activityRepository.update(activity);
    }

    /** Mencari kegiatan berdasarkan judul (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return activityRepository.findBy(a -> a.getTitle().toLowerCase().contains(lowerKeyword));
    }

    /**
     * Mengurutkan kegiatan sesuai pilihan menu mentah dari user
     * (1 = hari, 2 = waktu, 3 = judul A-Z, 4 = judul Z-A).
     *
     * @throws ValidationException jika pilihan tidak dikenali
     */
    public List<Activity> sortActivities(String choice) throws ValidationException {
        SortOption option = parseSortOption(choice);
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /**
     * Menghapus kegiatan berdasarkan ID mentah dari user.
     *
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada kegiatan dengan ID tersebut
     */
    public void removeActivity(String rawId) throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        if (!activityRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Memetakan pilihan menu menjadi {@link SortOption}; melempar {@link ValidationException} jika tidak dikenali. */
    private SortOption parseSortOption(String choice) throws ValidationException {
        return switch (choice) {
            case "1" -> SortOption.DAY;
            case "2" -> SortOption.TIME;
            case "3" -> SortOption.TITLE_ASC;
            case "4" -> SortOption.TITLE_DESC;
            default -> throw new ValidationException("Pilihan tidak valid!");
        };
    }
}
