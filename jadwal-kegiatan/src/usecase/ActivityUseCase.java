package usecase;

import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Use case jadwal kegiatan. Menerima input mentah (String) dari caller,
 * melakukan parsing dan validasi, lalu melempar checked exception domain jika gagal.
 * Tidak melakukan I/O dan tidak bergantung pada layer presentasi.
 */
public class ActivityUseCase {
    /**
     * Waktu berformat HH:mm (00:00 - 23:59). Format baku ini diperlukan karena
     * pengurutan berdasarkan waktu membandingkan teksnya secara alfabetis.
     */
    private static final Pattern TIME_PATTERN = Pattern.compile("([01]\\d|2[0-3]):[0-5]\\d");

    private static final String INVALID_TIME_MESSAGE = "Format waktu tidak valid! Gunakan HH:mm (contoh: 08:00).";

    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua kegiatan. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /**
     * Menambahkan kegiatan baru setelah validasi.
     *
     * @throws ValidationException jika ada field yang kosong atau format waktu salah
     */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        String cleanTitle = InputValidator.requireText(title, "Judul kegiatan tidak boleh kosong!");
        String cleanDay = InputValidator.requireText(day, "Hari tidak boleh kosong!");
        String cleanTime = InputValidator.requireText(time, "Waktu tidak boleh kosong!");
        validateTimeFormat(cleanTime);
        return activityRepository.save(cleanTitle, cleanDay, cleanTime);
    }

    /**
     * Memeriksa format ID (harus berupa angka) tanpa mengubah data apa pun.
     * Dipakai View agar ID yang salah format langsung ditolak sebelum field berikutnya ditanyakan.
     *
     * @param rawId teks ID mentah dari pengguna
     * @throws ValidationException jika {@code rawId} bukan angka
     */
    public void validateId(String rawId) throws ValidationException {
        InputValidator.parseId(rawId);
    }

    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan berdasarkan input mentah dari pengguna.
     * Teks kosong pada sebuah field berarti field tersebut tidak diubah.
     * Waktu yang diisi divalidasi lebih dulu sehingga kegiatan tidak pernah berubah sebagian.
     *
     * @param rawId teks ID mentah dari pengguna
     * @param title judul baru (kosong jika tidak diubah)
     * @param day   hari baru (kosong jika tidak diubah)
     * @param time  waktu baru (kosong jika tidak diubah)
     * @throws ValidationException     jika {@code rawId} bukan angka atau format waktu yang diisi salah
     * @throws EntityNotFoundException jika tidak ada kegiatan dengan ID tersebut
     */
    public void updateActivity(String rawId, String title, String day, String time)
            throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        String newTitle = InputValidator.blankToNull(title);
        String newDay = InputValidator.blankToNull(day);
        String newTime = InputValidator.blankToNull(time);
        if (newTime != null) {
            validateTimeFormat(newTime);
        }

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

    /**
     * Mencari kegiatan berdasarkan judul (case-insensitive).
     *
     * @param keyword kata kunci pencarian
     * @return daftar kegiatan yang judulnya mengandung keyword
     */
    public List<Activity> searchActivities(String keyword) {
        return activityRepository.findBy(activity -> TextMatcher.containsIgnoreCase(activity.getTitle(), keyword));
    }

    /**
     * Mengurutkan kegiatan sesuai pilihan menu mentah dari pengguna.
     *
     * @param choice pilihan menu ("1" = hari, "2" = waktu, "3" = judul A-Z, "4" = judul Z-A)
     * @return daftar kegiatan yang sudah diurutkan
     * @throws ValidationException jika pilihan tidak dikenali
     */
    public List<Activity> sortActivities(String choice) throws ValidationException {
        SortOption option = parseSortOption(choice);
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /**
     * Menghapus kegiatan berdasarkan ID mentah dari pengguna.
     *
     * @param rawId teks ID mentah dari pengguna
     * @throws ValidationException     jika {@code rawId} bukan angka
     * @throws EntityNotFoundException jika tidak ada kegiatan dengan ID tersebut
     */
    public void removeActivity(String rawId) throws ValidationException, EntityNotFoundException {
        int id = InputValidator.parseId(rawId);
        if (!activityRepository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Memastikan waktu (sudah dirapikan) berformat HH:mm. */
    private void validateTimeFormat(String time) throws ValidationException {
        InputValidator.requireFormat(time, TIME_PATTERN, INVALID_TIME_MESSAGE);
    }

    /**
     * Memetakan input mentah dari pengguna menjadi enum {@link SortOption}.
     *
     * @param choice pilihan menu ("1", "2", "3", atau "4")
     * @return enum SortOption yang sesuai
     * @throws ValidationException jika pilihan tidak ada di menu
     */
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
