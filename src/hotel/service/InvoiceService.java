package hotel.service;

import hotel.model.Invoice;
import hotel.model.Reservation;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {

    Invoice getInvoiceById(int invoiceId);

    Invoice getInvoiceByReservationId(int reservationId);

    List<Invoice> getAllInvoices();

    Invoice createInvoiceForReservation(Reservation reservation);

    Invoice createOrUpdateInvoice(int reservationId, BigDecimal subtotal, BigDecimal taxRate);

    void markInvoicePaid(int invoiceId);

    void deleteInvoice(int invoiceId);
}
