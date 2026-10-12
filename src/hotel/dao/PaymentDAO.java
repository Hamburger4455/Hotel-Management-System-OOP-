package hotel.dao;

import hotel.model.Payment;
import hotel.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentDAO {

    Payment findById(int paymentId);

    List<Payment> findByInvoiceId(int invoiceId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findAll();

    void save(Payment payment);

    void update(Payment payment);

    void delete(int paymentId);

    BigDecimal sumSuccessfulAmountByInvoice(int invoiceId);
}
