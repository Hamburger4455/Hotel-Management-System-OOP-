package hotel.dao;

import hotel.model.Reservation;
import hotel.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {

    int countByGuestId(int guestId);

    Reservation findById(int id);

    List<Reservation> findAll();

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByDateRange(LocalDate from, LocalDate to);

    List<Reservation> searchReservations(
            String guestName,
            Integer roomId,
            ReservationStatus status,
            LocalDate from,
            LocalDate to
    );

    void save(Reservation reservation);

    void update(Reservation reservation);

    void delete(int reservationId);
}
