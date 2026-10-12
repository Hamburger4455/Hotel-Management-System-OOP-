package hotel.ui_controller;

import hotel.model.Guest;
import hotel.service.GuestService;
import hotel.service_implementation.GuestServiceImpl;
import hotel.ui.MainApp;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GuestListController {

    @FXML
    private TableView<Guest> guestTable;

    @FXML
    private TableColumn<Guest, Integer> colId;

    @FXML
    private TableColumn<Guest, String> colName;

    @FXML
    private TableColumn<Guest, String> colPhone;

    @FXML
    private TextField txtSearch;

    private final GuestService guestService = new GuestServiceImpl();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getGuestId()).asObject()
        );
        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName())
        );
        colPhone.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPhone())
        );

        loadAllGuests();
    }

    private void loadAllGuests() {
        List<Guest> guests = guestService.getAllGuests();
        guestTable.setItems(FXCollections.observableArrayList(guests));
    }

    @FXML
    private void onSearchClicked() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllGuests();
        } else {
            List<Guest> guests = guestService.searchGuestsByName(keyword);
            guestTable.setItems(FXCollections.observableArrayList(guests));
        }
    }

    @FXML
    private void onShowAllClicked() {
        txtSearch.clear();
        loadAllGuests();
    }

    @FXML
    private void onAddClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("guest_form.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Add Guest");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();

            loadAllGuests();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open guest form: " + e.getMessage());
        }
    }

    @FXML
    private void onEditClicked() {
        Guest selected = guestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a guest to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("guest_form.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Edit Guest");

            MainApp.applyAppIcon(stage);
            stage.initOwner(MainApp.getPrimaryStage());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            // Get controller and pass selected guest
            GuestFormController controller = loader.getController();
            controller.setGuestToEdit(selected);

            stage.showAndWait();
            loadAllGuests();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open guest form for edit: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteClicked() {
        Guest selected = guestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a guest to delete.");
            return;
        }

        // check if guest has reservations
        if (!guestService.canDeleteGuest(selected.getGuestId())) {
            showWarning("This guest has existing reservations and cannot be deleted.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete guest: " + selected.getFullName() + " ?");
        Optional<ButtonType> choice = confirm.showAndWait();

        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            guestService.deleteGuest(selected.getGuestId());
            loadAllGuests();
        }
    }

    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage) guestTable.getScene().getWindow();
        stage.close();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
