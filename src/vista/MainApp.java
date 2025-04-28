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
        mostrarMenuPrincipal();
    }

    private void mostrarMenuPrincipal() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Button botonModoTriangular = new Button("Modo Triangular");
        Button botonModoCruz = new Button("Modo Cruz");
        Button botonSalir = new Button("Salir");

        botonModoTriangular.setOnAction(e -> mostrarTableroTriangular());
        botonModoCruz.setOnAction(e -> System.out.println("Aquí Modo Cruz"));
        botonSalir.setOnAction(e -> primaryStage.close());

        layout.getChildren().addAll(botonModoTriangular, botonModoCruz, botonSalir);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setTitle("Menú Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mostrarTableroTriangular() {
        TableroVista tableroVista = new TableroVista();
        tableroVista.start(primaryStage); 
    }

    public static void main(String[] args) {
        launch(args);
    }
}
