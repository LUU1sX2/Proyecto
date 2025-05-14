package controlador;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import modelo.Ficha;
import modelo.Tablero;

import java.util.Optional;
import java.util.Stack;
import modelo.MovimientoRealizado;
import modelo.PuntajeManager;
import vista.TableroVista;

public class ControladorCruz implements ControladorJuego {

    private Tablero tablero;
    private Ficha fichaSeleccionada;
    private int contadorMovimientos;
    private Stack<MovimientoRealizado> pilaMovimientos = new Stack<>();
    private TableroVista vista;

    public ControladorCruz(Tablero tablero,TableroVista vista) {
        this.tablero = tablero;
        this.vista= vista;

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
        if (fichaSeleccionada == null) {
            return false;
        }

        Ficha destino = tablero.getFicha(filaDestino, colDestino);
        if (destino == null || destino.isExiste()) {
            return false;
        }

        int dx = filaDestino - fichaSeleccionada.getxPosicion();
        int dy = colDestino - fichaSeleccionada.getyPosicion();

        if (!((Math.abs(dx) == 2 && dy == 0) || (Math.abs(dy) == 2 && dx == 0))) {
            return false;
        }

        int filaMedia = fichaSeleccionada.getxPosicion() + dx / 2;
        int colMedia = fichaSeleccionada.getyPosicion() + dy / 2;
        Ficha intermedia = tablero.getFicha(filaMedia, colMedia);

        if (intermedia != null && intermedia.isExiste()) {
            vista.iniciarTemporizador();

            pilaMovimientos.push(new MovimientoRealizado(
                    fichaSeleccionada, intermedia, destino
            ));

            intermedia.setExiste(false); // ficha comida
            destino.setExiste(true);
            fichaSeleccionada.setExiste(false);
            fichaSeleccionada = null;
            contadorMovimientos++;
            // Verifica si se gana o se pierde
            if (verificarVictoria()) {
                vista.detenerTemporizador();
                int puntajeActual = vista.getPuntajeActual(); // asegúrate que vista tenga este método

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("¡Ganaste!");
                dialog.setHeaderText("Ingresa tus 3 letras:");
                dialog.setContentText("Nombre:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(nombre -> {
                    if (nombre.matches("[A-Z]{3}")) {
                        PuntajeManager.guardarPuntaje(nombre, puntajeActual,0, "cruz"); // o "triangulo"
                        vista.actualizarTablaPuntajes(); // si tienes esto visible
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Debes ingresar exactamente 3 letras mayúsculas.");
                        error.showAndWait();
                    }
                });
            }else if (!hayMovimientosPosibles()) {
                vista.detenerTemporizador();
                mostrarAlerta("Derrota", "No hay más movimientos posibles.");
            }

            return true;
        }

        return false;
    }

    public boolean deshacerUltimoMovimiento() {
        if (pilaMovimientos.isEmpty()) {
            return false;
        }

        MovimientoRealizado mov = pilaMovimientos.pop();

        // Restaurar estados de las fichas
        tablero.getFicha(mov.getOrigen().getxPosicion(), mov.getOrigen().getyPosicion())
                .setExiste(mov.getOrigen().isExiste());

        tablero.getFicha(mov.getIntermedia().getxPosicion(), mov.getIntermedia().getyPosicion())
                .setExiste(mov.getIntermedia().isExiste());

        tablero.getFicha(mov.getDestino().getxPosicion(), mov.getDestino().getyPosicion())
                .setExiste(mov.getDestino().isExiste());

        return true;
    }

    public boolean verificarVictoria() {
        int contador = 0;
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col < tablero.getTamaño(); col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null && ficha.isExiste()) {
                    contador++;
                }
            }
        }
        return contador == 1;
    }

    private boolean hayMovimientosPosibles() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col < tablero.getTamaño(); col++) {
                Ficha f = tablero.getFicha(fila, col);
                if (f != null && f.isExiste() && puedeMover(fila, col)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean puedeMover(int fila, int col) {
        int[][] direcciones = {
                {0, 2}, {0, -2}, // horizontal
                {2, 0}, {-2, 0}  // vertical
        };


        for (int[] dir : direcciones) {
            int destinoFila = fila + dir[0];
            int destinoCol = col + dir[1];
            int mediaFila = fila + dir[0] / 2;
            int mediaCol = col + dir[1] / 2;

            Ficha destino = tablero.getFicha(destinoFila, destinoCol);
            Ficha intermedia = tablero.getFicha(mediaFila, mediaCol);

            if (destino != null && !destino.isExiste()
                    && intermedia != null && intermedia.isExiste()) {
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
