package modelo;

import java.io.*;
import java.util.*;

public class PuntajeManager {

    private static final String ARCHIVO_TRIANGULO = "puntajes_triangulo.txt";
    private static final String ARCHIVO_CRUZ = "puntajes_cruz.txt";

    // Cargar puntajes (formato: NOMBRE PUNTAJE MOVIMIENTOS)
    public static List<String> cargarMejoresPuntajes(String modo) {
        String archivo = obtenerArchivo(modo);
        List<String> puntajes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                puntajes.add(linea);
            }
        } catch (IOException e) {
            // Si no existe el archivo, se retorna lista vacía
        }

        return puntajes;
    }

    // Guardar un nuevo puntaje con movimientos
    public static void guardarPuntaje(String nombre, int puntaje, int movimientos, String modo) {
        String archivo = obtenerArchivo(modo);
        List<String> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lista.add(linea);
            }
        } catch (IOException e) {
            // Ignorar si el archivo aún no existe
        }

        // Agregar nuevo puntaje (formato: NOMBRE PUNTAJE MOVIMIENTOS)
        lista.add(nombre + " " + puntaje + " " + movimientos);

        // Ordenar por puntaje (tiempo menor es mejor)
        lista.sort(Comparator.comparingInt(linea -> {
            String[] partes = linea.split(" ");
            return Integer.parseInt(partes[1]); // puntaje
        }));

        // Limitar a top 5
        if (lista.size() > 5) {
            lista = lista.subList(0, 5);
        }

        // Guardar en archivo
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (String linea : lista) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String obtenerArchivo(String modo) {
        if ("triangulo".equalsIgnoreCase(modo)) {
            return ARCHIVO_TRIANGULO;
        } else if ("cruz".equalsIgnoreCase(modo)) {
            return ARCHIVO_CRUZ;
        } else {
            throw new IllegalArgumentException("Modo no reconocido: " + modo);
        }
    }
}
