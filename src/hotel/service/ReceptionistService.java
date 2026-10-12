package hotel.service;

import hotel.model.Receptionist;

public interface ReceptionistService {

    /*
     * Returns the receptionist if staffId exists AND password matches "1234",
     * otherwise returns null.
     */
    Receptionist authenticate(int staffId, String password);
}
