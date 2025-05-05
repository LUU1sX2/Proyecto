package vista;

import controlador.ControladorCruz;
import controlador.ControladorJuego;
import controlador.ControladorTriangulo;
import modelo.Tablero;
import modelo.Ficha;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

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
        int tamaño = 5;
        tablero = new Tablero(tamaño);
        controlador = new ControladorTriangulo(tablero);

        botones = new Button[tamaño][tamaño];

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        contadorLabel.setText("Movimientos: 0");

        for (int fila = 0; fila < tamaño; fila++) {
            HBox filaBotones = new HBox(10);
            filaBotones.setAlignment(Pos.CENTER);

            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                Button boton = new Button();
                boton.setText(ficha.isExiste() ? "Ficha" : "");
                boton.setPrefSize(50, 50);

                final int f = fila;
                final int c = col;
                boton.setOnAction(e -> manejarClickFicha(f, c));

                botones[fila][col] = boton;
                filaBotones.getChildren().add(boton);
            }

            layout.getChildren().add(filaBotones);
        }

        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button botonSalirMenu = new Button("Salir al Menú");
        botonSalirMenu.setOnAction(e -> mostrarMenuPrincipal(primaryStage));

        Button botonSalirJuego = new Button("Salir del Juego");
        botonSalirJuego.setOnAction(e -> System.exit(0));

        Button undoBtn = new Button("Undo");
        undoBtn.setOnAction(e -> {
            if (controlador.deshacerUltimoMovimiento()) {
                actualizarVista();
                actualizarContador();
            }
        });

        Button reiniciarBtn = new Button("Reiniciar");
        reiniciarBtn.setOnAction(e -> mostrarTableroTriangular(primaryStage));

        controles.getChildren().addAll(botonSalirMenu, botonSalirJuego, undoBtn, reiniciarBtn);
        layout.getChildren().addAll(contadorLabel, controles);

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setTitle("Tablero Triangular");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mostrarTableroCruz(Stage primaryStage) {
        int tamaño = 7;
        tablero = new Tablero(tamaño, true);
        controlador = new ControladorCruz(tablero);
        botones = new Button[tamaño][tamaño];

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        contadorLabel.setText("Movimientos: 0");

        for (int fila = 0; fila < tamaño; fila++) {
            HBox filaBotones = new HBox(10);
            filaBotones.setAlignment(Pos.CENTER);

            for (int col = 0; col < tamaño; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                Button boton = new Button();
                boton.setText((ficha != null && ficha.isExiste()) ? "Ficha" : "");
                boton.setPrefSize(50, 50);

                final int f = fila;
                final int c = col;
                boton.setOnAction(e -> {
                    if (seleccionando) {
                        if (controlador.seleccionarFicha(f, c)) {
                            seleccionando = false;
                        }
                    } else {
                        if (controlador.moverFicha(f, c)) {
                            actualizarVistaCruz();
                        }
                        seleccionando = true;
                    }
                });

                botones[fila][col] = boton;
                if (ficha != null) {
                    filaBotones.getChildren().add(boton);
                }
            }

            layout.getChildren().add(filaBotones);
        }

        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button botonSalirMenu = new Button("Salir al Menú");
        botonSalirMenu.setOnAction(e -> mostrarMenuPrincipal(primaryStage));

        Button botonSalirJuego = new Button("Salir del Juego");
        botonSalirJuego.setOnAction(e -> System.exit(0));

        Button undoBtn = new Button("Undo");
        undoBtn.setOnAction(e -> {
            if (controlador.deshacerUltimoMovimiento()) {
                actualizarVistaCruz();
            }
        });

        Button reiniciarBtn = new Button("Reiniciar");
        reiniciarBtn.setOnAction(e -> mostrarTableroCruz(primaryStage));

        controles.getChildren().addAll(botonSalirMenu, botonSalirJuego, undoBtn, reiniciarBtn);
        layout.getChildren().addAll(contadorLabel, controles);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setTitle("Modo Cruz");
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private void actualizarVistaCruz() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col < tablero.getTamaño(); col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null) {
                    botones[fila][col].setText(ficha.isExiste() ? "Ficha" : "");
                }
            }
        }
        contadorLabel.setText("Movimientos: " + controlador.getContadorMovimientos());
    }

    private void actualizarContador() {

        contadorLabel.setText("Movimientos: " + controlador.getContadorMovimientos());
    }

    private void actualizarVista() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                botones[fila][col].setText(ficha.isExiste() ? "Ficha" : "");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
