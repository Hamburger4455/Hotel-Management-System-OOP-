package hotel.service_implementation;

import hotel.dao.GuestDAO;
import hotel.dao_implementation.GuestDaoImpl;
import hotel.dao.ReservationDAO;
import hotel.dao_implementation.ReservationDaoImpl;
import hotel.model.Guest;
import hotel.service.GuestService;

import java.util.List;

public class GuestServiceImpl implements GuestService {

    private final GuestDAO guestDao = new GuestDaoImpl();
    private final ReservationDAO reservationDao = new ReservationDaoImpl();


    @Override
    public Guest getGuestById(int id) {
        return guestDao.findById(id);
    }

    @Override
    public List<Guest> getAllGuests() {
        return guestDao.findAll();
    }

    @Override
    public List<Guest> searchGuestsByName(String keyword) {
        return guestDao.findByName(keyword);
    }

    @Override
    public Guest createGuest(String fullName, String phone) {
        Guest g = new Guest();
        g.setFullName(fullName);
        g.setPhone(phone);
        guestDao.save(g);
        return g;
    }

    @Override
    public void updateGuest(Guest guest) {
        guestDao.update(guest);
    }

    @Override
    public void deleteGuest(int guestId) {
        guestDao.delete(guestId);
    }

    @Override
    public boolean canDeleteGuest(int guestId) {
        int count = reservationDao.countByGuestId(guestId);
        return count == 0;
    }

}
