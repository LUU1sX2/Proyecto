package vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import modelo.Ficha;
import modelo.Tablero;
import controlador.ControladorJuego;
import controlador.ControladorTriangulo;

import java.io.IOException;

public class TableroVista extends Application {

    private Stage primaryStage;
    private Button[][] botones;
    private Tablero tablero;
    private ControladorJuego controlador;
    private boolean seleccionando = true;
    private Label contadorLabel = new Label("Movimientos: 0");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mostrarMenuPrincipal(primaryStage);
    }

    public void mostrarMenuPrincipal(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Button triangular = new Button("Modo Triangular");
        triangular.setOnAction(e -> mostrarTableroTriangular(primaryStage));

        Button cruz = new Button("Modo Cruz");
        cruz.setOnAction(e -> mostrarTableroCruz(primaryStage));

        Button salir = new Button("Salir del Juego");
        salir.setOnAction(e -> System.exit(0));

        layout.getChildren().addAll(triangular, cruz, salir);
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Selecciona un modo");
        primaryStage.show();
    }

    private void mostrarTableroTriangular(Stage primaryStage) {
        try {
            // Cargar el FXML para el Tablero Triangular
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/TableroTriangulo.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y establecer el tablero
            ControladorTriangulo controlador = loader.getController();
            controlador.setTablero(new Tablero(5, false)); // Configurar el tablero después de cargar el controlador

            // Crear la escena y mostrarla
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Modo Triangular");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void mostrarTableroCruz(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/TableroVista.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Modo Cruz");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarClickFicha(int fila, int columna) {
        if (seleccionando) {
            if (controlador.seleccionarFicha(fila, columna)) {
                seleccionando = false;
            }
        } else {
            if (controlador.moverFicha(fila, columna)) {
                actualizarVista();
                actualizarContador();
            }
            seleccionando = true;
        }
    }

    private void actualizarVista() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                Button boton = botones[fila][col];

                boton.getStyleClass().remove("ficha-triangular");

                if (ficha != null && ficha.isExiste()) {
                    boton.getStyleClass().add("ficha-triangular");
                }
            }
        }
    }

    private void actualizarContador() {
        contadorLabel.setText("Movimientos: " + controlador.getContadorMovimientos());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
