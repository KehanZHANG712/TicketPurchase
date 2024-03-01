package domain;

import java.util.ArrayList;
import java.util.List;

public class EventPlanner extends User {
    private List<Event> createdEvents;

    public EventPlanner(String userName, String password, String email) {
        super(userName, password, 3, email);
        this.createdEvents = new ArrayList<>();
    }

    public EventPlanner(int userId, String userName, String password, String email) {
        super(userId, userName, password, 3, email);
        this.createdEvents = new ArrayList<>();
    }

    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(List<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public void addEvent(Event event) {
        this.createdEvents.add(event);
    }

    public void removeEvent(Event event) {
        this.createdEvents.remove(event);
    }
}

