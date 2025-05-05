package vista;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        TableroVista vista = new TableroVista();
        vista.mostrarMenuPrincipal(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
