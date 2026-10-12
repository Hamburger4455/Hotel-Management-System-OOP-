package hotel.dao;

import hotel.model.Guest;
import java.util.List;

public interface GuestDAO {

    Guest findById(int id);

    List<Guest> findAll();

    List<Guest> findByName(String nameKeyword);

    void save(Guest guest);

    void update(Guest guest);

    void delete(int guestId);
}
