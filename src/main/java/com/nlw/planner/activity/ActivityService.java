package com.nlw.planner.activity;


import com.nlw.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponse registerActivity(Activity activity){
        this.repository.save(activity);

        return new ActivityResponse(activity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID id) {
        return this.repository.findByTripId(id).stream().map(activity -> new ActivityData(activity.getId(),activity.getTitle(), activity.getOccursAt())).toList();
    }
}
