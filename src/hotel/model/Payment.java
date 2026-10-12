package hotel.model;

import hotel.model.enums.PaymentMethod;
import hotel.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private Invoice invoice;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    public Payment() {}

    public Payment(int paymentId, Invoice invoice, BigDecimal amount,
                   PaymentMethod method, PaymentStatus status,
                   LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.invoice = invoice;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}
