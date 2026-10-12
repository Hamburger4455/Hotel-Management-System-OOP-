package hotel.service_implementation;

import hotel.dao.InvoiceDAO;
import hotel.dao.PaymentDAO;
import hotel.dao_implementation.InvoiceDaoImpl;
import hotel.dao_implementation.PaymentDaoImpl;
import hotel.model.Invoice;
import hotel.model.Payment;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.PaymentStatus;
import hotel.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDao = new PaymentDaoImpl();
    private final InvoiceDAO invoiceDao = new InvoiceDaoImpl();

    @Override
    public Payment getPaymentById(int paymentId) {
        return paymentDao.findById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsByInvoice(int invoiceId) {
        return paymentDao.findByInvoiceId(invoiceId);
    }

    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentDao.findByStatus(status);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDao.findAll();
    }

    @Override
    public void recordPayment(int invoiceId,
                              BigDecimal amount,
                              PaymentMethod method,
                              PaymentStatus status) {

        Invoice invoice = invoiceDao.findById(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found with id: " + invoiceId);
        }

        if (invoice.isPaid()) {
            throw new IllegalStateException("Invoice is already fully paid.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        // Save payment
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(status);
        payment.setPaymentDate(LocalDateTime.now());

        paymentDao.save(payment);

        // Check if invoice become paid only when SUCCESS
        if (status == PaymentStatus.SUCCESS) {
            updateInvoicePaidStatus(invoice);
        }
    }

    @Override
    public void updatePaymentStatus(int paymentId, PaymentStatus status) {
        Payment payment = paymentDao.findById(paymentId);
        if (payment == null) return;

        payment.setStatus(status);
        paymentDao.update(payment);

        if (status == PaymentStatus.SUCCESS) {
            Invoice invoice = invoiceDao.findById(payment.getInvoice().getInvoiceId());
            if (invoice != null) {
                updateInvoicePaidStatus(invoice);
            }
        }
    }

    @Override
    public void deletePayment(int paymentId) {
        paymentDao.delete(paymentId);
    }

    // Recalculate paid amount and update invoice paid flag
    private void updateInvoicePaidStatus(Invoice invoice) {

        BigDecimal totalPaid = paymentDao.sumSuccessfulAmountByInvoice(invoice.getInvoiceId());
        BigDecimal total = invoice.getTotal();

        if (totalPaid.compareTo(total) >= 0 && !invoice.isPaid()) {
            invoice.setPaid(true);
            invoiceDao.updatePaidStatus(invoice.getInvoiceId(), true);
        }
    }

    @Override
    public void updatePayment(Payment payment) {
        // update payment row
        paymentDao.update(payment);

    }
}
