package modelo;

public class Tablero {

    private int tamaño;
    private Ficha[][] fichas;
    private boolean esCruz;

    // Constructor para modo triangular
    public Tablero(int tamaño) {
        this.tamaño = tamaño;
        this.esCruz = false;
        fichas = new Ficha[tamaño][tamaño];
        inicializarTableroTriangular();
    }

    // Constructor para modo cruz
    public Tablero(int tamaño, boolean esCruz) {
        this.tamaño = tamaño;
        this.esCruz = esCruz;
        fichas = new Ficha[tamaño][tamaño];
        if (esCruz) {
            inicializarTableroCruz();
        } else {
            inicializarTableroTriangular();
        }
    }

    private void inicializarTableroTriangular() {
        int filaCentral = tamaño / 2;
        int colCentral = filaCentral / 2;

        for (int fila = 0; fila < tamaño; fila++) {
            for (int col = 0; col <= fila; col++) {
                boolean existe = !(fila == filaCentral && col == colCentral);
                fichas[fila][col] = new Ficha(fila, col, existe);
            }
        }
    }

    private void inicializarTableroCruz() {
        for (int fila = 0; fila < tamaño; fila++) {
            for (int col = 0; col < tamaño; col++) {
                // Para tablero de 13x13: brazos de la cruz del 3 al 9 inclusive
                boolean enCruz = (fila >= 3 && fila <= 9) || (col >= 3 && col <= 9);
                if (enCruz) {
                    fichas[fila][col] = new Ficha(fila, col, true);
                } else {
                    fichas[fila][col] = null;
                }
            }
        }

        // Centro vacío (posición 6,6 en un tablero 13x13)
        int centro = tamaño / 2;
        if (fichas[centro][centro] != null) {
            fichas[centro][centro].setExiste(false);
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
