package adapter.repository;

import domain.entity.Activity;
import domain.repository.IActivityRepository;

/**
 * Implementasi repository kegiatan menggunakan basis generic {@link InMemoryRepository}.
 */
public class ActivityRepository extends InMemoryRepository<Activity, Integer> implements IActivityRepository {
    public ActivityRepository() {
        super(Activity::getId);
    }

    @Override
    public Activity save(String title, String day, String time) {
        return save(new Activity(nextId(), title, day, time));
    }

    private int nextId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextId'");
    }
}
