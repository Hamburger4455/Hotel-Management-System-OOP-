package hotel.service;

import hotel.model.Room;
import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;
import java.util.List;

public interface RoomService {

    Room getRoomById(int roomId);

    List<Room> getAllRooms();

    List<Room> getRoomsByType(RoomType type);

    List<Room> getRoomsByStatus(RoomStatus status);

    List<Room> getAvailableRooms();

    void updateRoom(Room room);

    void updateRoomStatus(int roomId, RoomStatus status);
}
