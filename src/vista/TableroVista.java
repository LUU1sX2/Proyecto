package vista;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.PuntajeManager;
import controlador.ControladorCruz;
import controlador.ControladorJuego;
import controlador.ControladorTriangulo;
import modelo.Tablero;
import modelo.Ficha;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

public class TableroVista extends Application {

    private Stage primaryStage;
    private Button[][] botones;
    private Tablero tablero;
    private ControladorJuego controlador;
    private boolean seleccionando = true;
    private int puntaje = 0;
    private Label puntajeLabel = new Label("Puntaje: 0");
    private Timeline temporizador;
    private long tiempoTranscurrido = 0;
    private Label contadorLabel = new Label("Movimientos: 0");
    private ListView<String> listaPuntajesTriangulo = new ListView<>();
    private ListView<String> listaPuntajesCruz = new ListView<>();
    private boolean temporizadorIniciado = false;

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

        Label tituloTriangulo = new Label("Top 5 - Triangular:");
        Label tituloCruz = new Label("Top 5 - Cruz:");

        listaPuntajesTriangulo.setPrefSize(200, 120);
        listaPuntajesCruz.setPrefSize(200, 120);
        actualizarTablaPuntajes();

        VBox tablaTriangulo = new VBox(5, tituloTriangulo, listaPuntajesTriangulo);
        VBox tablaCruz = new VBox(5, tituloCruz, listaPuntajesCruz);
        HBox tablas = new HBox(20, tablaTriangulo, tablaCruz);
        tablas.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(tablas, triangular, cruz, salir);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Selecciona un modo");
        primaryStage.show();
    }

    private void mostrarTableroTriangular(Stage primaryStage) {
        int tamaño = 5;
        tablero = new Tablero(tamaño);
        controlador = new ControladorTriangulo(tablero, this);

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
        layout.getChildren().add(puntajeLabel);

        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button botonSalirMenu = new Button("Salir al Menú");
        botonSalirMenu.setOnAction(e -> {
            mostrarMenuPrincipal(primaryStage);
            reiniciarTemporizador();
        });

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
        reiniciarBtn.setOnAction(e -> {
            reiniciarTemporizador();
            mostrarTableroTriangular(primaryStage);
        });

        controles.getChildren().addAll(botonSalirMenu, botonSalirJuego, undoBtn, reiniciarBtn);
        layout.getChildren().addAll(contadorLabel, controles);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setTitle("Tablero Triangular");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mostrarTableroCruz(Stage primaryStage) {
        int tamaño = 7;
        tablero = new Tablero(tamaño, true);
        controlador = new ControladorCruz(tablero, this);
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
        layout.getChildren().add(puntajeLabel);

        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button botonSalirMenu = new Button("Salir al Menú");
        botonSalirMenu.setOnAction(e -> {
            mostrarMenuPrincipal(primaryStage);
            reiniciarTemporizador();
        });

        Button botonSalirJuego = new Button("Salir del Juego");
        botonSalirJuego.setOnAction(e -> System.exit(0));

        Button undoBtn = new Button("Undo");
        undoBtn.setOnAction(e -> {
            if (controlador.deshacerUltimoMovimiento()) {
                actualizarVistaCruz();
            }
        });

        Button reiniciarBtn = new Button("Reiniciar");
        reiniciarBtn.setOnAction(e -> {
            reiniciarTemporizador();
            mostrarTableroCruz(primaryStage);
        });

        controles.getChildren().addAll(botonSalirMenu, botonSalirJuego, undoBtn, reiniciarBtn);
        layout.getChildren().addAll(contadorLabel, controles);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setTitle("Modo Cruz");
        primaryStage.setResizable(false);
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

    public void iniciarTemporizador() {
        if (temporizadorIniciado) return;

        temporizadorIniciado = true;

        if (temporizador != null) {
            temporizador.stop();
        }

        tiempoTranscurrido = 0;
        puntaje = 0;

        temporizador = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            tiempoTranscurrido++;
            puntaje = (int) (tiempoTranscurrido);
            puntajeLabel.setText("Puntaje: " + puntaje);
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();
    }

    public void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
        temporizadorIniciado = false;
    }

    public void reiniciarTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
        tiempoTranscurrido = 0;
        puntaje = 0;
        puntajeLabel.setText("Puntaje: 0");
        temporizadorIniciado = false;
    }

    public void actualizarTablaPuntajes() {
        List<String> puntajesTriangulo = PuntajeManager.cargarMejoresPuntajes("triangulo");
        List<String> puntajesCruz = PuntajeManager.cargarMejoresPuntajes("cruz");

        listaPuntajesTriangulo.getItems().setAll(puntajesTriangulo);
        listaPuntajesCruz.getItems().setAll(puntajesCruz);
    }

    public void mostrarDialogoNombreYGuardarPuntaje(int puntaje, String modo) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("¡Ganaste!");
        dialog.setHeaderText("Ingresa tus 3 letras:");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            if (nombre.matches("[A-Z]{3}")) {
                PuntajeManager.guardarPuntaje(nombre, puntaje, controlador.getContadorMovimientos(), modo);
                actualizarTablaPuntajes();
            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Nombre inválido. Usa 3 letras mayúsculas.");
                alerta.showAndWait();
            }
        });
    }

    public int getPuntajeActual() {
        return puntaje;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
