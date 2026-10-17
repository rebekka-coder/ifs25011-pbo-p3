package domain.repository;

import domain.entity.Guest;

/**
 * Port spesifik Guest yang memperluas generic {@link IRepository}.
 */
public interface IGuestRepository extends IRepository<Guest, Integer> {
    /** Menyimpan tamu baru dengan ID otomatis. */
    Guest save(String name, String purpose);
}
