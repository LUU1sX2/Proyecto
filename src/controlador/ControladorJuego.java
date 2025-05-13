package controlador;

public interface ControladorJuego {

    boolean seleccionarFicha(int fila, int col);
    boolean moverFicha(int fila, int col);
    boolean deshacerUltimoMovimiento();
    int getContadorMovimientos();
    boolean verificarVictoria();

}
