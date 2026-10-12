package hotel.model;

import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private RoomType roomType;
    private RoomStatus status;
    private BigDecimal ratePerNight;

    public Room() {}

    public Room(int roomId, RoomType roomType, RoomStatus status, BigDecimal ratePerNight) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.status = status;
        this.ratePerNight = ratePerNight;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public boolean isAvailable() {
        return RoomStatus.AVAILABLE.equals(status);
    }

    @Override
    public String toString() {
        return "Room " + roomId;
    }

}
