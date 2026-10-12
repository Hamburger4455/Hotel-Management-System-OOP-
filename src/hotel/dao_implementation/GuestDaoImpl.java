package hotel.dao_implementation;

import hotel.dao.GuestDAO;
import hotel.model.Guest;
import hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDaoImpl implements GuestDAO {

    @Override
    public Guest findById(int id) {
        String sql = "SELECT * FROM guest WHERE guest_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToGuest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guest";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToGuest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Guest> findByName(String nameKeyword) {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guest WHERE full_name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nameKeyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToGuest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Guest guest) {
        String sql = "INSERT INTO guest (full_name, phone) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, guest.getFullName());
            ps.setString(2, guest.getPhone());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                guest.setGuestId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Guest guest) {
        String sql = "UPDATE guest SET full_name = ?, phone = ? WHERE guest_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, guest.getFullName());
            ps.setString(2, guest.getPhone());
            ps.setInt(3, guest.getGuestId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int guestId) {
        String sql = "DELETE FROM guest WHERE guest_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Guest mapRowToGuest(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setFullName(rs.getString("full_name"));
        g.setPhone(rs.getString("phone"));
        return g;
    }
}
