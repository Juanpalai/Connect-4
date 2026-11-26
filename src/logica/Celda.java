package logica;

/**
 * Coordenada de una casilla del tablero.
 */
public final class Celda {

    private final int fila;
    private final int columna;

    /**
     * Crea una coordenada.
     *
     * @param fila    fila de la casilla
     * @param columna columna de la casilla
     */
    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Devuelve la fila de la casilla.
     *
     * @return la fila
     */
    public int fila() {
        return fila;
    }

    /**
     * Devuelve la columna de la casilla.
     *
     * @return la columna
     */
    public int columna() {
        return columna;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Celda)) {
            return false;
        }
        Celda celda = (Celda) otro;
        return fila == celda.fila && columna == celda.columna;
    }

    @Override
    public int hashCode() {
        return 31 * fila + columna;
    }

    @Override
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
}
