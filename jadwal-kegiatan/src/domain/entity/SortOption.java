package domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan kegiatan. Setiap opsi membawa comparator-nya sendiri.
 */
public enum SortOption {
    /** Urut hari (Senin-Minggu), lalu waktu. */
    DAY(Comparator.comparingInt(Activity::getDayOrder).thenComparing(Activity::getTime)),

    /** Urut waktu (HH:mm), lalu hari. */
    TIME(Comparator.comparing(Activity::getTime).thenComparingInt(Activity::getDayOrder)),

    /** Judul A-Z (case-insensitive). */
    TITLE_ASC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER)),

    /** Judul Z-A (case-insensitive). */
    TITLE_DESC(Comparator.comparing(Activity::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());

    private final Comparator<Activity> comparator;

    SortOption(Comparator<Activity> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Activity> comparator() {
        return comparator;
    }
}
