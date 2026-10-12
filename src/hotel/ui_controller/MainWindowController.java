package hotel.ui_controller;

import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.enums.ReservationStatus;
import hotel.service.ReservationService;
import hotel.service_implementation.ReservationServiceImpl;
import hotel.ui.MainApp;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MainWindowController {

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, Integer> colId;

    @FXML
    private TableColumn<Reservation, Integer> colGuestId;

    @FXML
    private TableColumn<Reservation, Integer> colRoomId;

    @FXML
    private TableColumn<Reservation, String> colStatus;

    @FXML
    private TableColumn<Reservation, LocalDate> colCheckIn;

    @FXML
    private TableColumn<Reservation, LocalDate> colCheckOut;

    @FXML
    private TableColumn<Reservation, String> colRoomStatus;

    // filter controls
    @FXML private TextField txtGuest;
    @FXML private TextField txtRoom;
    @FXML private ComboBox<ReservationStatus> cbStatus;
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    private final ReservationService reservationService = new ReservationServiceImpl();

    @FXML
    private void initialize() {
        // table column bindings
        colId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        colGuestId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        safeGetGuestId(cellData.getValue())
                ).asObject()
        );

        colRoomId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        safeGetRoomId(cellData.getValue())
                ).asObject()
        );

        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        safeGetReservationStatusName(cellData.getValue())
                )
        );

        colRoomStatus.setCellValueFactory(cd -> {
            Room room = cd.getValue().getRoom();
            String text = "";
            if (room != null && room.getStatus() != null) {
                text = room.getStatus().name();
            }
            return new SimpleStringProperty(text);
        });

        // filter combo setup
        cbStatus.getItems().clear();
        cbStatus.getItems().add(null); // allow blank selection (no filter)
        cbStatus.getItems().addAll(ReservationStatus.values());

        cbStatus.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ReservationStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
            }
        });

        cbStatus.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ReservationStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
            }
        });

        // load data initially
        loadReservations();
    }

    private int safeGetGuestId(Reservation r) {
        if (r == null || r.getGuest() == null) return 0;
        return r.getGuest().getGuestId();
    }

    private int safeGetRoomId(Reservation r) {
        if (r == null || r.getRoom() == null) return 0;
        return r.getRoom().getRoomId();
    }

    private String safeGetReservationStatusName(Reservation r) {
        if (r == null || r.getStatus() == null) return "";
        return r.getStatus().name();
    }

    @FXML
    private void onRefreshClicked() {
        loadReservations();
    }

    @FXML
    private void onNewReservationClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("reservation_form.fxml")
            );

            if (loader.getLocation() == null) {
                System.out.println("!!! FXML resource not found: reservation_form.fxml");
                showError("Cannot load reservation form FXML. Check file location and name.");
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("New Reservation");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();

            // After closing the form, refresh table
            loadReservations();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open reservation form: " + e.getMessage());
        }
    }

    private void loadReservations() {
        List<Reservation> list = reservationService.getAllReservations();
        reservationTable.setItems(FXCollections.observableArrayList(list));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onCheckInClicked() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation first.");
            return;
        }

        try {
            reservationService.checkIn(selected.getReservationId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guest checked in successfully.");
            loadReservations();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onCheckOutClicked() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation first.");
            return;
        }

        try {
            reservationService.checkOut(selected.getReservationId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Guest checked out successfully.");
            loadReservations();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onFilter() {

        String guestName = txtGuest.getText();
        if (guestName != null) guestName = guestName.trim();
        if (guestName != null && guestName.isEmpty()) guestName = null;

        Integer roomId = null;
        String rt = txtRoom.getText();
        if (rt != null) rt = rt.trim();
        if (rt != null && !rt.isEmpty()) {
            try {
                roomId = Integer.parseInt(rt);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Room ID must be a number.");
                return;
            }
        }

        ReservationStatus status = cbStatus.getValue();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();

        List<Reservation> results = reservationService.searchReservations(
                guestName, roomId, status, from, to
        );

        reservationTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    private void onClearFilters() {
        txtGuest.clear();
        txtRoom.clear();
        cbStatus.setValue(null);
        dpFrom.setValue(null);
        dpTo.setValue(null);
        loadReservations();
    }

    @FXML
    private void onOpenGuestList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("guest_list.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Guest List");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Failed to open guest list: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onOpenRoomsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("room_list.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Room Management");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open room list: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onOpenBillingClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("billing_window.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Billing");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelReservationClicked() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel this reservation?");
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        try {
            reservationService.cancelReservation(selected.getReservationId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation cancelled.");
            loadReservations();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void onOpenDashboardClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("dashboard.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Dashboard");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to open dashboard: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onEditReservationClicked() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a reservation to edit.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("reservation_form.fxml")
            );
            Parent root = loader.load(); // IMPORTANT: load before getController

            ReservationFormController controller = loader.getController();
            controller.setReservationToEdit(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Reservation");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            // refresh table after edit
            loadReservations();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to open reservation form: " + e.getMessage()).showAndWait();
        }
    }
}
