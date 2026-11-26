package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reglas puras de Cuatro en Raya: detección de victorias y empates.
 *
 * <p>Esta clase no guarda estado ni depende de AWT/Swing.</p>
 */
public final class Arbitro {

    /** Número de fichas consecutivas necesarias para ganar. */
    public static final int FICHAS_PARA_GANAR = 4;

    /**
     * Direcciones en las que se puede formar una línea: horizontal, vertical,
     * diagonal descendente y diagonal ascendente.
     */
    private static final int[][] DIRECCIONES = {
        {0, 1},
        {1, 0},
        {1, 1},
        {1, -1}
    };

    private Arbitro() {
    }

    /**
     * Comprueba si la ficha situada en {@code (fila, columna)} forma parte de
     * una línea ganadora.
     *
     * @param tablero tablero que se desea comprobar
     * @param fila    fila de la última ficha colocada
     * @param columna columna de la última ficha colocada
     * @return la línea completa que contiene esa ficha, o una lista vacía si
     *         no hay victoria
     */
    public static List<Celda> lineaGanadora(Tablero tablero, int fila, int columna) {
        int ficha = tablero.fichaEn(fila, columna);
        if (ficha == Tablero.VACIA) {
            return Collections.emptyList();
        }
        for (int[] direccion : DIRECCIONES) {
            List<Celda> linea = construirLinea(tablero, fila, columna,
                    direccion[0], direccion[1], ficha);
            if (linea.size() >= FICHAS_PARA_GANAR) {
                return linea;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Comprueba si un jugador tiene alguna línea ganadora en el tablero.
     *
     * @param tablero tablero que se desea comprobar
     * @param ficha   {@link Tablero#HUMANO} o {@link Tablero#MAQUINA}
     * @return {@code true} si hay al menos cuatro fichas consecutivas
     */
    public static boolean hayVictoria(Tablero tablero, int ficha) {
        if (!esFichaDeJuego(ficha)) {
            throw new IllegalArgumentException("Ficha no válida: " + ficha);
        }
        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
                if (tablero.fichaEn(fila, columna) == ficha
                        && !lineaGanadora(tablero, fila, columna).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Comprueba si el tablero está lleno y nadie ha ganado.
     *
     * @param tablero tablero que se desea comprobar
     * @return {@code true} si la partida termina en empate
     */
    public static boolean hayEmpate(Tablero tablero) {
        return tablero.estaLleno()
                && !hayVictoria(tablero, Tablero.HUMANO)
                && !hayVictoria(tablero, Tablero.MAQUINA);
    }

    private static List<Celda> construirLinea(Tablero tablero, int fila, int columna,
            int deltaFila, int deltaColumna, int ficha) {
        int inicioFila = fila;
        int inicioColumna = columna;
        while (dentroDelTablero(inicioFila - deltaFila, inicioColumna - deltaColumna)
                && tablero.fichaEn(inicioFila - deltaFila, inicioColumna - deltaColumna) == ficha) {
            inicioFila -= deltaFila;
            inicioColumna -= deltaColumna;
        }

        List<Celda> linea = new ArrayList<Celda>();
        int actualFila = inicioFila;
        int actualColumna = inicioColumna;
        while (dentroDelTablero(actualFila, actualColumna)
                && tablero.fichaEn(actualFila, actualColumna) == ficha) {
            linea.add(new Celda(actualFila, actualColumna));
            actualFila += deltaFila;
            actualColumna += deltaColumna;
        }
        return linea;
    }

    private static boolean dentroDelTablero(int fila, int columna) {
        return fila >= 0 && fila < Tablero.FILAS
                && columna >= 0 && columna < Tablero.COLUMNAS;
    }

    private static boolean esFichaDeJuego(int ficha) {
        return ficha == Tablero.HUMANO || ficha == Tablero.MAQUINA;
    }
}
