package hotel.service;

import hotel.model.Reservation;
import hotel.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    List<Reservation> getAllReservations();

    List<Reservation> getReservationsByStatus(ReservationStatus status);

    List<Reservation> getReservationsByDateRange(LocalDate from, LocalDate to);

    Reservation createReservation(int guestId, int roomId,
                                  LocalDate checkIn, LocalDate checkOut);

    List<Reservation> searchReservations(
            String guestName,
            Integer roomId,
            ReservationStatus status,
            LocalDate from,
            LocalDate to
    );

    void cancelReservation(int reservationId);

    void checkIn(int reservationId);

    void checkOut(int reservationId);

    void updateReservation(Reservation reservation);
}
