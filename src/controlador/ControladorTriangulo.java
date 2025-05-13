package controlador;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import modelo.Ficha;
import modelo.MovimientoRealizado;
import modelo.Tablero;
import javafx.event.ActionEvent;
import java.util.Stack;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControladorTriangulo implements ControladorJuego {

    @FXML private Label contadorLabel;
    @FXML private Button ficha1, ficha2, ficha3, ficha4, ficha5, ficha6, ficha7, ficha8, ficha9, ficha10, ficha11, ficha12, ficha13, ficha14, ficha15;
    @FXML private Button botonMenu, botonSalir, undoBtn, reiniciarBtn;
    private Button[][] botones = new Button[5][5];
    // Declare the array of buttons

    private Tablero tablero;
    private Ficha fichaSeleccionada;
    private int contadorMovimientos;
    private final Stack<MovimientoRealizado> pilaMovimientos = new Stack<>();

    @FXML
    private void initialize() {
        tablero = new Tablero(5, false);  // Inicializa el tablero Triangular
        fichaSeleccionada = null;
        contadorMovimientos = 0;

        // Configurar los botones (fichas) para que respondan a las acciones
        configurarBotones();

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

    private void configurarBotones() {
        // Asignar las acciones de los botones a los métodos
        configurarBoton(ficha1, 0, 0);
        configurarBoton(ficha2, 1, 0);
        configurarBoton(ficha3, 2, 0);
        // ... Repítelo para el resto de las fichas, asegurándote de asignar las posiciones correctas
    }

    private void configurarBoton(Button boton, int fila, int col) {
        if (boton != null) {
            boton.setOnAction(e -> manejarClick(fila, col));
        }
    }

    private void manejarClick(int fila, int col) {
        if (fichaSeleccionada == null) {
            if (seleccionarFicha(fila, col)) {
                fichaSeleccionada = tablero.getFicha(fila, col);
            }
        } else {
            fichaSeleccionada = null;
        }
    }

    private void actualizarVista() {
        // Recorremos cada botón del tablero
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                Button boton = botones[fila][col];  // Asegúrate de que 'botones' esté correctamente inicializado

                // Si hay una ficha en esa posición
                if (ficha != null) {
                    if (ficha.isExiste()) {
                        // Si la ficha existe, mostramos la imagen
                        Image image = new Image(getClass().getResourceAsStream("/img/ficha.jpg"));
                        ImageView view = new ImageView(image);
                        view.setFitWidth(40);  // Ajusta el tamaño según la casilla
                        view.setFitHeight(40);
                        boton.setGraphic(view);
                        boton.setText("");  // Vaciamos el texto del botón
                    } else {
                        // Si la ficha no existe (fue eliminada), limpiamos la imagen
                        boton.setGraphic(null);
                    }
                }
            }
        }

        // Actualizamos el contador de movimientos en la interfaz
        contadorLabel.setText("Movimientos: " + contadorMovimientos);
    }


    private void volverAlMenu() {
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

    @FXML
    public boolean moverFicha(int filaDestino, int colDestino) {
        // Verificar si la ficha seleccionada es válida
        if (fichaSeleccionada == null) {
            return false; // Si no hay ficha seleccionada, no se puede mover
        }

        // Obtener la ficha de destino
        Ficha destino = tablero.getFicha(filaDestino, colDestino);
        if (destino == null || destino.isExiste()) {
            return false; // Si el destino está fuera del tablero o ya está ocupado, no se puede mover
        }

        // Calcular el cambio de posición
        int dx = filaDestino - fichaSeleccionada.getxPosicion();
        int dy = colDestino - fichaSeleccionada.getyPosicion();

        // Verificar si el movimiento es válido (a dos casillas en alguna dirección)
        if (!((Math.abs(dx) == 2 && dy == 0) || (Math.abs(dy) == 2 && dx == 0) || (Math.abs(dx) == 2 && Math.abs(dy) == 2))) {
            return false; // El movimiento debe ser en una dirección válida
        }

        // Calcular la posición intermedia (la ficha que se debe saltar)
        int filaMedia = fichaSeleccionada.getxPosicion() + dx / 2;
        int colMedia = fichaSeleccionada.getyPosicion() + dy / 2;
        Ficha intermedia = tablero.getFicha(filaMedia, colMedia);

        // Verificar si la ficha intermedia está ocupada y se puede saltar
        if (intermedia != null && intermedia.isExiste()) {
            // Realizar el movimiento
            pilaMovimientos.push(new MovimientoRealizado(fichaSeleccionada, intermedia, destino)); // Guardar el movimiento en la pila de movimientos

            intermedia.setExiste(false); // Eliminar la ficha intermedia
            destino.setExiste(true); // Colocar la ficha en el destino
            fichaSeleccionada.setExiste(false); // Eliminar la ficha del origen
            fichaSeleccionada = null; // Desmarcar la ficha seleccionada
            contadorMovimientos++; // Aumentar el contador de movimientos

            // Comprobar si el jugador ha ganado
            if (verificarVictoria()) {
                mostrarAlerta("¡Victoria!", "¡Ganaste! Solo queda una ficha.");
            } else if (!hayMovimientosPosibles()) {
                // Si no hay más movimientos posibles, el jugador ha perdido
                mostrarAlerta("Derrota", "No hay más movimientos posibles.");
            }
            return true; // Movimiento exitoso
        }

        return false; // Si la ficha intermedia no es válida, el movimiento no es permitido
    }
    @FXML
    public void moverFicha(ActionEvent event) {
        // Obtener el botón que activó el evento
        Button fuente = (Button) event.getSource();

        // Extraer la fila y columna del ID del botón
        int filaDestino = Integer.parseInt(fuente.getId().substring(5)); // Tomando los últimos 2 caracteres del ID como la fila
        int colDestino = 0; // Suponiendo que tienes solo una columna, ajusta según tu lógica

        // Llamar al método moverFicha del controlador
        if (moverFicha(filaDestino, colDestino)) {
            actualizarVista();
        }
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
            for (int col = 0; col <= fila; col++) {
                Ficha f = tablero.getFicha(fila, col);
                if (f != null && f.isExiste()) contador++;
            }
        }
        return contador == 1;
    }

    private boolean hayMovimientosPosibles() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
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

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }
}
