package logica;

/**
 * Motor de la máquina basado en minimax con poda alfa-beta.
 *
 * <p>El algoritmo es determinista y no usa librerías externas. Se exploran
 * las columnas de centro hacia fuera para mejorar la poda y para desempatar
 * hacia el centro.</p>
 */
public final class IA {

    /** Profundidad de búsqueda usada por defecto. */
    public static final int PROFUNDIDAD_POR_DEFECTO = 6;

    private static final int VALOR_VICTORIA = 1_000_000;
    private static final int BONUS_CENTRO = 6;
    private static final int[] VALOR_VENTANA = {0, 1, 10, 50, VALOR_VICTORIA};
    private static final int[] ORDEN_COLUMNAS = crearOrdenColumnas();

    private final int profundidad;

    /** Crea una IA con la profundidad por defecto. */
    public IA() {
        this(PROFUNDIDAD_POR_DEFECTO);
    }

    /**
     * Crea una IA con una profundidad de búsqueda concreta.
     *
     * @param profundidad número de jugadas que se anticipan (mínimo 1)
     */
    public IA(int profundidad) {
        if (profundidad < 1) {
            throw new IllegalArgumentException("La profundidad debe ser al menos 1");
        }
        this.profundidad = profundidad;
    }

    /**
     * Elige la mejor columna para la ficha indicada.
     *
     * @param tablero posición actual; no se modifica
     * @param ficha   {@link Tablero#HUMANO} o {@link Tablero#MAQUINA}
     * @return la columna elegida, o {@code -1} si el tablero está lleno
     */
    public int elegirColumna(Tablero tablero, int ficha) {
        if (tablero == null) {
            throw new IllegalArgumentException("El tablero no puede ser nulo");
        }
        if (!esFichaDeJuego(ficha)) {
            throw new IllegalArgumentException("Ficha no válida: " + ficha);
        }
        if (tablero.estaLleno()) {
            return -1;
        }

        int mejorColumna = -1;
        int mejorValor = Integer.MIN_VALUE;
        for (int columna : ORDEN_COLUMNAS) {
            if (tablero.columnaLlena(columna)) {
                continue;
            }

            Tablero copia = new Tablero(tablero);
            int fila = copia.filaDisponible(columna);
            copia.colocar(columna, ficha);

            int valor;
            if (!Arbitro.lineaGanadora(copia, fila, columna).isEmpty()) {
                valor = VALOR_VICTORIA - 1;
            } else if (copia.estaLleno()) {
                valor = 0;
            } else {
                valor = minimax(copia, profundidad - 1,
                        Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1,
                        oponente(ficha), ficha, 1);
            }

            if (valor > mejorValor) {
                mejorValor = valor;
                mejorColumna = columna;
            }
        }
        return mejorColumna;
    }

    /**
     * Devuelve la profundidad de búsqueda configurada.
     *
     * @return número de jugadas que se anticipan
     */
    public int getProfundidad() {
        return profundidad;
    }

    private int minimax(Tablero tablero, int profundidadRestante, int alfa, int beta,
            int turno, int fichaMax, int jugadas) {
        if (tablero.estaLleno()) {
            return 0;
        }
        if (profundidadRestante <= 0) {
            return evaluar(tablero, fichaMax);
        }

        if (turno == fichaMax) {
            int mejorValor = Integer.MIN_VALUE;
            for (int columna : ORDEN_COLUMNAS) {
                if (tablero.columnaLlena(columna)) {
                    continue;
                }
                int valor = valorJugada(tablero, columna, turno, fichaMax, jugadas,
                        profundidadRestante, alfa, beta);
                if (valor > mejorValor) {
                    mejorValor = valor;
                }
                if (valor > alfa) {
                    alfa = valor;
                }
                if (alfa >= beta) {
                    break;
                }
            }
            return mejorValor;
        }

        int mejorValor = Integer.MAX_VALUE;
        for (int columna : ORDEN_COLUMNAS) {
            if (tablero.columnaLlena(columna)) {
                continue;
            }
            int valor = valorJugada(tablero, columna, turno, fichaMax, jugadas,
                    profundidadRestante, alfa, beta);
            if (valor < mejorValor) {
                mejorValor = valor;
            }
            if (valor < beta) {
                beta = valor;
            }
            if (alfa >= beta) {
                break;
            }
        }
        return mejorValor;
    }

