package hotel.service_implementation;

import hotel.dao.ReceptionistDAO;
import hotel.dao_implementation.ReceptionistDaoImpl;
import hotel.model.Receptionist;
import hotel.service.ReceptionistService;

public class ReceptionistServiceImpl implements ReceptionistService {

    private static final String FIXED_PASSWORD = "1234";

    private final ReceptionistDAO receptionistDao = new ReceptionistDaoImpl();

    @Override
    public Receptionist authenticate(int staffId, String password) {
        if (!FIXED_PASSWORD.equals(password)) {
            return null;
        }

        return receptionistDao.findById(staffId);
    }
}
