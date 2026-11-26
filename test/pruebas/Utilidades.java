package pruebas;

import logica.Tablero;

/**
 * Utilidades compartidas por las pruebas. No contienen lógica de producción.
 */
final class Utilidades {

    /**
     * Patrón completo de 7x7 sin cuatro fichas iguales consecutivas. Los 1 se
     * convierten en fichas de la máquina y los 0 en fichas del humano.
     */
    private static final int[][] PATRON_SIN_CUATRO = {
        {1, 0, 0, 0, 1, 0, 0},
        {0, 0, 1, 0, 1, 0, 0},
        {0, 0, 0, 1, 1, 0, 0},
        {1, 1, 0, 0, 0, 1, 1},
        {1, 1, 0, 1, 1, 0, 1},
        {0, 0, 1, 1, 0, 1, 1},
        {1, 1, 0, 0, 1, 0, 0}
    };

    private Utilidades() {
    }

    static int[][] matrizVacia() {
        return new int[Tablero.FILAS][Tablero.COLUMNAS];
    }

    static void poner(int[][] matriz, int fila, int columna, int ficha) {
        matriz[fila][columna] = ficha;
    }

    static Tablero tablero(int[][] matriz) {
        return Tablero.desdeMatriz(matriz);
    }

    static int[][] matrizCompletaSinCuatro() {
        int[][] matriz = new int[Tablero.FILAS][Tablero.COLUMNAS];
        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
                matriz[fila][columna] = PATRON_SIN_CUATRO[fila][columna] == 1
                        ? Tablero.MAQUINA : Tablero.HUMANO;
            }
        }
        return matriz;
    }

    static Tablero tableroCompletoSinCuatro() {
        return Tablero.desdeMatriz(matrizCompletaSinCuatro());
    }

    static Tablero tableroCompletoSinCuatroConHueco(int fila, int columna) {
        int[][] matriz = matrizCompletaSinCuatro();
        matriz[fila][columna] = Tablero.VACIA;
        return Tablero.desdeMatriz(matriz);
    }
}
