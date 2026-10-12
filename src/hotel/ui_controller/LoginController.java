package hotel.ui_controller;

import hotel.model.Receptionist;
import hotel.service.ReceptionistService;
import hotel.service_implementation.ReceptionistServiceImpl;
import hotel.ui.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtStaffId;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    private final ReceptionistService receptionistService = new ReceptionistServiceImpl();

    @FXML
    private void onLogin() {
        lblMessage.setText("");

        String staffIdText = txtStaffId.getText().trim();
        String password = txtPassword.getText().trim();

        if (staffIdText.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Please enter staff ID and password.");
            return;
        }

        int staffId;
        try {
            staffId = Integer.parseInt(staffIdText);
        } catch (NumberFormatException e) {
            lblMessage.setText("Staff ID must be a number.");
            return;
        }

        Receptionist rec = receptionistService.authenticate(staffId, password);
        if (rec == null) {
            lblMessage.setText("Invalid staff ID or password.");
            return;
        }

        try {
            MainApp.showDashBoard(rec);
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Failed to open main window.");
        }
    }

    @FXML
    private void onExit() {
        // close current window
        txtStaffId.getScene().getWindow().hide();
    }
}
