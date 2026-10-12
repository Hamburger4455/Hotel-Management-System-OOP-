package hotel.ui_controller;

import hotel.model.Invoice;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.RoomStatus;
import hotel.service.InvoiceService;
import hotel.service.ReservationService;
import hotel.service.RoomService;
import hotel.service_implementation.InvoiceServiceImpl;
import hotel.service_implementation.ReservationServiceImpl;
import hotel.service_implementation.RoomServiceImpl;
import hotel.ui.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblTotalReservations;

    @FXML
    private Label lblUpcomingReservations;

    @FXML
    private Label lblOccupiedRooms;

    @FXML
    private Label lblAvailableRooms;

    @FXML
    private Label lblPaidRevenue;

    @FXML
    private Label lblUnpaidInvoices;

    @FXML
    private Label lblCheckinsToday;

    @FXML
    private Label lblCheckoutsToday;

    private final ReservationService reservationService = new ReservationServiceImpl();
    private final RoomService roomService = new RoomServiceImpl();
    private final InvoiceService invoiceService = new InvoiceServiceImpl();

    @FXML
    private void initialize() {
        if (MainApp.getLoggedInUser() != null) {
            lblWelcome.setText("Welcome, " + MainApp.getLoggedInUser().getFullName());
        } else {
            lblWelcome.setText("Dashboard");
        }
        loadStats();
    }

    private void loadStats() {
        LocalDate today = LocalDate.now();

        // Reservations
        List<Reservation> reservations = reservationService.getAllReservations();
        lblTotalReservations.setText(String.valueOf(reservations.size()));

        long upcoming = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING
                        || r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> !r.getCheckInDate().isBefore(today))   // today or future
                .count();
        lblUpcomingReservations.setText(String.valueOf(upcoming));

        long checkinsToday = reservations.stream()
                .filter(r -> r.getCheckInDate().equals(today))
                .count();
        long checkoutsToday = reservations.stream()
                .filter(r -> r.getCheckOutDate().equals(today))
                .count();
        lblCheckinsToday.setText(String.valueOf(checkinsToday));
        lblCheckoutsToday.setText(String.valueOf(checkoutsToday));

        // Rooms
        List<Room> rooms = roomService.getAllRooms();
        long occupied = rooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                .count();
        long available = rooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .count();
        lblOccupiedRooms.setText(occupied + " / " + rooms.size());
        lblAvailableRooms.setText(String.valueOf(available));

        // Invoices
        List<Invoice> invoices = invoiceService.getAllInvoices();
        BigDecimal paidSum = invoices.stream()
                .filter(Invoice::isPaid)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long unpaidCount = invoices.stream()
                .filter(inv -> !inv.isPaid())
                .count();

        lblPaidRevenue.setText(paidSum.toString());
        lblUnpaidInvoices.setText(String.valueOf(unpaidCount));
    }


    @FXML
    private void onNewReservationClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("reservation_form.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("New Reservation");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();

            loadStats(); // refresh after adding
        } catch (IOException e) {
            showError("Failed to open reservation form: " + e.getMessage());
        }
    }

    @FXML
    private void onOpenReservationsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("main_window.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Reservations");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(true);
            stage.showAndWait(); // wait until the reservation window is closed

            loadStats(); // refresh dashboard after reservation is closed

        } catch (IOException e) {
            showError("Failed to open reservations screen: " + e.getMessage());
        }
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
            loadStats();
        } catch (IOException e) {
            showError("Failed to open guest list: " + e.getMessage());
        }
    }

    @FXML
    private void onOpenRoomList() {
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
            loadStats();
        } catch (IOException e) {
            showError("Failed to open room list: " + e.getMessage());
        }
    }

    @FXML
    private void onOpenBilling() {
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
            loadStats();
        } catch (IOException e) {
            showError("Failed to open billing screen: " + e.getMessage());
        }
    }

    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage) lblWelcome.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void onRefreshClicked(){
        loadStats();
    }

    @FXML
    private void onLogoutClicked() {
        try {
            MainApp.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to return to login: " + e.getMessage()).showAndWait();
        }
    }
}
