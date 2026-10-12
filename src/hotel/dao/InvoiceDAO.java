package hotel.dao;

import hotel.model.Invoice;

import java.util.List;

public interface InvoiceDAO {

    Invoice findById(int invoiceId);

    Invoice findByReservationId(int reservationId);

    List<Invoice> findAll();

    void save(Invoice invoice);

    void update(Invoice invoice);

    void delete(int invoiceId);

    void updatePaidStatus(int invoiceId, boolean isPaid);
}
