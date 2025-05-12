package controlador;

import javafx.scene.control.Alert;
import modelo.Ficha;
import modelo.Tablero;
import java.util.Stack;
import modelo.MovimientoRealizado;

public class ControladorTriangulo implements ControladorJuego {

    private Tablero tablero;
    private Ficha fichaSeleccionada;
    private int contadorMovimientos;
    private Stack<MovimientoRealizado> pilaMovimientos = new Stack<>();

    public ControladorTriangulo(Tablero tablero) {
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
        if (fichaSeleccionada == null) return false;

        Ficha destino = tablero.getFicha(filaDestino, colDestino);
        if (destino == null || destino.isExiste()) return false;

        int dx = filaDestino - fichaSeleccionada.getxPosicion();
        int dy = colDestino - fichaSeleccionada.getyPosicion();

        // Asegurarse de que el movimiento sea válido
        if (!((Math.abs(dx) == 2 && dy == 0) || (Math.abs(dy) == 2 && dx == 0) || (Math.abs(dx) == 2 && Math.abs(dy) == 2)))
            return false;

        // Coordenadas de la ficha intermedia
        int filaMedia = fichaSeleccionada.getxPosicion() + dx / 2;
        int colMedia = fichaSeleccionada.getyPosicion() + dy / 2;
        Ficha intermedia = tablero.getFicha(filaMedia, colMedia);

        if (intermedia != null && intermedia.isExiste()) {
            // Realizar el movimiento
            pilaMovimientos.push(new MovimientoRealizado(fichaSeleccionada, intermedia, destino));

            intermedia.setExiste(false);
            destino.setExiste(true);
            fichaSeleccionada.setExiste(false);
            fichaSeleccionada = null;
            contadorMovimientos++;

            // Verificar si el jugador ha ganado o perdido
            if (verificarVictoria()) {
                mostrarAlerta("\u00a1Victoria!", "\u00a1Ganaste! Solo queda una ficha.");
            } else if (!hayMovimientosPosibles()) {
                mostrarAlerta("Derrota", "No hay m\u00e1s movimientos posibles.");
            }
            return true;
        }
        return false;
    }

    public boolean deshacerUltimoMovimiento() {
        if (pilaMovimientos.isEmpty()) return false;

        MovimientoRealizado mov = pilaMovimientos.pop();

        // Deshacer el movimiento anterior
        tablero.getFicha(mov.getOrigen().getxPosicion(), mov.getOrigen().getyPosicion()).setExiste(true);
        tablero.getFicha(mov.getIntermedia().getxPosicion(), mov.getIntermedia().getyPosicion()).setExiste(true);
        tablero.getFicha(mov.getDestino().getxPosicion(), mov.getDestino().getyPosicion()).setExiste(false);

        contadorMovimientos--;
        return true;
    }

    public boolean verificarVictoria() {
        int contador = 0;
        // Contar el número de fichas que quedan en el tablero
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null && ficha.isExiste()) contador++;
            }
        }
        // Si solo queda una ficha, el jugador ha ganado
        return contador == 1;
    }

    private boolean hayMovimientosPosibles() {
        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col <= fila; col++) {
                Ficha ficha = tablero.getFicha(fila, col);
                if (ficha != null && ficha.isExiste() && puedeMover(fila, col)) return true;
            }
        }
        return false;
    }

    private boolean puedeMover(int fila, int col) {
        int[][] direcciones = {
                {0, 2}, {0, -2},
                {2, 0}, {-2, 0},
                {2, 2}, {-2, -2}
        };

        // Verificar si la ficha puede moverse en alguna de las direcciones posibles
        for (int[] dir : direcciones) {
            int destinoFila = fila + dir[0];
            int destinoCol = col + dir[1];
            int mediaFila = fila + dir[0] / 2;
            int mediaCol = col + dir[1] / 2;

            Ficha destino = tablero.getFicha(destinoFila, destinoCol);
            Ficha intermedia = tablero.getFicha(mediaFila, mediaCol);

            if (destino != null && !destino.isExiste() && intermedia != null && intermedia.isExiste()) return true;
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
