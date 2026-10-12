package hotel.ui;

import hotel.model.Receptionist;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static Receptionist loggedInUser;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void showLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Hotel Management - Login");

        applyAppIcon(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // dashboard after login
    public static void showDashBoard(Receptionist receptionist) throws IOException {
        loggedInUser = receptionist;

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Hotel Management - Dashboard - Logged in as " + receptionist.getFullName());

        applyAppIcon(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void showMainWindow(Receptionist receptionist) throws IOException {
        loggedInUser = receptionist;

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("main_window.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Hotel Management - Logged in as " + receptionist.getFullName());

        applyAppIcon(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static Receptionist getLoggedInUser() {
        return loggedInUser;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // reusable helper for any Stage like primary or dialogs
    public static void applyAppIcon(Stage stage) {
        stage.getIcons().clear();
        stage.getIcons().add(
                new Image(MainApp.class.getResourceAsStream("hotel_icon.png"))
        );
    }
}
