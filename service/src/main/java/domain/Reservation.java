package domain;

public class Reservation {
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getReservationTicketId() {
        return reservationTicketId;
    }

    public void setReservationTicketId(int reservationTicketId) {
        this.reservationTicketId = reservationTicketId;
    }

    public int getReservationUserId() {
        return reservationUserId;
    }

    public void setReservationUserId(int reservationUserId) {
        this.reservationUserId = reservationUserId;
    }

    public int getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(int reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public int getReservationPrice() {
        return reservationPrice;
    }

    public void setReservationPrice(int reservationPrice) {
        this.reservationPrice = reservationPrice;
    }



    public Reservation(int reservationId, int reservationTicketId, int reservationUserId, int reservationNumber, int reservationPrice) {
        this.reservationId = reservationId;
        this.reservationTicketId = reservationTicketId;
        this.reservationUserId = reservationUserId;
        this.reservationNumber = reservationNumber;
        this.reservationPrice = reservationPrice;
    }

    public Reservation(int reservationTicketId, int reservationUserId, int reservationNumber, int reservationPrice) {
        this.reservationTicketId = reservationTicketId;
        this.reservationUserId = reservationUserId;
        this.reservationNumber = reservationNumber;
        this.reservationPrice = reservationPrice;
    }

    private int reservationId;
    private int reservationTicketId;
    private int reservationUserId;
    private int reservationNumber;

    private int reservationPrice;
    // getters and setters


    public Reservation(int reservationId, int reservationUserId) {
        this.reservationId = reservationId;
        this.reservationUserId = reservationUserId;
    }
}

