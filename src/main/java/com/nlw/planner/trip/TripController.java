package com.nlw.planner.trip;

import com.nlw.planner.activity.*;
import com.nlw.planner.exception.DateErrorException;
import com.nlw.planner.exception.TripNotFoundException;
import com.nlw.planner.link.LinkData;
import com.nlw.planner.link.LinkRequestPayload;
import com.nlw.planner.link.LinkResponse;
import com.nlw.planner.link.LinkService;
import com.nlw.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private TripService tripService;

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
        Optional<Trip> trip = this.tripService.findTripById(id);

        return trip.map(ResponseEntity::ok).orElseThrow(TripNotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){
        Trip  newTrip = new Trip(payload);
        if(newTrip.getEndAt().isBefore(newTrip.getStartsAt())){
            throw new DateErrorException("A data de térmno deve ser maior que a data de início");
        } else if (newTrip.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new DateErrorException("A data de início não pode estar no passado");
        }
        this.tripService.registerTrip(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setEndAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            if(rawTrip.getEndAt().isBefore(rawTrip.getStartsAt())){
                throw new DateErrorException("A data de térmno deve ser maior que a data de início");
            } else if (rawTrip.getStartsAt().isBefore(LocalDateTime.now())) {
                throw new DateErrorException("A data de início não pode estar no passado");
            }
            rawTrip.setDestination(payload.destination());
            this.tripService.updateTrip(rawTrip);
            return ResponseEntity.ok(rawTrip);
        }else{
            throw new TripNotFoundException();
        }
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);
            this.tripService.updateTrip(rawTrip);
            this.participantService.triggerConfirmationEmailToPaticipants(id);
            return ResponseEntity.ok(rawTrip);
        }else{
            throw new TripNotFoundException();
        }
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantData> participants = this.participantService.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participants);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,@RequestBody ParticipantRequestPayload payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(),rawTrip);

            if(rawTrip.getIsConfirmed()) this.participantService.triggerConfirmationEmailToPaticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }else{
            throw new TripNotFoundException();
        }
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activities = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activities);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            Activity activity = new Activity(payload.title(), payload.occurs_at(), rawTrip);
            if(activity.getOccursAt().isBefore(rawTrip.getStartsAt()) || activity.getOccursAt().isAfter(rawTrip.getEndAt()) ){
                throw new DateErrorException("A data da atividade deve estar entre o período da trip");
            }
            ActivityResponse activityResponse = this.activityService.registerActivity(activity);
            return ResponseEntity.ok(activityResponse);
        }else{
            throw new TripNotFoundException();
        }
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> links = this.linkService.getAllLinksFromId(id);

        return ResponseEntity.ok(links);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            LinkResponse linkResponse = this.linkService.registerLink(payload,rawTrip);

            return ResponseEntity.ok(linkResponse);
        }else{
            throw new TripNotFoundException();
        }
    }

}
