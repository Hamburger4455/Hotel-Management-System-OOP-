package hotel.service_implementation;

import hotel.dao.GuestDAO;
import hotel.dao.ReservationDAO;
import hotel.dao.RoomDAO;
import hotel.dao_implementation.GuestDaoImpl;
import hotel.dao_implementation.ReservationDaoImpl;
import hotel.dao_implementation.RoomDaoImpl;
import hotel.model.Guest;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.RoomStatus;
import hotel.service.InvoiceService;
import hotel.service.ReservationService;
import hotel.service.RoomService;

import java.time.LocalDate;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {

    private final ReservationDAO reservationDao = new ReservationDaoImpl();
    private final GuestDAO guestDao = new GuestDaoImpl();
    private final RoomDAO roomDao = new RoomDaoImpl();
    private final RoomService roomService = new RoomServiceImpl();
    private final InvoiceService invoiceService = new InvoiceServiceImpl();


    @Override
    public List<Reservation> getAllReservations() {
        return reservationDao.findAll();
    }

    @Override
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationDao.findByStatus(status);
    }

    @Override
    public List<Reservation> getReservationsByDateRange(LocalDate from, LocalDate to) {
        return reservationDao.findByDateRange(from, to);
    }

    @Override
    public Reservation createReservation(int guestId, int roomId,
                                         LocalDate checkIn, LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        Guest guest = guestDao.findById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found with id: " + guestId);
        }

        Room room = roomDao.findById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found with id: " + roomId);
        }
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Room " + roomId + " is not available");
        }

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setStatus(ReservationStatus.PENDING);

        reservationDao.save(reservation);
        //keep room status change for checkIn, not at booking

        invoiceService.createInvoiceForReservation(reservation);

        return reservation;
    }

    @Override
    public void cancelReservation(int reservationId) {
        Reservation res = reservationDao.findById(reservationId);
        if (res == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        if (res.getStatus() == ReservationStatus.CHECKED_IN ||
                res.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel an active or finished stay.");
        }

        res.setStatus(ReservationStatus.CANCELLED);
        reservationDao.update(res);

        //free room
        int roomId = res.getRoom().getRoomId();
        roomService.updateRoomStatus(roomId, RoomStatus.AVAILABLE);
    }

    @Override
    public void checkIn(int reservationId) {
        Reservation res = reservationDao.findById(reservationId);
        if (res == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        // only allow check-in from PENDING/CONFIRMED
        if (res.getStatus() != ReservationStatus.PENDING &&
                res.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot check in reservation with status: " + res.getStatus());
        }

        // update reservation
        res.setStatus(ReservationStatus.CHECKED_IN);
        reservationDao.update(res);

        // update room to OCCUPIED
        int roomId = res.getRoom().getRoomId();
        roomService.updateRoomStatus(roomId, RoomStatus.OCCUPIED);
    }

    @Override
    public void checkOut(int reservationId) {
        Reservation res = reservationDao.findById(reservationId);
        if (res == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        // only CHECKED_IN status can be checked out
        if (res.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Only CHECKED_IN reservations can be checked out. Current status: " + res.getStatus());
        }

        // update reservation
        res.setStatus(ReservationStatus.CHECKED_OUT);
        reservationDao.update(res);

        // update room to available
        int roomId = res.getRoom().getRoomId();
        roomService.updateRoomStatus(roomId, RoomStatus.AVAILABLE);
    }

    @Override
    public List<Reservation> searchReservations(String guestName,
                                                Integer roomId,
                                                ReservationStatus status,
                                                LocalDate from,
                                                LocalDate to) {
        return reservationDao.searchReservations(guestName, roomId, status, from, to);
    }

    @Override
    public void updateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }

        reservationDao.update(reservation);

    }

}
