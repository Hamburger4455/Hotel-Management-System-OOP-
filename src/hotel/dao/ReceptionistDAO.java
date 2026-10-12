package hotel.dao;

import hotel.model.Receptionist;

import java.util.List;

public interface ReceptionistDAO {

    Receptionist findById(int staffId);

    List<Receptionist> findAll();

    void save(Receptionist receptionist);

    void update(Receptionist receptionist);

    void delete(int staffId);
}
