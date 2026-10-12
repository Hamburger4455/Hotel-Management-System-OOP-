package hotel.dao_implementation;

import hotel.dao.ReceptionistDAO;
import hotel.model.Receptionist;
import hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDaoImpl implements ReceptionistDAO {

    @Override
    public Receptionist findById(int staffId) {
        String sql = "SELECT * FROM receptionist WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToReceptionist(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Receptionist> findAll() {
        List<Receptionist> list = new ArrayList<>();
        String sql = "SELECT * FROM receptionist";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToReceptionist(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Receptionist receptionist) {
        String sql = "INSERT INTO receptionist (full_name, shift) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, receptionist.getFullName());
            ps.setString(2, receptionist.getShift());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                receptionist.setStaffId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Receptionist receptionist) {
        String sql = "UPDATE receptionist SET full_name = ?, shift = ? WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, receptionist.getFullName());
            ps.setString(2, receptionist.getShift());
            ps.setInt(3, receptionist.getStaffId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int staffId) {
        String sql = "DELETE FROM receptionist WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Receptionist mapRowToReceptionist(ResultSet rs) throws SQLException {
        Receptionist r = new Receptionist();
        r.setStaffId(rs.getInt("staff_id"));
        r.setFullName(rs.getString("full_name"));
        r.setShift(rs.getString("shift"));
        return r;
    }
}
