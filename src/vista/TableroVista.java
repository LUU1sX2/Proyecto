package vista;

import controlador.ControladorCruz;
import controlador.ControladorJuego;
import controlador.ControladorTriangulo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Ficha;
import modelo.Tablero;

public class TableroVista extends Application {

    private Stage primaryStage;
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
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void mostrarTableroTriangular(Stage primaryStage) {
        int tamano = 5;
        tablero = new Tablero(tamano);
        controlador = new ControladorTriangulo(tablero);

        VBox tableroVisual = crearTableroVisual(tamano, false);
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        contadorLabel.setText("Movimientos: 0");
        HBox controles = crearControles(() -> mostrarTableroTriangular(primaryStage));

        layout.getChildren().addAll(tableroVisual, contadorLabel, controles);

        Scene scene = new Scene(layout, 500, 550);
        primaryStage.setTitle("Tablero Triangular");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void mostrarTableroCruz(Stage primaryStage) {
        int tamano = 7;
        tablero = new Tablero(tamano, true);
        controlador = new ControladorCruz(tablero);

        Pane tableroPane = new Pane();
        double spacing = 55;
        double offsetX = 30;
        double offsetY = 30;

        for (int fila = 0; fila < tamano; fila++) {
            for (int col = 0; col < tamano; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null && ficha.isExiste()) {
                    ImageView img = crearFichaVisual(ficha, fila, col);
                    img.setLayoutX(offsetX + col * spacing);
                    img.setLayoutY(offsetY + fila * spacing);
                    tableroPane.getChildren().add(img);
                }
            }
        }

        StackPane tableroCentrado = new StackPane(tableroPane);
        tableroCentrado.setPrefSize(400, 400);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        contadorLabel.setText("Movimientos: 0");
        HBox controles = crearControles(() -> mostrarTableroCruz(primaryStage));

        layout.getChildren().addAll(tableroCentrado, contadorLabel, controles);

        Scene scene = new Scene(layout, 500, 550);
        primaryStage.setTitle("Modo Cruz");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private VBox crearTableroVisual(int tamano, boolean esCruz) {
        VBox tableroVisual = new VBox(10);
        tableroVisual.setAlignment(Pos.CENTER);

        for (int fila = 0; fila < tamano; fila++) {
            HBox filaFichas = new HBox(10);
            filaFichas.setAlignment(Pos.CENTER);

            for (int col = 0; col < tamano; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (!esCruz && col > fila) continue;
                if (ficha != null) {
                    ImageView fichaImg = crearFichaVisual(ficha, fila, col);
                    filaFichas.getChildren().add(fichaImg);
                } else if (esCruz) {
                    Region espacioVacio = new Region();
                    espacioVacio.setPrefSize(50, 50);
                    filaFichas.getChildren().add(espacioVacio);
                }
            }

            tableroVisual.getChildren().add(filaFichas);
        }

        return tableroVisual;
    }

    private ImageView crearFichaVisual(Ficha ficha, int fila, int col) {
        ImageView img = new ImageView();
        img.setFitWidth(50);
        img.setFitHeight(50);
        img.setPreserveRatio(true);

        if (ficha != null && ficha.isExiste()) {
            img.setImage(new Image(getClass().getResource("/Imagenes/bolita_roja.png").toExternalForm()));
        }

        img.setOnMouseClicked(e -> {
            if (seleccionando) {
                if (controlador.seleccionarFicha(fila, col)) {
                    seleccionando = false;
                }
            } else {
                if (controlador.moverFicha(fila, col)) {
                    if (tablero.esModoCruz()) {
                        mostrarTableroCruz(primaryStage);
                    } else {
                        mostrarTableroTriangular(primaryStage);
                    }
                }
                seleccionando = true;
            }
        });

        return img;
    }

    private HBox crearControles(Runnable reiniciarAccion) {
        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button botonSalirMenu = new Button("Salir al Menú");
        botonSalirMenu.setOnAction(e -> mostrarMenuPrincipal(primaryStage));

        Button botonSalirJuego = new Button("Salir del Juego");
        botonSalirJuego.setOnAction(e -> System.exit(0));

        Button undoBtn = new Button("Undo");
        undoBtn.setOnAction(e -> {
            if (controlador.deshacerUltimoMovimiento()) {
                if (tablero.esModoCruz()) {
                    mostrarTableroCruz(primaryStage);
                } else {
                    mostrarTableroTriangular(primaryStage);
                }
            }
        });

        Button reiniciarBtn = new Button("Reiniciar");
        reiniciarBtn.setOnAction(e -> reiniciarAccion.run());

        controles.getChildren().addAll(botonSalirMenu, botonSalirJuego, undoBtn, reiniciarBtn);
        return controles;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
