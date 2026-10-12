package hotel.service;

import hotel.model.Payment;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    Payment getPaymentById(int paymentId);

    List<Payment> getPaymentsByInvoice(int invoiceId);

    List<Payment> getPaymentsByStatus(PaymentStatus status);

    List<Payment> getAllPayments();

    /*
     * Record a payment attempt. If status is SUCCESS and
     * total successful payments >= invoice total, invoice will be marked paid.
     */
    void recordPayment(int invoiceId, BigDecimal amount,
                          PaymentMethod method, PaymentStatus status);

    void updatePaymentStatus(int paymentId, PaymentStatus status);

    void deletePayment(int paymentId);

    void updatePayment(Payment payment);
}
