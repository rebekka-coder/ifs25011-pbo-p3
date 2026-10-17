package domain.entity;

import java.util.List;

/**
 * Entity inti yang merepresentasikan satu kegiatan terjadwal.
 */
public class Activity {
    /** Urutan hari dalam seminggu, dipakai untuk pengurutan berdasarkan hari. */
    private static final List<String> DAYS =
            List.of("senin", "selasa", "rabu", "kamis", "jumat", "sabtu", "minggu");

    /** ID unik kegiatan, tidak boleh diubah setelah dibuat. */
    private final int id;

    private String title;
    private String day;
    private String time;

    public Activity(int id, String title, String day, String time) {
        this.id = id;
        this.title = title;
        this.day = day;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    /** Nomor urut hari (Senin = 0 ... Minggu = 6); hari tak dikenal diurutkan paling akhir. */
    public int getDayOrder() {
        int index = DAYS.indexOf(day.trim().toLowerCase());
        return index >= 0 ? index : DAYS.size();
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeDay(String day) {
        this.day = day;
    }

    public void changeTime(String time) {
        this.time = time;
    }
}
