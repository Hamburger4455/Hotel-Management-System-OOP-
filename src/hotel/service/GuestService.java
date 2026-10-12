package hotel.service;

import hotel.model.Guest;

import java.util.List;

public interface GuestService {

    boolean canDeleteGuest(int guestId);

    Guest getGuestById(int id);

    List<Guest> getAllGuests();

    List<Guest> searchGuestsByName(String keyword);

    Guest createGuest(String fullName, String phone);

    void updateGuest(Guest guest);

    void deleteGuest(int guestId);
}
