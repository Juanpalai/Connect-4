package logica;

/**
 * Estados posibles de una partida.
 */
public enum EstadoPartida {

    /** La partida sigue en curso. */
    EN_CURSO,

    /** El jugador humano ha ganado. */
    VICTORIA_HUMANO,

    /** La máquina ha ganado. */
    VICTORIA_MAQUINA,

    /** El tablero se ha llenado sin ganador. */
    EMPATE;

    /**
     * Indica si la partida ya ha terminado.
     *
     * @return {@code true} si hay ganador o empate
     */
    public boolean haTerminado() {
        return this != EN_CURSO;
    }

    /**
     * Indica si el estado representa una victoria.
     *
     * @return {@code true} si gana el humano o la máquina
     */
    public boolean hayGanador() {
        return this == VICTORIA_HUMANO || this == VICTORIA_MAQUINA;
    }
}
