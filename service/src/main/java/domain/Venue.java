package domain;

public class Venue {
    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public int getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(int venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    public int getMoshCapacity() {
        return moshCapacity;
    }

    public void setMoshCapacity(int moshCapacity) {
        this.moshCapacity = moshCapacity;
    }

    public int getStandingCapacity() {
        return standingCapacity;
    }

    public void setStandingCapacity(int standingCapacity) {
        this.standingCapacity = standingCapacity;
    }

    public int getSeatedCapacity() {
        return seatedCapacity;
    }

    public void setSeatedCapacity(int seatedCapacity) {
        this.seatedCapacity = seatedCapacity;
    }

    public int getVipCapacity() {
        return vipCapacity;
    }

    public void setVipCapacity(int vipCapacity) {
        this.vipCapacity = vipCapacity;
    }

    public Venue(String venueAddress, int venueCapacity, int moshCapacity, int standingCapacity, int seatedCapacity, int vipCapacity) {

        this.venueAddress = venueAddress;
        this.venueCapacity = venueCapacity;
        this.moshCapacity = moshCapacity;
        this.standingCapacity = standingCapacity;
        this.seatedCapacity = seatedCapacity;
        this.vipCapacity = vipCapacity;
    }

    public Venue(int venueId, String venueAddress, int venueCapacity, int moshCapacity, int standingCapacity, int seatedCapacity, int vipCapacity) {
        this.venueId = venueId;
        this.venueAddress = venueAddress;
        this.venueCapacity = venueCapacity;
        this.moshCapacity = moshCapacity;
        this.standingCapacity = standingCapacity;
        this.seatedCapacity = seatedCapacity;
        this.vipCapacity = vipCapacity;
    }

    private int venueId;
    private String venueAddress;
    private int venueCapacity;
    private int moshCapacity;
    private int standingCapacity;
    private int seatedCapacity;
    private int vipCapacity;
    // getters and setters
}

