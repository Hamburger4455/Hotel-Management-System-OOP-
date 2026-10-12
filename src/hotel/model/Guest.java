package hotel.model;

public class Guest extends Person {
    private int guestId;
    private String phone;

    public Guest() {
        super();
    }

    public Guest(int guestId, String fullName, String phone) {
        super(fullName);
        this.guestId = guestId;
        this.phone = phone;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
