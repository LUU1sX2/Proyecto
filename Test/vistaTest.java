import modelo.Tablero;
import modelo.Ficha;
import controlador.ControladorTriangulo;
import controlador.ControladorCruz;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;

import vista.vista;

import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;

class VistaTest {

    private Tablero tableroTriangular;
    private Tablero tableroCruz;
    private ControladorTriangulo controladorTriangular;
    private ControladorCruz controladorCruz;

    @BeforeAll
    static void initJavaFX() {

        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        tableroTriangular = new Tablero(5);
        controladorTriangular = new ControladorTriangulo(tableroTriangular, null);

        tableroCruz = new Tablero(7, true);
        controladorCruz = new ControladorCruz(tableroCruz, null);
    }

    @Test
    void testBloqueoJuego() {
        for (int fila = 0; fila < 5; fila++) {
            for (int col = 0; col <= fila; col++) {
                tableroTriangular.getFicha(fila, col).setExiste(false);
            }
        }

        tableroTriangular.getFicha(0, 0).setExiste(true);
        tableroTriangular.getFicha(2, 2).setExiste(true);
        tableroTriangular.getFicha(4, 4).setExiste(true);

        assertFalse(controladorTriangular.hayMovimientosPosibles());
        assertFalse(controladorTriangular.verificarVictoria());
    }

    @Test
    void testControladorCruzDetectaVictoria() {
        Tablero tablero = new Tablero(7, true);
        vista vistaMock = new vista();
        ControladorCruz controlador = new ControladorCruz(tablero, vistaMock);

        for (int fila = 0; fila < tablero.getTamaño(); fila++) {
            for (int col = 0; col < tablero.getTamaño(); col++) {
                Ficha f = tablero.getFicha(fila, col);
                if (f != null) f.setExiste(false);
            }
        }
        tablero.getFicha(3, 3).setExiste(true);

        assertTrue(controlador.verificarVictoria());
    }

    @Test
    void testPuntajeInicialEsCero() {
        vista v = new vista();
        assertEquals(0, v.getPuntajeActual());
    }
    @Test
    void testReiniciarTemporizador() {
        Platform.runLater(() -> {
            vista vista = new vista();
            vista.reiniciarTemporizador();
            assertEquals(0, vista.getPuntajeActual());
        });
    }
}
