package vista;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.net.URL;
import java.util.List;
import java.util.Optional;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class vista extends Application {

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

    private Background crearFondo() {
        Image fondo = new Image(getClass().getResource("/img/fondo.png").toExternalForm());
        BackgroundImage fondoImg = new BackgroundImage(
                fondo,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, false)
        );
        return new Background(fondoImg);
    }

    private Button crearBotonConImagen(String imagePath, double width, double height) {
        URL imageUrl = getClass().getClassLoader().getResource("img/" + imagePath);
        if (imageUrl == null) {
            System.err.println("⚠️ Imagen no encontrada: img/" + imagePath);
            return new Button("X");
        }

        ImageView imagen = new ImageView(new Image(imageUrl.toExternalForm()));
        imagen.setFitWidth(width);
        imagen.setFitHeight(height);
        Button boton = new Button();
        boton.setGraphic(imagen);
        boton.setStyle("-fx-background-color: transparent;");
        return boton;
    }

    public void mostrarMenuPrincipal(Stage primaryStage) {
        reproducirMusica("enmenu.mp3");
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(crearFondo());

        Button triangular = crearBotonConImagen("triangulo.png", 50, 50);
        triangular.setOnAction(e -> mostrarTableroTriangular(primaryStage));

        Button cruz = crearBotonConImagen("cruz.png", 50, 50);
        cruz.setOnAction(e -> mostrarTableroCruz(primaryStage));

        Button salir = crearBotonConImagen("salir.png", 100, 50);
        salir.setOnAction(e -> System.exit(0));

        Label tituloTriangulo = new Label("Top 5 - Triangular:");
        Label tituloCruz = new Label("Top 5 - Cruz:");

        listaPuntajesTriangulo.setPrefSize(200, 120);
        listaPuntajesCruz.setPrefSize(200, 120);

        listaPuntajesTriangulo.getStyleClass().add("list-view");
        listaPuntajesCruz.getStyleClass().add("list-view");
        tituloTriangulo.getStyleClass().add("label");
        tituloCruz.getStyleClass().add("label");

        actualizarTablaPuntajes();

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        puntajeLabel.getStyleClass().add("label");
        contadorLabel.getStyleClass().add("contador");

        VBox tablaTriangulo = new VBox(5, tituloTriangulo, listaPuntajesTriangulo);
        VBox tablaCruz = new VBox(5, tituloCruz, listaPuntajesCruz);
        HBox tablas = new HBox(20, tablaTriangulo, tablaCruz);
        tablas.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(tablas, triangular, cruz, salir);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Selecciona un modo");
        primaryStage.show();
    }

    private void mostrarTableroTriangular(Stage primaryStage) {
        int tamaño = 5;
        reproducirMusica("modotriangulo.mp3");
        tablero = new Tablero(tamaño);
        controlador = new ControladorTriangulo(tablero, this);
        botones = new Button[tamaño][tamaño];

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(crearFondo());
        contadorLabel.setText("Movimientos: 0");
        puntajeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFD700;");
        contadorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFD700;");

        for (int fila = 0; fila < tamaño; fila++) {
            HBox filaBotones = new HBox(10);
            filaBotones.setAlignment(Pos.CENTER);

            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                Button boton = new Button();
                boton.setGraphic(new ImageView(new Image(getClass().getResource(
                        ficha.isExiste() ? "/img/ficha.png" : "/img/muerto.png").toExternalForm(), 50, 50, true, true)));
                boton.setStyle("-fx-background-color: transparent;");
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

        Button botonSalirMenu = crearBotonConImagen("menu.png", 100, 40);
        botonSalirMenu.setOnAction(e -> {
            mostrarMenuPrincipal(primaryStage);
            reiniciarTemporizador();
        });

        Button botonSalirJuego = crearBotonConImagen("salir.png", 100, 40);
        botonSalirJuego.setOnAction(e -> System.exit(0));

        Button undoBtn = crearBotonConImagen("undo.jpg", 100, 40);
        undoBtn.setOnAction(e -> {
            if (controlador.deshacerUltimoMovimiento()) {
                actualizarVista();
                actualizarContador();
            }
        });

        Button reiniciarBtn = crearBotonConImagen("reiniciar.png", 100, 40);
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
        reproducirMusica("modocruz.mp3");
        tablero = new Tablero(tamaño, true);
        controlador = new ControladorCruz(tablero, this);
        botones = new Button[tamaño][tamaño];

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(crearFondo());
        contadorLabel.setText("Movimientos: 0");
        puntajeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFD700;");
        contadorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFD700;");

        for (int fila = 0; fila < tamaño; fila++) {
            HBox filaBotones = new HBox(10);
            filaBotones.setAlignment(Pos.CENTER);

            for (int col = 0; col < tamaño; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null) {
                    Button boton = new Button();
                    boton.setGraphic(new ImageView(new Image(getClass().getResource(
                            ficha.isExiste() ? "/img/ficha.png" : "/img/muerto.png").toExternalForm(), 50, 50, true, true)));
                    boton.setStyle("-fx-background-color: transparent;");
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
                    filaBotones.getChildren().add(boton);
                }
            }
            layout.getChildren().add(filaBotones);
        }

        layout.getChildren().add(puntajeLabel);

        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER);

        Button botonSalirMenu = crearBotonConImagen("menu.png", 100, 40);
        botonSalirMenu.setOnAction(e -> {
            mostrarMenuPrincipal(primaryStage);
            reiniciarTemporizador();
        });

        Button botonSalirJuego = crearBotonConImagen("salir.png", 100, 40);
        botonSalirJuego.setOnAction(e -> System.exit(0));

        Button undoBtn = crearBotonConImagen("undo.jpg", 100, 40);
        undoBtn.setOnAction(e -> {
            if (controlador.deshacerUltimoMovimiento()) {
                actualizarVistaCruz();
            }
        });

        Button reiniciarBtn = crearBotonConImagen("reiniciar.png", 100, 40);
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
                    botones[fila][col].setGraphic(new ImageView(new Image(getClass().getResource(
                            ficha.isExiste() ? "/img/ficha.png" : "/img/muerto.png").toExternalForm(), 50, 50, true, true)));
                }
            }
        }
        contadorLabel.setText("Movimientos: " + controlador.getContadorMovimientos());
    }

    private void actualizarVista() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                botones[fila][col].setGraphic(new ImageView(new Image(getClass().getResource(
                        ficha.isExiste() ? "/img/ficha.png" : "/img/muerto.png").toExternalForm(), 50, 50, true, true)));
            }
        }
    }

    private void actualizarContador() {
        contadorLabel.setText("Movimientos: " + controlador.getContadorMovimientos());
    }

    public void iniciarTemporizador() {
        if (temporizadorIniciado) return;

        temporizadorIniciado = true;
        if (temporizador != null) temporizador.stop();
        tiempoTranscurrido = 0;
        puntaje = 0;

        temporizador = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            tiempoTranscurrido++;
            puntaje = (int) tiempoTranscurrido;
            puntajeLabel.setText("Puntaje: " + puntaje);
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();
    }

    public void detenerTemporizador() {
        if (temporizador != null) temporizador.stop();
        temporizadorIniciado = false;
    }

    public void reiniciarTemporizador() {
        if (temporizador != null) temporizador.stop();
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

    private MediaPlayer currentMediaPlayer;

    private void reproducirMusica(String archivoMusica) {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
        }

        URL url = getClass().getResource("/audio/" + archivoMusica);
        if (url != null) {
            String rutaMusica = url.toString();
            Media media = new Media(rutaMusica);
            currentMediaPlayer = new MediaPlayer(media);
            currentMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            currentMediaPlayer.play();
        } else {
            System.out.println("Archivo no encontrado: " + archivoMusica);
        }
    }

    public int getPuntajeActual() {
        return puntaje;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
