package modelo;

import java.io.*;
import java.util.*;

public class PuntajeManager {

    private static final String ARCHIVO_TRIANGULO = "puntajes_triangulo.txt";
    private static final String ARCHIVO_CRUZ = "puntajes_cruz.txt";


    public static List<String> cargarMejoresPuntajes(String modo) {
        String archivo = obtenerArchivo(modo);
        List<String> puntajes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                puntajes.add(linea);
            }
        } catch (IOException e) {
        }
        return puntajes;
    }

    public static void guardarPuntaje(String nombre, int puntaje, String modo) {
        String archivo = obtenerArchivo(modo);
        List<String> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lista.add(linea);
            }
        } catch (IOException e) {

        }
        lista.add(nombre + " " + puntaje);
        lista.sort(Comparator.comparingInt(linea -> {
            String[] partes = linea.split(" ");
            return Integer.parseInt(partes[1]);
        }));

        if (lista.size() > 5) {
            lista = lista.subList(0, 5);
        }

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
            return modo;
        }
    }
}
