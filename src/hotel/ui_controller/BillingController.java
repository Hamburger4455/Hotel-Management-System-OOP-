package hotel.ui_controller;

import hotel.model.Invoice;
import hotel.model.Payment;
import hotel.model.Reservation;
import hotel.model.Room;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.PaymentStatus;
import hotel.service.InvoiceService;
import hotel.service.PaymentService;
import hotel.service_implementation.InvoiceServiceImpl;
import hotel.service_implementation.PaymentServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class BillingController {

    @FXML private ComboBox<Invoice> cbInvoice;

    @FXML private Label lblReservation, lblGuest, lblRoom;
    @FXML private Label lblSubtotal, lblTax, lblTotal, lblPaid;

    @FXML private TableView<Payment> paymentTable;

    @FXML private TableColumn<Payment, Integer> colId;
    @FXML private TableColumn<Payment, BigDecimal> colAmount;
    @FXML private TableColumn<Payment, String> colMethod;
    @FXML private TableColumn<Payment, String> colStatus;
    @FXML private TableColumn<Payment, String> colDate;

    private final InvoiceService invoiceService = new InvoiceServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {

        //invoice display format: "Invoice #1 (Res 1)"
        cbInvoice.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Invoice inv, boolean empty) {
                super.updateItem(inv, empty);
                if (empty || inv == null) {
                    setText(null);
                } else {
                    Reservation res = inv.getReservation();
                    String suffix;
                    if (res != null) {
                        suffix = "(Res " + res.getReservationId() + ")";
                    } else {
                        suffix = "(Res -)";
                    }
                    setText("Invoice #" + inv.getInvoiceId() + " " + suffix);
                }
            }
        });

        cbInvoice.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Invoice inv, boolean empty) {
                super.updateItem(inv, empty);
                if (empty || inv == null) {
                    setText(null);
                } else {
                    Reservation res = inv.getReservation();
                    String suffix;
                    if (res != null) {
                        suffix = "(Res " + res.getReservationId() + ")";
                    } else {
                        suffix = "(Res -)";
                    }
                    setText("Invoice #" + inv.getInvoiceId() + " " + suffix);
                }
            }
        });

        //payment table columns
        colId.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getPaymentId()).asObject());
        colAmount.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getAmount()));
        colMethod.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getMethod().name()));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().name()));
        colDate.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPaymentDate().format(fmt)));

        loadInvoices();

        cbInvoice.setOnAction(e -> loadSelectedInvoice());
    }

    private void loadInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        cbInvoice.setItems(FXCollections.observableArrayList(invoices));

        if (!invoices.isEmpty()) {
            cbInvoice.getSelectionModel().selectFirst();
            loadSelectedInvoice();
        } else {
            clearDetails();
        }
    }

    private void loadSelectedInvoice() {
        Invoice selected = cbInvoice.getValue();
        if (selected == null) {
            clearDetails();
            return;
        }

        // always reload from DB to get latest values
        Invoice inv = invoiceService.getInvoiceById(selected.getInvoiceId());
        if (inv == null) {
            clearDetails();
            showError("Selected invoice no longer exists.");
            return;
        }

        Reservation r = inv.getReservation();

        if (r != null) {
            lblReservation.setText(String.valueOf(r.getReservationId()));

            if (r.getGuest() != null) {
                lblGuest.setText(String.valueOf(r.getGuest().getGuestId()));
            } else {
                lblGuest.setText("-");
            }

            Room room = r.getRoom();
            if (room != null) {
                lblRoom.setText(String.valueOf(room.getRoomId()));
            } else {
                lblRoom.setText("-");
            }
        } else {
            lblReservation.setText("-");
            lblGuest.setText("-");
            lblRoom.setText("-");
        }

        lblSubtotal.setText(inv.getSubtotal().toString());
        lblTax.setText(inv.getTax().toString());
        lblTotal.setText(inv.getTotal().toString());
        lblPaid.setText(inv.isPaid() ? "YES" : "NO");

        List<Payment> payments = paymentService.getPaymentsByInvoice(inv.getInvoiceId());
        paymentTable.setItems(FXCollections.observableArrayList(payments));
    }

    @FXML
    private void onRefreshClicked() {
        loadInvoices();
    }

    @FXML
    private void onAddPaymentClicked() {
        Invoice selected = cbInvoice.getValue();
        if (selected == null) {
            showWarning("Please select an invoice first.");
            return;
        }

        // Re-fetch latest invoice state
        Invoice inv = invoiceService.getInvoiceById(selected.getInvoiceId());
        if (inv == null) {
            showError("Invoice not found.");
            return;
        }

        // Block adding payments to fully-paid invoices
        if (inv.isPaid()) {
            showWarning("This invoice is already fully paid. No additional payment is required.");
            return;
        }

        // Ask amount
        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Add Payment");
        amountDialog.setHeaderText(null);
        amountDialog.setContentText("Enter amount:");
        Optional<String> result = amountDialog.showAndWait();
        if (result.isEmpty()) return;

        BigDecimal amount;
        try {
            amount = new BigDecimal(result.get().trim());
        } catch (Exception e) {
            showError("Invalid amount.");
            return;
        }

        // Choose method
        ChoiceDialog<PaymentMethod> methodDialog =
                new ChoiceDialog<>(PaymentMethod.CASH, PaymentMethod.values());
        methodDialog.setTitle("Payment Method");
        methodDialog.setHeaderText(null);
        methodDialog.setContentText("Method:");
        Optional<PaymentMethod> method = methodDialog.showAndWait();
        if (method.isEmpty()) return;

        // Choose status
        ChoiceDialog<PaymentStatus> statusDialog =
                new ChoiceDialog<>(PaymentStatus.SUCCESS, PaymentStatus.values());
        statusDialog.setTitle("Payment Status");
        statusDialog.setHeaderText(null);
        statusDialog.setContentText("Status:");
        Optional<PaymentStatus> status = statusDialog.showAndWait();
        if (status.isEmpty()) return;

        // Record payment with error handling
        try {
            paymentService.recordPayment(
                    inv.getInvoiceId(),
                    amount,
                    method.get(),
                    status.get()
            );
            showInfo("Payment added successfully.");
        } catch (IllegalStateException ex) {
            showWarning(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to record payment: " + ex.getMessage());
        }

        // Refresh invoice & payment table
        loadSelectedInvoice();
    }

    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage) cbInvoice.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void clearDetails() {
        lblReservation.setText("");
        lblGuest.setText("");
        lblRoom.setText("");
        lblSubtotal.setText("");
        lblTax.setText("");
        lblTotal.setText("");
        lblPaid.setText("");
        paymentTable.setItems(FXCollections.observableArrayList());
    }
}
