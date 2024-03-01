package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event{
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventArtistName() {
        return eventArtistName;
    }

    public void setEventArtistName(String eventArtistName) {
        this.eventArtistName = eventArtistName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public int getEventPermission() {
        return eventPermission;
    }

    public void setEventPermission(int eventPermission) {
        this.eventPermission = eventPermission;
    }

    public Event(int eventId, String eventArtistName, String eventName, String eventVenue, LocalDateTime eventStartTime, LocalDateTime eventEndTime, int eventPermission, List<EventPlanner> eventPlanners) {
        this.eventId = eventId;
        this.eventArtistName = eventArtistName;
        this.eventName = eventName;
        this.eventVenue = eventVenue;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventPermission = eventPermission;
        this.eventPlanners = eventPlanners != null ? eventPlanners : new ArrayList<>();
    }

    public Event(String eventArtistName, String eventName, String eventVenue, LocalDateTime eventStartTime, LocalDateTime eventEndTime, int eventPermission, List<EventPlanner> eventPlanners) {
        this.eventArtistName = eventArtistName;
        this.eventName = eventName;
        this.eventVenue = eventVenue;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventPermission = eventPermission;
        this.eventPlanners = eventPlanners != null ? eventPlanners : new ArrayList<>();
    }

    public Event(int eventId, String eventArtistName, String eventName, String eventVenue, LocalDateTime eventStartTime, LocalDateTime eventEndTime, int eventPermission, List<EventPlanner> eventPlanners, int eventVersion) {
        this.eventId = eventId;
        this.eventArtistName = eventArtistName;
        this.eventName = eventName;
        this.eventVenue = eventVenue;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventPermission = eventPermission;
        this.eventPlanners = eventPlanners != null ? eventPlanners : new ArrayList<>();
        this.eventVersion = eventVersion;
    }



    private int eventId;
    private String eventArtistName;
    private String eventName;
    private String eventVenue;

    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public LocalDateTime getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(LocalDateTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    private LocalDateTime eventStartTime;  // import java.time.LocalDateTime;
    private LocalDateTime eventEndTime;
    private int eventPermission;

    public int getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(int eventVersion) {
        this.eventVersion = eventVersion;
    }

    private int eventVersion;

    public List<EventPlanner> getEventPlanners() {
        return eventPlanners;
    }

    public void setEventPlanners(List<EventPlanner> eventPlanners) {
        this.eventPlanners = eventPlanners;
    }

    private List<EventPlanner> eventPlanners;
    // getters and setters


    public Event(int eventId, int eventPermission) {
        this.eventId = eventId;
        this.eventPermission = eventPermission;
    }
}

