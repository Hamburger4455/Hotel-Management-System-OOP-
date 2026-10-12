package hotel.model;

import java.math.BigDecimal;

public class Invoice {
    private int invoiceId;
    private Reservation reservation;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private boolean paid;

    public Invoice() {}

    public Invoice(int invoiceId, Reservation reservation,
                   BigDecimal subtotal, BigDecimal tax,
                   BigDecimal total, boolean paid) {
        this.invoiceId = invoiceId;
        this.reservation = reservation;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.paid = paid;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