    private int valorJugada(Tablero tablero, int columna, int turno, int fichaMax,
            int jugadas, int profundidadRestante, int alfa, int beta) {
        Tablero copia = new Tablero(tablero);
        int fila = copia.filaDisponible(columna);
        copia.colocar(columna, turno);
        int nuevasJugadas = jugadas + 1;

        if (!Arbitro.lineaGanadora(copia, fila, columna).isEmpty()) {
            return turno == fichaMax
                    ? VALOR_VICTORIA - nuevasJugadas
                    : -VALOR_VICTORIA + nuevasJugadas;
        }
        if (copia.estaLleno()) {
            return 0;
        }
        return minimax(copia, profundidadRestante - 1, alfa, beta,
                oponente(turno), fichaMax, nuevasJugadas);
    }

    private int evaluar(Tablero tablero, int fichaMax) {
        int puntaje = 0;

        int fichasEnLinea = Arbitro.FICHAS_PARA_GANAR;
        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            for (int columna = 0; columna <= Tablero.COLUMNAS - fichasEnLinea; columna++) {
                puntaje += valorVentana(tablero, fila, columna, 0, 1, fichaMax);
            }
        }
        for (int fila = 0; fila <= Tablero.FILAS - fichasEnLinea; fila++) {
            for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
                puntaje += valorVentana(tablero, fila, columna, 1, 0, fichaMax);
            }
        }
        for (int fila = 0; fila <= Tablero.FILAS - fichasEnLinea; fila++) {
            for (int columna = 0; columna <= Tablero.COLUMNAS - fichasEnLinea; columna++) {
                puntaje += valorVentana(tablero, fila, columna, 1, 1, fichaMax);
                puntaje += valorVentana(tablero, fila,
                        columna + fichasEnLinea - 1, 1, -1, fichaMax);
            }
        }

        int columnaCentral = Tablero.COLUMNAS / 2;
        for (int fila = 0; fila < Tablero.FILAS; fila++) {
            int ficha = tablero.fichaEn(fila, columnaCentral);
            if (ficha == fichaMax) {
                puntaje += BONUS_CENTRO;
            } else if (ficha == oponente(fichaMax)) {
                puntaje -= BONUS_CENTRO;
            }
        }
        return puntaje;
    }

    private int valorVentana(Tablero tablero, int fila, int columna,
            int deltaFila, int deltaColumna, int fichaMax) {
        int fichaRival = oponente(fichaMax);
        int mias = 0;
        int rivales = 0;
        for (int desplazamiento = 0; desplazamiento < Arbitro.FICHAS_PARA_GANAR;
                desplazamiento++) {
            int ficha = tablero.fichaEn(fila + deltaFila * desplazamiento,
                    columna + deltaColumna * desplazamiento);
            if (ficha == fichaMax) {
                mias++;
            } else if (ficha == fichaRival) {
                rivales++;
            }
        }

        if (mias > 0 && rivales > 0) {
            return 0;
        }
        if (mias > 0) {
            return VALOR_VENTANA[mias];
        }
        if (rivales > 0) {
            return -VALOR_VENTANA[rivales];
        }
        return 0;
    }

    private static int[] crearOrdenColumnas() {
        int[] orden = new int[Tablero.COLUMNAS];
        int centro = Tablero.COLUMNAS / 2;
        int indice = 0;
        orden[indice++] = centro;
        int distancia = 1;
        while (indice < orden.length) {
            int derecha = centro + distancia;
            if (derecha < Tablero.COLUMNAS) {
                orden[indice++] = derecha;
            }
            int izquierda = centro - distancia;
            if (izquierda >= 0 && indice < orden.length) {
                orden[indice++] = izquierda;
            }
            distancia++;
        }
        return orden;
    }

    private static int oponente(int ficha) {
        return ficha == Tablero.HUMANO ? Tablero.MAQUINA : Tablero.HUMANO;
    }

    private static boolean esFichaDeJuego(int ficha) {
        return ficha == Tablero.HUMANO || ficha == Tablero.MAQUINA;
    }
}
