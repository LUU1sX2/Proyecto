package controlador;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import modelo.Ficha;
import modelo.MovimientoRealizado;
import modelo.Tablero;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Stack;

public class ControladorCruz implements ControladorJuego {

    @FXML private Label contadorLabel;
    @FXML private GridPane tableroGrid;
    @FXML private Button botonMenu, botonSalir, undoBtn, reiniciarBtn;

    private Tablero tablero;
    private Ficha fichaSeleccionada;
    private int contadorMovimientos;
    private final Stack<MovimientoRealizado> pilaMovimientos = new Stack<>();

    @FXML
    private void initialize() {
        tablero = new Tablero(13, true);  // Compatible con diseño FXML
        fichaSeleccionada = null;
        contadorMovimientos = 0;

        // Configurar botones del GridPane
        for (Node node : tableroGrid.getChildren()) {
            if (node instanceof Button boton) {
                Integer fila = GridPane.getRowIndex(boton);
                Integer col = GridPane.getColumnIndex(boton);
                fila = (fila == null) ? 0 : fila;
                col = (col == null) ? 0 : col;

                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null) {
                    boton.setText(ficha.isExiste() ? "Ficha" : "");
                    final int f = fila;
                    final int c = col;
                    boton.setOnAction(e -> manejarClick(f, c));
                } else {
                    boton.setVisible(false);  // Ocultar los que no tienen ficha
                }
            }
        }

        // Configurar botones de acción
        botonMenu.setOnAction(e -> volverAlMenu());
        botonSalir.setOnAction(e -> System.exit(0));
        reiniciarBtn.setOnAction(e -> recargar());
        undoBtn.setOnAction(e -> {
            if (deshacerUltimoMovimiento()) {
                actualizarVista();
            }
        });

        actualizarVista(); // Mostrar estado inicial
    }

    private void manejarClick(int fila, int col) {
        if (fichaSeleccionada == null) {
            if (seleccionarFicha(fila, col)) {
                fichaSeleccionada = tablero.getFicha(fila, col);
            }
        } else {
            if (moverFicha(fila, col)) {
                actualizarVista();
            }
            fichaSeleccionada = null;
        }
    }

    private void actualizarVista() {
        for (Node node : tableroGrid.getChildren()) {
            if (node instanceof Button boton) {
                Integer fila = GridPane.getRowIndex(boton);
                Integer col = GridPane.getColumnIndex(boton);
                fila = (fila == null) ? 0 : fila;
                col = (col == null) ? 0 : col;

                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null) {
                    if (ficha.isExiste()) {
                        Image image = new Image(getClass().getResourceAsStream("/img/ficha.jpg"));
                        ImageView view = new ImageView(image);
                        view.setFitWidth(40); // ajusta según el tamaño de casilla
                        view.setFitHeight(40);
                        boton.setGraphic(view);
                        boton.setText(""); // asegúrate de que no haya texto

                    } else {
                        boton.setGraphic(null);  // ❗ Quita la imagen si la ficha fue eliminada
                    }

                    // Puedes mantener el texto o vaciarlo
                    boton.setText(""); // o "Ficha" si lo deseas
                }
            }
        }
        contadorLabel.setText("Movimientos: " + contadorMovimientos);
    }


    private void volverAlMenu() {
        // Aquí puedes cargar otra vista si tienes más escenas
        System.out.println("Volver al menú (no implementado)");
    }

    private void recargar() {
        initialize();
    }

    public boolean seleccionarFicha(int fila, int col) {
        Ficha f = tablero.getFicha(fila, col);
        if (f != null && f.isExiste()) {
            fichaSeleccionada = f;
            return true;
        }
        return false;
    }

    public boolean moverFicha(int filaDestino, int colDestino) {
        if (fichaSeleccionada == null) return false;

        Ficha destino = tablero.getFicha(filaDestino, colDestino);
        if (destino == null || destino.isExiste()) return false;

        int dx = filaDestino - fichaSeleccionada.getxPosicion();
        int dy = colDestino - fichaSeleccionada.getyPosicion();

        if (!((Math.abs(dx) == 2 && dy == 0) || (Math.abs(dy) == 2 && dx == 0))) return false;

        int filaMedia = fichaSeleccionada.getxPosicion() + dx / 2;
        int colMedia = fichaSeleccionada.getyPosicion() + dy / 2;
        Ficha intermedia = tablero.getFicha(filaMedia, colMedia);

        if (intermedia != null && intermedia.isExiste()) {
            pilaMovimientos.push(new MovimientoRealizado(fichaSeleccionada, intermedia, destino));
            intermedia.setExiste(false);
            destino.setExiste(true);
            fichaSeleccionada.setExiste(false);
            contadorMovimientos++;

            if (verificarVictoria()) {
                mostrarAlerta("¡Victoria!", "¡Ganaste! Solo queda una ficha.");
            } else if (!hayMovimientosPosibles()) {
                mostrarAlerta("Derrota", "No hay más movimientos posibles.");
            }
            return true;
        }

        return false;
    }

    public boolean deshacerUltimoMovimiento() {
        if (pilaMovimientos.isEmpty()) return false;

        MovimientoRealizado mov = pilaMovimientos.pop();
        tablero.getFicha(mov.getOrigen().getxPosicion(), mov.getOrigen().getyPosicion()).setExiste(true);
        tablero.getFicha(mov.getIntermedia().getxPosicion(), mov.getIntermedia().getyPosicion()).setExiste(true);
        tablero.getFicha(mov.getDestino().getxPosicion(), mov.getDestino().getyPosicion()).setExiste(false);
        contadorMovimientos--;
        return true;
    }

    public boolean verificarVictoria() {
        int contador = 0;
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col < tablero.getTamaño(); col++) {
                Ficha f = tablero.getFicha(fila, col);
                if (f != null && f.isExiste()) contador++;
            }
        }
        return contador == 1;
    }

    private boolean hayMovimientosPosibles() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col < tablero.getTamaño(); col++) {
                Ficha f = tablero.getFicha(fila, col);
                if (f != null && f.isExiste() && puedeMover(fila, col)) return true;
            }
        }
        return false;
    }

    private boolean puedeMover(int fila, int col) {
        int[][] direcciones = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
        for (int[] dir : direcciones) {
            int destinoFila = fila + dir[0];
            int destinoCol = col + dir[1];
            int mediaFila = fila + dir[0] / 2;
            int mediaCol = col + dir[1] / 2;

            Ficha destino = tablero.getFicha(destinoFila, destinoCol);
            Ficha intermedia = tablero.getFicha(mediaFila, mediaCol);

            if (destino != null && !destino.isExiste() && intermedia != null && intermedia.isExiste()) {
                return true;
            }
        }
        return false;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public int getContadorMovimientos() {
        return contadorMovimientos;
    }
}
