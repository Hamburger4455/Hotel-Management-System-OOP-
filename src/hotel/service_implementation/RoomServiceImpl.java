package hotel.service_implementation;

import hotel.dao.RoomDAO;
import hotel.dao_implementation.RoomDaoImpl;
import hotel.model.Room;
import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;
import hotel.service.RoomService;

import java.util.List;

public class RoomServiceImpl implements RoomService {

    private final RoomDAO roomDao = new RoomDaoImpl();

    @Override
    public Room getRoomById(int roomId) {
        return roomDao.findById(roomId);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomDao.findAll();
    }

    @Override
    public List<Room> getRoomsByType(RoomType type) {
        return roomDao.findByType(type);
    }

    @Override
    public List<Room> getRoomsByStatus(RoomStatus status) {
        return roomDao.findByStatus(status);
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomDao.findAvailableRooms();
    }

    @Override
    public void updateRoom(Room room) {
        roomDao.update(room);
    }

    @Override
    public void updateRoomStatus(int roomId, RoomStatus status) {
        Room room = roomDao.findById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
        room.setStatus(status);
        roomDao.update(room);
    }
}
