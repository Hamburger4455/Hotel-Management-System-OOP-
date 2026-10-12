package hotel.dao_implementation;

import hotel.dao.ReservationDAO;
import hotel.dao.RoomDAO;
import hotel.dao_implementation.RoomDaoImpl;
import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;
import hotel.model.Guest;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.enums.ReservationStatus;
import hotel.util.DBConnection;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDaoImpl implements ReservationDAO {

    private final RoomDAO roomDao = new RoomDaoImpl();

    @Override
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservation WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Reservation> findByDateRange(LocalDate from, LocalDate to) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation " +
                "WHERE check_in_date >= ? AND check_out_date <= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservation " +
                "(guest_id, room_id, check_in_date, check_out_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reservation.getGuest().getGuestId());
            ps.setInt(2, reservation.getRoom().getRoomId());
            ps.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            ps.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            ps.setString(5, reservation.getStatus().name());

            ps.executeUpdate();

            // get generated reservation_id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                reservation.setReservationId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservation SET " +
                "guest_id = ?, room_id = ?, check_in_date = ?, " +
                "check_out_date = ?, status = ? " +
                "WHERE reservation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservation.getGuest().getGuestId());
            ps.setInt(2, reservation.getRoom().getRoomId());
            ps.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            ps.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            ps.setString(5, reservation.getStatus().name());
            ps.setInt(6, reservation.getReservationId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int reservationId) {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));

        // get guest using ID
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        r.setGuest(g);

        // get full room from DB
        int roomId = rs.getInt("room_id");
        Room room = roomDao.findById(roomId);
        r.setRoom(room);

        r.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        r.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));

        return r;
    }

    @Override
    public int countByGuestId(int guestId) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE guest_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Reservation> searchReservations(String guestName,
                                                Integer roomId,
                                                ReservationStatus status,
                                                LocalDate from,
                                                LocalDate to) {

        List<Reservation> list = new ArrayList<>();

        String sql =
                "SELECT r.* FROM reservation r " +
                        "JOIN guest g ON r.guest_id = g.guest_id WHERE 1=1 ";

        if (guestName != null) sql += " AND g.full_name LIKE ? ";
        if (roomId != null)    sql += " AND r.room_id = ? ";
        if (status != null)    sql += " AND r.status = ? ";
        if (from != null)      sql += " AND r.check_in_date >= ? ";
        if (to != null)        sql += " AND r.check_out_date <= ? ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

            if (guestName != null) ps.setString(idx++, "%" + guestName + "%");
            if (roomId != null)    ps.setInt(idx++, roomId);
            if (status != null)    ps.setString(idx++, status.name());
            if (from != null)      ps.setDate(idx++, Date.valueOf(from));
            if (to != null)        ps.setDate(idx++, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
