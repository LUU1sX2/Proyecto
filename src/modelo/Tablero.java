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
            // Puedes agregar inicializarTableroTriangular() si lo deseas.
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

