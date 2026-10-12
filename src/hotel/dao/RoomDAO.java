package hotel.dao;

import hotel.model.Room;
import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;

import java.util.List;

public interface RoomDAO {

    Room findById(int roomId);

    List<Room> findAll();

    List<Room> findByType(RoomType type);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findAvailableRooms();

    void save(Room room);

    void update(Room room);

    void delete(int roomId);
}
