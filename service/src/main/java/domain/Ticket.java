package domain;

public class Ticket {
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketVenue() {
        return ticketVenue;
    }

    public void setTicketVenue(String ticketVenue) {
        this.ticketVenue = ticketVenue;
    }

    public String getTicketEvent() {
        return ticketEvent;
    }

    public void setTicketEvent(String ticketEvent) {
        this.ticketEvent = ticketEvent;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTicketRest() {
        return ticketRest;
    }

    public void setTicketRest(int ticketRest) {
        this.ticketRest = ticketRest;
    }

    public Ticket(String ticketVenue, String ticketEvent, String ticketType, int ticketPrice, int ticketRest) {
        this.ticketVenue = ticketVenue;
        this.ticketEvent = ticketEvent;
        this.ticketType = ticketType;
        this.ticketPrice = ticketPrice;
        this.ticketRest = ticketRest;
    }

    public Ticket(int ticketId, String ticketVenue, String ticketEvent, String ticketType, int ticketPrice) {
        this.ticketId = ticketId;
        this.ticketVenue = ticketVenue;
        this.ticketEvent = ticketEvent;
        this.ticketType = ticketType;
        this.ticketPrice = ticketPrice;
    }

    public Ticket(int ticketId, String ticketVenue, String ticketEvent, String ticketType, int ticketPrice, int ticketRest) {
        this.ticketId = ticketId;
        this.ticketVenue = ticketVenue;
        this.ticketEvent = ticketEvent;
        this.ticketType = ticketType;
        this.ticketPrice = ticketPrice;
        this.ticketRest = ticketRest;
    }

    private int ticketId;
    private String ticketVenue;
    private String ticketEvent;
    private String ticketType;
    private int ticketPrice; // You can use BigDecimal if you prefer
    // getters and setters
    private int ticketRest;
    private int ticketVersion;

    public int getTicketVersion() {
        return ticketVersion;
    }

    public void setTicketVersion(int ticketVersion) {
        this.ticketVersion = ticketVersion;
    }

    public Ticket(int ticketId, String ticketVenue, String ticketEvent, String ticketType, int ticketPrice, int ticketRest, int ticketVersion) {
        this.ticketId = ticketId;
        this.ticketVenue = ticketVenue;
        this.ticketEvent = ticketEvent;
        this.ticketType = ticketType;
        this.ticketPrice = ticketPrice;
        this.ticketRest = ticketRest;
        this.ticketVersion = ticketVersion;
    }
}

