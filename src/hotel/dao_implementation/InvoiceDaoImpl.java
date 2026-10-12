package hotel.dao_implementation;

import hotel.dao.InvoiceDAO;
import hotel.model.Invoice;
import hotel.model.Reservation;
import hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDaoImpl implements InvoiceDAO {

    @Override
    public Invoice findById(int invoiceId) {
        String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToInvoice(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Invoice findByReservationId(int reservationId) {
        String sql = "SELECT * FROM invoice WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToInvoice(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoice";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Invoice invoice) {
        String sql = "INSERT INTO invoice (reservation_id, subtotal, tax, total, is_paid) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getReservation().getReservationId());
            ps.setBigDecimal(2, invoice.getSubtotal());
            ps.setBigDecimal(3, invoice.getTax());
            ps.setBigDecimal(4, invoice.getTotal());
            ps.setBoolean(5, invoice.isPaid());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                invoice.setInvoiceId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Invoice invoice) {
        String sql = "UPDATE invoice SET reservation_id = ?, subtotal = ?, tax = ?, " +
                "total = ?, is_paid = ? WHERE invoice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoice.getReservation().getReservationId());
            ps.setBigDecimal(2, invoice.getSubtotal());
            ps.setBigDecimal(3, invoice.getTax());
            ps.setBigDecimal(4, invoice.getTotal());
            ps.setBoolean(5, invoice.isPaid());
            ps.setInt(6, invoice.getInvoiceId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int invoiceId) {
        String sql = "DELETE FROM invoice WHERE invoice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();

        inv.setInvoiceId(rs.getInt("invoice_id"));

        Reservation res = new Reservation();
        res.setReservationId(rs.getInt("reservation_id"));
        inv.setReservation(res);

        inv.setSubtotal(rs.getBigDecimal("subtotal"));
        inv.setTax(rs.getBigDecimal("tax"));
        inv.setTotal(rs.getBigDecimal("total"));
        inv.setPaid(rs.getBoolean("is_paid"));

        return inv;
    }

    @Override
    public void updatePaidStatus(int invoiceId, boolean isPaid) {
        String sql = "UPDATE invoice SET is_paid = ? WHERE invoice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isPaid ? 1 : 0);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
