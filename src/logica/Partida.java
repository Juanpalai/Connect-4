package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Desarrollo de una partida: turnos, jugadas válidas, marcador y estado final.
 *
 * <p>No contiene ninguna dependencia de AWT ni de Swing. La interfaz gráfica
 * se limita a consultar el estado y a invocar {@link #jugar(int)}.</p>
 */
public final class Partida {

    private final Tablero tablero;
    private final List<Celda> lineaGanadora = new ArrayList<Celda>();

    private int turnoActual;
    private EstadoPartida estado;
    private int victoriasHumano;
    private int victoriasMaquina;
    private int ultimaFila;
    private int ultimaColumna;

    /** Crea una partida nueva con el tablero vacío y el turno del humano. */
    public Partida() {
        tablero = new Tablero();
        reiniciar();
    }

    /**
     * Crea una partida a partir de una posición concreta. Pensado para pruebas
     * y para reanudar posiciones; el tablero se copia.
     *
     * @param tableroInicial posición inicial
     * @param turnoInicial   {@link Tablero#HUMANO} o {@link Tablero#MAQUINA}
     */
    public Partida(Tablero tableroInicial, int turnoInicial) {
        if (tableroInicial == null) {
            throw new IllegalArgumentException("El tablero inicial no puede ser nulo");
        }
        if (turnoInicial != Tablero.HUMANO && turnoInicial != Tablero.MAQUINA) {
            throw new IllegalArgumentException("Turno inicial no válido: " + turnoInicial);
        }
        tablero = new Tablero(tableroInicial);
        turnoActual = turnoInicial;
        ultimaFila = -1;
        ultimaColumna = -1;
        estado = calcularEstadoInicial();
        if (estado.hayGanador()) {
            localizarLineaGanadora(estado == EstadoPartida.VICTORIA_HUMANO
                    ? Tablero.HUMANO : Tablero.MAQUINA);
        }
    }

    private EstadoPartida calcularEstadoInicial() {
        if (Arbitro.hayVictoria(tablero, Tablero.HUMANO)) {
            return EstadoPartida.VICTORIA_HUMANO;
        }
        if (Arbitro.hayVictoria(tablero, Tablero.MAQUINA)) {
            return EstadoPartida.VICTORIA_MAQUINA;
        }
        if (tablero.estaLleno()) {
            return EstadoPartida.EMPATE;
        }
        return EstadoPartida.EN_CURSO;
    }

    private void localizarLineaGanadora(int ficha) {
        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
                if (tablero.fichaEn(fila, columna) == ficha) {
                    List<Celda> linea = Arbitro.lineaGanadora(tablero, fila, columna);
                    if (!linea.isEmpty()) {
                        lineaGanadora.addAll(linea);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Intenta colocar una ficha del jugador al que le toca.
     *
     * @param columna columna elegida
     * @return {@code true} si la jugada era válida y se ha aplicado
     */
    public boolean jugar(int columna) {
        if (estado != EstadoPartida.EN_CURSO) {
            return false;
        }
        int fila = tablero.filaDisponible(columna);
        if (fila < 0) {
            return false;
        }

        tablero.colocar(columna, turnoActual);
        ultimaFila = fila;
        ultimaColumna = columna;

        List<Celda> linea = Arbitro.lineaGanadora(tablero, fila, columna);
        if (!linea.isEmpty()) {
            lineaGanadora.clear();
            lineaGanadora.addAll(linea);
            if (turnoActual == Tablero.HUMANO) {
                estado = EstadoPartida.VICTORIA_HUMANO;
                victoriasHumano++;
            } else {
                estado = EstadoPartida.VICTORIA_MAQUINA;
                victoriasMaquina++;
            }
            return true;
        }

        if (tablero.estaLleno()) {
            estado = EstadoPartida.EMPATE;
            return true;
        }

        turnoActual = turnoActual == Tablero.HUMANO ? Tablero.MAQUINA : Tablero.HUMANO;
        return true;
    }

    /**
     * Juega una columna solo si es el turno del humano.
     *
     * @param columna columna elegida
     * @return {@code true} si la jugada se ha aplicado
     */
    public boolean jugarHumano(int columna) {
        return turnoActual == Tablero.HUMANO && jugar(columna);
    }

    /**
     * Indica si se puede colocar una ficha en la columna indicada.
     *
     * @param columna columna que se desea comprobar
     * @return {@code true} si la partida sigue y la columna tiene hueco
     */
    public boolean puedeJugar(int columna) {
        return estado == EstadoPartida.EN_CURSO && tablero.filaDisponible(columna) >= 0;
    }

    /**
     * Reinicia el tablero y los turnos, conservando el marcador histórico.
     */
    public void reiniciar() {
        tablero.limpiar();
        turnoActual = Tablero.HUMANO;
        estado = EstadoPartida.EN_CURSO;
        lineaGanadora.clear();
        ultimaFila = -1;
        ultimaColumna = -1;
    }

    /**
     * Devuelve el tablero actual. La interfaz solo debe leerlo; para simular
     * jugadas se usa una copia.
     *
     * @return el tablero actual
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Devuelve la ficha del jugador al que le toca mover.
     *
     * @return {@link Tablero#HUMANO} o {@link Tablero#MAQUINA}
     */
    public int getTurnoActual() {
        return turnoActual;
    }

    /**
     * Indica si le toca al humano.
     *
     * @return {@code true} si el turno es del humano
     */
    public boolean esTurnoHumano() {
        return turnoActual == Tablero.HUMANO;
    }

    /**
     * Devuelve el estado actual de la partida.
     *
     * @return el estado de la partida
     */
    public EstadoPartida getEstado() {
        return estado;
    }

    /**
     * Devuelve la línea ganadora.
     *
     * @return las celdas ganadoras, o una lista vacía si todavía no hay ganador
     */
    public List<Celda> getLineaGanadora() {
        return Collections.unmodifiableList(lineaGanadora);
    }

    /**
     * Devuelve la fila de la última ficha colocada.
     *
     * @return la fila, o {@code -1} si todavía no se ha jugado
     */
    public int getUltimaFila() {
        return ultimaFila;
    }

    /**
     * Devuelve la columna de la última ficha colocada.
     *
     * @return la columna, o {@code -1} si todavía no se ha jugado
     */
    public int getUltimaColumna() {
        return ultimaColumna;
    }

    /**
     * Devuelve las victorias del jugador humano.
     *
     * @return número de victorias
     */
    public int getVictoriasHumano() {
        return victoriasHumano;
    }

    /**
     * Devuelve las victorias de la máquina.
     *
     * @return número de derrotas del humano
     */
    public int getVictoriasMaquina() {
        return victoriasMaquina;
    }
}
