package domain.repository;

import domain.entity.Activity;

/**
 * Port spesifik Activity yang memperluas generic {@link IRepository}.
 */
public interface IActivityRepository extends IRepository<Activity, Integer> {
    /** Menyimpan kegiatan baru dengan ID otomatis. */
    Activity save(String title, String day, String time);
}
