package hotel.ui_controller;

import hotel.model.Guest;
import hotel.service.GuestService;
import hotel.service_implementation.GuestServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GuestFormController {

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtPhone;

    private final GuestService guestService = new GuestServiceImpl();

    // null = add mode, not null = edit mode
    private Guest editingGuest;

    //Call this from GuestListController when editing
    public void setGuestToEdit(Guest guest) {
        this.editingGuest = guest;

        if (guest != null) {
            txtFullName.setText(guest.getFullName());
            txtPhone.setText(guest.getPhone());
        }
    }

    @FXML
    private void onSave() {
        String name = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation error", "Full name is required.");
            return;
        }

        try {
            if (editingGuest == null) {
                // ADD mode
                Guest g = guestService.createGuest(name, phone);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Guest created with ID: " + g.getGuestId());
            } else {
                // EDIT mode
                editingGuest.setFullName(name);
                editingGuest.setPhone(phone);
                guestService.updateGuest(editingGuest);
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Guest updated successfully.");
            }

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to save guest: " + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtFullName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
