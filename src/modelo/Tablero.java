package modelo;

public class Tablero {
    private final int tamaño;
    private final Ficha[][] fichas;
    private final boolean esCruz;

    public Tablero(int tamaño, boolean esCruz) {
        this.tamaño = tamaño;
        this.esCruz = esCruz;
        this.fichas = new Ficha[tamaño][tamaño];

        if (esCruz) {
            inicializarTableroCruz();
        } else {
            inicializarTableroTriangular();  // Método para inicializar el tablero triangular
        }
    }

    private void inicializarTableroCruz() {
        for (int fila = 0; fila < tamaño; fila++) {
            for (int col = 0; col < tamaño; col++) {
                boolean enCruz = (
                        (fila >= 3 && fila <= 9 && col >= 8 && col <= 10) ||                         // brazo vertical
                                (fila >= 4 && fila <= 6 && col >= 6 && col <= 12) ||                         // brazo horizontal medio
                                ((fila >= 3 && fila <= 7) && (col == 6 || col <= 12))                        // extremos horizontales completos incluyendo fila 7
                );

                if (enCruz) {
                    fichas[fila][col] = new Ficha(fila, col, true);
                } else {
                    fichas[fila][col] = null;
                }
            }
        }

        // Centro del tablero en FXML es fila=6, col=9
        if (fichas[6][9] != null) {
            fichas[6][9].setExiste(false);
        }
    }

    private void inicializarTableroTriangular() {
        // Lógica para el tablero triangular (solo un ejemplo básico)
        // Aquí configuramos las posiciones de las fichas para el tablero triangular
        for (int fila = 0; fila < tamaño; fila++) {
            for (int col = 0; col <= fila; col++) {  // Las columnas van hasta el número de la fila (formato triangular)
                fichas[fila][col] = new Ficha(fila, col, true);
            }
        }

        // El centro o alguna ficha especial (puedes modificar esto según las reglas de tu juego)
        if (fichas[0][0] != null) {
            fichas[0][0].setExiste(false);  // Ejemplo, puedes cambiar la lógica de qué ficha no existe
        }
    }

    public Ficha getFicha(int fila, int columna) {
        if (fila >= 0 && fila < tamaño && columna >= 0 && columna < tamaño) {
            return fichas[fila][columna];
        }
        return null;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean esModoCruz() {
        return esCruz;
    }
}

