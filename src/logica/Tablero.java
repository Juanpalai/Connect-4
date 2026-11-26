package logica;

/**
 * Estado de un tablero de Cuatro en Raya.
 *
 * <p>La fila 0 es la fila superior y la fila {@link #FILAS}-1 es la fila
 * inferior, que es donde caen las fichas. Esta clase no depende de AWT ni de
 * Swing: solo contiene el estado y las operaciones básicas del tablero.</p>
 */
public final class Tablero {

    /** Número de filas del tablero. */
    public static final int FILAS = 7;

    /** Número de columnas del tablero. */
    public static final int COLUMNAS = 7;

    /** Valor de una casilla vacía. */
    public static final int VACIA = 0;

    /** Ficha del jugador humano (roja). */
    public static final int HUMANO = -1;

    /** Ficha de la máquina (azul). */
    public static final int MAQUINA = 1;

    private final int[][] celdas;
    private int fichasColocadas;

    /** Crea un tablero vacío. */
    public Tablero() {
        celdas = new int[FILAS][COLUMNAS];
    }

    /**
     * Crea una copia independiente de otro tablero.
     *
     * @param otro tablero que se desea copiar
     */
    public Tablero(Tablero otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El tablero de origen no puede ser nulo");
        }
        celdas = new int[FILAS][COLUMNAS];
        for (int fila = 0; fila < FILAS; fila++) {
            System.arraycopy(otro.celdas[fila], 0, celdas[fila], 0, COLUMNAS);
        }
        fichasColocadas = otro.fichasColocadas;
    }

    private Tablero(int[][] celdas, int fichasColocadas) {
        this.celdas = celdas;
        this.fichasColocadas = fichasColocadas;
    }

    /**
     * Construye un tablero a partir de una matriz de {@code FILAS} x
     * {@code COLUMNAS}. La matriz se copia y no se modifica.
     *
     * <p>Este método está pensado para pruebas y para cargar posiciones
     * concretas; no valida que las fichas respeten la gravedad.</p>
     *
     * @param matriz matriz con valores {@link #VACIA}, {@link #HUMANO} o
     *               {@link #MAQUINA}
     * @return un tablero nuevo con el contenido indicado
     * @throws IllegalArgumentException si las dimensiones o los valores no son
     *                                  válidos
     */
    public static Tablero desdeMatriz(int[][] matriz) {
        if (matriz == null || matriz.length != FILAS) {
            throw new IllegalArgumentException("La matriz debe tener " + FILAS + " filas");
        }
        int[][] copia = new int[FILAS][COLUMNAS];
        int total = 0;
        for (int fila = 0; fila < FILAS; fila++) {
            if (matriz[fila] == null || matriz[fila].length != COLUMNAS) {
                throw new IllegalArgumentException("Cada fila debe tener " + COLUMNAS + " columnas");
            }
            for (int columna = 0; columna < COLUMNAS; columna++) {
                int ficha = matriz[fila][columna];
                if (ficha != VACIA && ficha != HUMANO && ficha != MAQUINA) {
                    throw new IllegalArgumentException("Ficha no válida en la posición ["
                            + fila + "][" + columna + "]: " + ficha);
                }
                copia[fila][columna] = ficha;
                if (ficha != VACIA) {
                    total++;
                }
            }
        }
        return new Tablero(copia, total);
    }

    /**
     * Devuelve la ficha que ocupa una casilla.
     *
     * @param fila    fila, de 0 (superior) a {@link #FILAS}-1 (inferior)
     * @param columna columna, de 0 a {@link #COLUMNAS}-1
     * @return {@link #VACIA}, {@link #HUMANO} o {@link #MAQUINA}
     */
    public int fichaEn(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            throw new IndexOutOfBoundsException("Casilla fuera del tablero: ["
                    + fila + "][" + columna + "]");
        }
        return celdas[fila][columna];
    }

    /**
     * Indica la fila donde caería una ficha en la columna indicada.
     *
     * @param columna columna que se desea consultar
     * @return la fila inferior libre, o {@code -1} si la columna no existe o
     *         está llena
     */
    public int filaDisponible(int columna) {
        if (columna < 0 || columna >= COLUMNAS) {
            return -1;
        }
        for (int fila = FILAS - 1; fila >= 0; fila--) {
            if (celdas[fila][columna] == VACIA) {
                return fila;
            }
        }
        return -1;
    }

    /**
     * Indica si una columna está llena.
     *
     * @param columna columna que se desea consultar
     * @return {@code true} si no queda ninguna casilla libre
     */
    public boolean columnaLlena(int columna) {
        return filaDisponible(columna) < 0;
    }

    /**
     * Coloca una ficha en la primera casilla libre de una columna, respetando
     * la gravedad.
     *
     * @param columna columna donde se deja caer la ficha
     * @param ficha   {@link #HUMANO} o {@link #MAQUINA}
     * @return {@code true} si la ficha se colocó; {@code false} si la columna
     *         no existe o está llena
     * @throws IllegalArgumentException si la ficha no es válida
     */
    public boolean colocar(int columna, int ficha) {
        if (ficha != HUMANO && ficha != MAQUINA) {
            throw new IllegalArgumentException("Ficha no válida: " + ficha);
        }
        int fila = filaDisponible(columna);
        if (fila < 0) {
            return false;
        }
        celdas[fila][columna] = ficha;
        fichasColocadas++;
        return true;
    }

    /**
     * Indica si ya no quedan casillas libres.
     *
     * @return {@code true} si el tablero está completo
     */
    public boolean estaLleno() {
        return fichasColocadas == FILAS * COLUMNAS;
    }

    /**
     * Devuelve cuántas fichas hay en el tablero.
     *
     * @return número de fichas colocadas
     */
    public int fichasColocadas() {
        return fichasColocadas;
    }

    /** Vacía todas las casillas del tablero. */
    public void limpiar() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                celdas[fila][columna] = VACIA;
            }
        }
        fichasColocadas = 0;
    }
}
