package hotel.ui_controller;

import hotel.model.Room;
import hotel.model.enums.RoomStatus;
import hotel.model.enums.RoomType;
import hotel.service.RoomService;
import hotel.service_implementation.RoomServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class RoomListController {

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TableColumn<Room, Integer> colId;

    @FXML
    private TableColumn<Room, String> colType;

    @FXML
    private TableColumn<Room, String> colStatus;

    @FXML
    private TableColumn<Room, BigDecimal> colRate;

    @FXML
    private ComboBox<RoomType> cbTypeFilter;

    @FXML
    private ComboBox<RoomStatus> cbStatusFilter;

    private final RoomService roomService = new RoomServiceImpl();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRoomId()).asObject()
        );
        colType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoomType().name())
        );
        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().name())
        );
        colRate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getRatePerNight())
        );

        cbTypeFilter.setItems(FXCollections.observableArrayList(RoomType.values()));
        cbStatusFilter.setItems(FXCollections.observableArrayList(RoomStatus.values()));

        loadAllRooms();
    }

    private void loadAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        roomTable.setItems(FXCollections.observableArrayList(rooms));
    }

    @FXML
    private void onApplyFilterClicked() {
        RoomType type = cbTypeFilter.getValue();
        RoomStatus status = cbStatusFilter.getValue();

        if (type == null && status == null) {
            loadAllRooms();
            return;
        }

        if (type != null && status == null) {
            roomTable.setItems(FXCollections.observableArrayList(
                    roomService.getRoomsByType(type)
            ));
        } else if (type == null) {
            roomTable.setItems(FXCollections.observableArrayList(
                    roomService.getRoomsByStatus(status)
            ));
        } else {
            // both type and status selected: filter manually
            List<Room> byType = roomService.getRoomsByType(type);
            List<Room> filtered = byType.stream()
                    .filter(r -> r.getStatus() == status)
                    .toList();
            roomTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void onClearFilterClicked() {
        cbTypeFilter.setValue(null);
        cbStatusFilter.setValue(null);
        loadAllRooms();
    }

    private Room getSelectedRoomOrWarn() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a room first.");
            return null;
        }
        return selected;
    }

    @FXML
    private void onSetAvailableClicked() {
        Room selected = getSelectedRoomOrWarn();
        if (selected == null) return;
        updateRoomStatus(selected, RoomStatus.AVAILABLE);
    }

    @FXML
    private void onSetMaintenanceClicked() {
        Room selected = getSelectedRoomOrWarn();
        if (selected == null) return;

        updateRoomStatus(selected, RoomStatus.MAINTENANCE);
    }

    @FXML
    private void onSetOccupiedClicked() {
        Room selected = getSelectedRoomOrWarn();
        if (selected == null) return;

        updateRoomStatus(selected, RoomStatus.OCCUPIED);
    }

    private void updateRoomStatus(Room room, RoomStatus newStatus) {
        room.setStatus(newStatus);
        roomService.updateRoom(room);
        showInfo("Room " + room.getRoomId() + " status set to " + newStatus);
        loadAllRooms();
    }

    @FXML
    private void onEditRateClicked() {
        Room selected = getSelectedRoomOrWarn();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(
                selected.getRatePerNight() != null
                        ? selected.getRatePerNight().toString()
                        : ""
        );
        dialog.setTitle("Edit Rate");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new rate per night:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        try {
            BigDecimal newRate = new BigDecimal(result.get().trim());
            if (newRate.compareTo(BigDecimal.ZERO) <= 0) {
                showWarning("Rate must be positive.");
                return;
            }

            selected.setRatePerNight(newRate);
            roomService.updateRoom(selected);
            showInfo("Rate updated for room " + selected.getRoomId());
            loadAllRooms();

        } catch (NumberFormatException e) {
            showWarning("Invalid number format.");
        }
    }

    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage) roomTable.getScene().getWindow();
        stage.close();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
