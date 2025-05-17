package vista;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        vista vista = new vista();
        vista.mostrarMenuPrincipal(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
