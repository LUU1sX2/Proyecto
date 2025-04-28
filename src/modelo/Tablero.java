/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Jose Luis
 */
public class Tablero {
    private int tamaño;
    private Ficha[][] fichas;

    public Tablero(int tamaño) {
        this.tamaño = tamaño;
        fichas = new Ficha[tamaño][tamaño];
        inicializarTablero();
    }

    private void inicializarTablero() {
        for (int fila = 0; fila < tamaño; fila++) {
            for (int col = 0; col <= fila; col++) {
                fichas[fila][col] = new Ficha(fila, col, true); 
            }
        }
    }

    public Ficha getFicha(int fila, int columna) {
        if (fila >= 0 && fila < tamaño && columna >= 0 && columna <= fila) {
            return fichas[fila][columna];
        }
        return null;
    }

    public int getTamaño() {
        return tamaño;
    }
}
