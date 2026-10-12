package hotel.dao_implementation;

import hotel.dao.RoomDAO;
import hotel.model.Room;
import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;
import hotel.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDaoImpl implements RoomDAO {

    @Override
    public Room findById(int roomId) {
        String sql = "SELECT * FROM room WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Room> findAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM room";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Room> findByType(RoomType type) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE room_type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Room> findAvailableRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = 'AVAILABLE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Room room) {
        String sql = "INSERT INTO room (room_type, status, rate_per_night) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, room.getRoomType().name());
            ps.setString(2, room.getStatus().name());
            ps.setBigDecimal(3, room.getRatePerNight());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                room.setRoomId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE room SET room_type = ?, status = ?, rate_per_night = ? " +
                "WHERE room_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomType().name());
            ps.setString(2, room.getStatus().name());
            ps.setBigDecimal(3, room.getRatePerNight());
            ps.setInt(4, room.getRoomId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int roomId) {
        String sql = "DELETE FROM room WHERE room_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Room mapRowToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomType(RoomType.valueOf(rs.getString("room_type")));
        room.setStatus(RoomStatus.valueOf(rs.getString("status")));
        room.setRatePerNight(rs.getBigDecimal("rate_per_night"));
        return room;
    }
}
