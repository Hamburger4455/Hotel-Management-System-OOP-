package hotel.model;

public class Receptionist extends Person {
    private int staffId;
    private String shift;

    public Receptionist() {
        super();
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
