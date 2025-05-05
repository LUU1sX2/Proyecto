package controlador;

import javafx.scene.control.Alert;
import modelo.Ficha;
import modelo.Tablero;
import java.util.Stack;
import modelo.MovimientoRealizado;

public class JuegoController {

    private Tablero tablero;
    private Ficha fichaSeleccionada;
    private int contadorMovimientos;
    private Stack<MovimientoRealizado> pilaMovimientos = new Stack<>();

    public JuegoController(Tablero tablero) {
        this.tablero = tablero;
        this.contadorMovimientos = 0;
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

        // Solo permitir salto de 2 posiciones horizontal o diagonal
        if (!((Math.abs(dx) == 2 && dy == 0) || (Math.abs(dy) == 2 && dx == 0) || (Math.abs(dx) == 2 && Math.abs(dy) == 2))) {
            return false;
        }

        int filaMedia = fichaSeleccionada.getxPosicion() + dx / 2;
        int colMedia = fichaSeleccionada.getyPosicion() + dy / 2;
        Ficha intermedia = tablero.getFicha(filaMedia, colMedia);

        if (intermedia != null && intermedia.isExiste()) {
            // NUEVO: Guardar el movimiento antes de realizarlo
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
                mostrarAlerta("¡Victoria!", "¡Ganaste! Solo queda una ficha.");
            } else if (!hayMovimientosPosibles()) {
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

        contadorMovimientos--; // Disminuir el contador
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
            for (int col = 0; col <= fila; col++) {
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
            {2, 0}, {-2, 0}, // diagonal hacia abajo/arriba
            {2, 2}, {-2, -2} // diagonal izquierda/derecha
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
