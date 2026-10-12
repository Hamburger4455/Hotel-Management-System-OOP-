package hotel.dao_implementation;

import hotel.dao.PaymentDAO;
import hotel.model.Invoice;
import hotel.model.Payment;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.PaymentStatus;
import hotel.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDaoImpl implements PaymentDAO {

    @Override
    public Payment findById(int paymentId) {
        String sql = "SELECT * FROM payment WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, paymentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToPayment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> findByInvoiceId(int invoiceId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE invoice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Payment payment) {
        // Let DB default set payment_date if null
        String sql = "INSERT INTO payment (invoice_id, amount, method, status, payment_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getInvoice().getInvoiceId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getMethod().name());
            ps.setString(4, payment.getStatus().name());

            LocalDateTime dt = payment.getPaymentDate();
            if (dt == null) {
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                ps.setTimestamp(5, Timestamp.valueOf(dt));
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                payment.setPaymentId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Payment payment) {
        String sql = "UPDATE payment SET invoice_id = ?, amount = ?, method = ?, " +
                "status = ?, payment_date = ? WHERE payment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payment.getInvoice().getInvoiceId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getMethod().name());
            ps.setString(4, payment.getStatus().name());
            ps.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
            ps.setInt(6, payment.getPaymentId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, paymentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();

        p.setPaymentId(rs.getInt("payment_id"));

        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        p.setInvoice(inv);

        p.setAmount(rs.getBigDecimal("amount"));
        p.setMethod(PaymentMethod.valueOf(rs.getString("method")));
        p.setStatus(PaymentStatus.valueOf(rs.getString("status")));

        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) {
            p.setPaymentDate(ts.toLocalDateTime());
        }

        return p;
    }

    @Override
    public BigDecimal sumSuccessfulAmountByInvoice(int invoiceId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_paid " +
                "FROM payment WHERE invoice_id = ? AND status = 'SUCCESS'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_paid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
