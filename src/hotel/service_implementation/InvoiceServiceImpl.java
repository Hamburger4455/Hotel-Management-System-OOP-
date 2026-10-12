package hotel.service_implementation;

import hotel.dao.InvoiceDAO;
import hotel.dao.ReservationDAO;
import hotel.dao_implementation.InvoiceDaoImpl;
import hotel.dao_implementation.ReservationDaoImpl;
import hotel.model.Invoice;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.service.InvoiceService;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceDAO invoiceDao = new InvoiceDaoImpl();
    private final ReservationDAO reservationDao = new ReservationDaoImpl();

    @Override
    public Invoice getInvoiceById(int invoiceId) {
        return invoiceDao.findById(invoiceId);
    }

    @Override
    public Invoice getInvoiceByReservationId(int reservationId) {
        return invoiceDao.findByReservationId(reservationId);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceDao.findAll();
    }

    /*
     * Automatically create an invoice for the given reservation
     * using room rate * nights and tax rate
     * If an invoice already exists for this reservation, it is returned
     */
    @Override
    public Invoice createInvoiceForReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation must not be null.");
        }
        if (reservation.getReservationId() == 0) {
            throw new IllegalArgumentException("Reservation must be saved (id > 0) before invoicing.");
        }

        // If there is already an invoice, return it
        Invoice existing = invoiceDao.findByReservationId(reservation.getReservationId());
        if (existing != null) {
            return existing;
        }

        // Make sure we have full reservation info (room, dates)
        Reservation res = reservation;
        if (res.getRoom() == null || res.getCheckInDate() == null || res.getCheckOutDate() == null) {
            res = reservationDao.findById(reservation.getReservationId());
            if (res == null) {
                throw new IllegalStateException("Reservation not found in DB: " + reservation.getReservationId());
            }
        }

        Room room = res.getRoom();
        if (room == null || room.getRatePerNight() == null) {
            throw new IllegalStateException("Reservation has no room / room rate information.");
        }

        long nights = ChronoUnit.DAYS.between(res.getCheckInDate(), res.getCheckOutDate());
        if (nights <= 0) {
            nights = 1;
        }

        BigDecimal rate = room.getRatePerNight();
        BigDecimal subtotal = rate.multiply(BigDecimal.valueOf(nights));

        //tax rate: 10%
        BigDecimal taxRate = new BigDecimal("0.10");

        return createOrUpdateInvoice(res.getReservationId(), subtotal, taxRate);
    }

    @Override
    public Invoice createOrUpdateInvoice(int reservationId, BigDecimal subtotal, BigDecimal taxRate) {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found with id: " + reservationId);
        }

        if (subtotal == null) {
            throw new IllegalArgumentException("Subtotal cannot be null");
        }
        if (taxRate == null) {
            taxRate = BigDecimal.ZERO;
        }

        BigDecimal tax = subtotal.multiply(taxRate);
        BigDecimal total = subtotal.add(tax);

        Invoice existing = invoiceDao.findByReservationId(reservationId);
        if (existing == null) {
            // create new
            Invoice inv = new Invoice();
            inv.setReservation(reservation);
            inv.setSubtotal(subtotal);
            inv.setTax(tax);
            inv.setTotal(total);
            inv.setPaid(false);
            invoiceDao.save(inv);
            return inv;
        } else {
            // update existing
            existing.setSubtotal(subtotal);
            existing.setTax(tax);
            existing.setTotal(total);
            invoiceDao.update(existing);
            return existing;
        }
    }

    @Override
    public void markInvoicePaid(int invoiceId) {
        Invoice inv = invoiceDao.findById(invoiceId);
        if (inv != null) {
            inv.setPaid(true);
            invoiceDao.update(inv);
        }
    }

    @Override
    public void deleteInvoice(int invoiceId) {
        invoiceDao.delete(invoiceId);
    }
}
