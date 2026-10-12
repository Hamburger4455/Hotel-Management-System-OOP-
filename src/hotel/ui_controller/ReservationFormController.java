package hotel.ui_controller;

import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.Guest;
import hotel.service.ReservationService;
import hotel.service.RoomService;
import hotel.service_implementation.ReservationServiceImpl;
import hotel.service_implementation.RoomServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ReservationFormController {

    @FXML
    private TextField txtGuestId;

    @FXML
    private ComboBox<Room> cbRoom;

    @FXML
    private DatePicker dpCheckIn;

    @FXML
    private DatePicker dpCheckOut;

    private final RoomService roomService = new RoomServiceImpl();
    private final ReservationService reservationService = new ReservationServiceImpl();
    private Reservation editingReservation = null;

    public void setReservationToEdit(Reservation reservation) {
        this.editingReservation = reservation;

        // Pre-fill fields
        txtGuestId.setText(String.valueOf(reservation.getGuest().getGuestId()));
        cbRoom.setValue(reservation.getRoom());
        dpCheckIn.setValue(reservation.getCheckInDate());
        dpCheckOut.setValue(reservation.getCheckOutDate());
    }

    @FXML
    private void initialize() {
        // Load available rooms into ComboBox
        List<Room> availableRooms = roomService.getAvailableRooms();
        cbRoom.setItems(FXCollections.observableArrayList(availableRooms));

        // How rooms are displayed in the dropdown
        cbRoom.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Room " + item.getRoomId() + " (" + item.getRoomType() + ")");
                }
            }
        });
        cbRoom.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Room " + item.getRoomId() + " (" + item.getRoomType() + ")");
                }
            }
        });
    }

    @FXML
    private void onSave() {
        try {
            int guestId = Integer.parseInt(txtGuestId.getText().trim());
            Room selectedRoom = cbRoom.getValue();
            LocalDate checkIn = dpCheckIn.getValue();
            LocalDate checkOut = dpCheckOut.getValue();

            if (selectedRoom == null || checkIn == null || checkOut == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Please fill all fields.");
                return;
            }

            if (editingReservation != null) {
                // edit mode
                // Update existing reservation object
                if (editingReservation.getGuest() == null) {
                    editingReservation.setGuest(new Guest());
                }
                editingReservation.getGuest().setGuestId(guestId);
                editingReservation.setRoom(selectedRoom);
                editingReservation.setCheckInDate(checkIn);
                editingReservation.setCheckOutDate(checkOut);

                reservationService.updateReservation(editingReservation);

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Reservation updated successfully.");

            } else {
                // create mode
                Reservation reservation = reservationService.createReservation(
                        guestId,
                        selectedRoom.getRoomId(),
                        checkIn,
                        checkOut
                );

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Reservation created with ID: " + reservation.getReservationId());
            }

            closeWindow();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid input",
                    "Guest ID must be a number.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                    "An unexpected error occurred: " + e.getMessage());
        }
    }


    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtGuestId.getScene().getWindow();
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
