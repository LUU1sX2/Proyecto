package vista;

import modelo.Tablero;
import modelo.Ficha;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class TableroVista extends Application {
    private Stage primaryStage;
    private Button[][] botones;
    private Tablero tablero;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mostrarTableroTriangular(); 
    }

    private void mostrarTableroTriangular() {
        int tamaño = 5; 
        tablero = new Tablero(tamaño);
        botones = new Button[tamaño][tamaño];

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        for (int fila = 0; fila < tamaño; fila++) {
            HBox filaBotones = new HBox(10);
            filaBotones.setAlignment(Pos.CENTER);

            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                Button boton = new Button();
                boton.setText("●"); 
                boton.setPrefSize(50, 50);

                final int f = fila;
                final int c = col;
                boton.setOnAction(e -> manejarClickFicha(f, c));

                botones[fila][col] = boton;
                filaBotones.getChildren().add(boton);
            }

            layout.getChildren().add(filaBotones);
        }

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setTitle("Tablero Triangular");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void manejarClickFicha(int fila, int columna) {
        Ficha ficha = tablero.getFicha(fila, columna);
        if (ficha != null && ficha.isExiste()) {
            System.out.println("Click en ficha activa en: (" + fila + ", " + columna + ")");
        } else {
            System.out.println("Click en espacio vacío.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
