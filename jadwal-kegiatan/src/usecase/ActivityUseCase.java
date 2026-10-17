package usecase;

import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.Optional;

/**
 * Use case jadwal kegiatan. Tidak melakukan I/O, hanya melempar checked exception domain.
 */
public class ActivityUseCase {
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua data yang tersimpan. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Menambahkan kegiatan baru setelah validasi. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        requireText(title, "Judul kegiatan tidak boleh kosong!");
        requireText(day, "Hari tidak boleh kosong!");
        requireText(time, "Waktu tidak boleh kosong!");
        return activityRepository.save(title, day, time);
    }

    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     */
    public void updateActivity(int id, String title, String day, String time)
            throws EntityNotFoundException, ValidationException {
        Optional<Activity> found = activityRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }

        requireTextIfPresent(title, "Judul kegiatan tidak boleh kosong!");
        requireTextIfPresent(day, "Hari tidak boleh kosong!");
        requireTextIfPresent(time, "Waktu tidak boleh kosong!");

        Activity activity = found.get();
        if (title != null) {
            activity.changeTitle(title);
        }
        if (day != null) {
            activity.changeDay(day);
        }
        if (time != null) {
            activity.changeTime(time);
        }
        activityRepository.update(activity);
    }

    /** Mencari kegiatan berdasarkan judul (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return activityRepository.findBy(a -> a.getTitle().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan data sesuai kriteria {@link SortOption}. */
    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghapus data berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeActivity(int id) throws EntityNotFoundException {
        if (!activityRepository.deleteById(id)) {
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
