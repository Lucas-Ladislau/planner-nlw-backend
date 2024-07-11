package com.nlw.planner.trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository repository;

    public Trip registerTrip(Trip trip) {
       return this.repository.save(trip);
    }

    public void updateTrip(Trip trip) {
        this.repository.save(trip);
    }

    public Optional<Trip> findTripById(UUID tripId){
        return this.repository.findById(tripId);
    }
}
