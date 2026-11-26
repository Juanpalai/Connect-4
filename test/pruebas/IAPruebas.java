package pruebas;

import logica.Arbitro;
import logica.IA;
import logica.Tablero;

/**
 * Pruebas tácticas del motor minimax.
 */
public final class IAPruebas {

    private IAPruebas() {
    }

    public static void ejecutar() {
        Pruebas.ejecutar("IA: devuelve una columna válida en un tablero vacío", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = new Tablero();
                IA ia = new IA();
                int columna = ia.elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(3, columna,
                        "en un tablero vacío prefiere la columna central");
                Pruebas.afirmar(columna >= 0 && columna < Tablero.COLUMNAS,
                        "la columna elegida está dentro del tablero");
                Pruebas.afirmar(!tablero.columnaLlena(columna),
                        "la columna elegida tiene hueco");
                Pruebas.afirmarIgual(Tablero.VACIA, tablero.fichaEn(6, columna),
                        "el tablero original sigue vacío");
                Pruebas.afirmarIgual(0, tablero.fichasColocadas(),
                        "la simulación no coloca fichas en el tablero original");
                Pruebas.afirmarIgual(IA.PROFUNDIDAD_POR_DEFECTO, ia.getProfundidad(),
                        "la profundidad por defecto es la esperada");
            }
        });

        Pruebas.ejecutar("IA: gana cuando puede completar cuatro", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.MAQUINA);
                }
                Utilidades.poner(matriz, 6, 6, Tablero.HUMANO);
                Tablero tablero = Utilidades.tablero(matriz);
                afirmarSinGanador(tablero);

                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(3, columna, "la máquina completa la línea en la columna 3");
            }
        });

        Pruebas.ejecutar("IA: bloquea una amenaza horizontal", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.HUMANO);
                }
                Utilidades.poner(matriz, 6, 6, Tablero.MAQUINA);
                Tablero tablero = Utilidades.tablero(matriz);
                afirmarSinGanador(tablero);

                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(3, columna, "la máquina bloquea la columna 3");
            }
        });

        Pruebas.ejecutar("IA: bloquea una amenaza vertical", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int fila = 4; fila < Tablero.FILAS; fila++) {
                    Utilidades.poner(matriz, fila, 0, Tablero.HUMANO);
                }
                Utilidades.poner(matriz, 6, 6, Tablero.MAQUINA);
                Tablero tablero = Utilidades.tablero(matriz);
                afirmarSinGanador(tablero);

                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(0, columna, "la máquina bloquea la columna 0");
            }
        });

        Pruebas.ejecutar("IA: prefiere ganar antes que bloquear", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.MAQUINA);
                }
                for (int fila = 4; fila < Tablero.FILAS; fila++) {
                    Utilidades.poner(matriz, fila, 6, Tablero.HUMANO);
                }
                Tablero tablero = Utilidades.tablero(matriz);
                afirmarSinGanador(tablero);

                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(3, columna, "la máquina gana en la columna 3");
            }
        });

        Pruebas.ejecutar("IA: bloquea una amenaza diagonal descendente", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                // Amenaza humana: (3,0), (4,1), (5,2) -> (6,3).
                Utilidades.poner(matriz, 3, 0, Tablero.HUMANO);
                Utilidades.poner(matriz, 4, 1, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 2, Tablero.HUMANO);
                // Soportes que respetan la gravedad y no crean otras amenazas.
                Utilidades.poner(matriz, 4, 0, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 0, Tablero.MAQUINA);
                Utilidades.poner(matriz, 6, 0, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 1, Tablero.HUMANO);
                Utilidades.poner(matriz, 6, 1, Tablero.MAQUINA);
                Utilidades.poner(matriz, 6, 2, Tablero.HUMANO);
                Tablero tablero = Utilidades.tablero(matriz);
                afirmarSinGanador(tablero);

                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(3, columna, "la máquina bloquea la diagonal en la columna 3");
            }
        });

        Pruebas.ejecutar("IA: bloquea una amenaza diagonal ascendente", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                // Amenaza humana: (3,3), (4,2), (5,1) -> (6,0).
                Utilidades.poner(matriz, 3, 3, Tablero.HUMANO);
                Utilidades.poner(matriz, 4, 2, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 1, Tablero.HUMANO);
                // Soportes que respetan la gravedad y no crean otras amenazas.
                Utilidades.poner(matriz, 6, 1, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 2, Tablero.HUMANO);
                Utilidades.poner(matriz, 6, 2, Tablero.MAQUINA);
                Utilidades.poner(matriz, 4, 3, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 3, Tablero.MAQUINA);
                Utilidades.poner(matriz, 6, 3, Tablero.HUMANO);
                Tablero tablero = Utilidades.tablero(matriz);
                afirmarSinGanador(tablero);

                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(0, columna, "la máquina bloquea la diagonal en la columna 0");
            }
        });

        Pruebas.ejecutar("IA: no elige una columna llena", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = Utilidades.tableroCompletoSinCuatroConHueco(0, 0);
                afirmarSinGanador(tablero);
                int columna = new IA().elegirColumna(tablero, Tablero.MAQUINA);
                Pruebas.afirmarIgual(0, columna, "solo queda libre la columna 0");
            }
        });

        Pruebas.ejecutar("IA: un tablero lleno devuelve -1", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = Utilidades.tableroCompletoSinCuatro();
                Pruebas.afirmar(tablero.estaLleno(), "el tablero está lleno");
                Pruebas.afirmarIgual(-1, new IA().elegirColumna(tablero, Tablero.MAQUINA),
                        "no hay columna posible");
            }
        });

        Pruebas.ejecutar("IA: valida profundidad y ficha", new Runnable() {
            @Override
            public void run() {
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        new IA(0);
                    }
                }, "profundidad cero no es válida");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        new IA(-2);
                    }
                }, "profundidad negativa no es válida");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        new IA().elegirColumna(new Tablero(), Tablero.VACIA);
                    }
                }, "ficha vacía no es válida");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        new IA().elegirColumna(null, Tablero.MAQUINA);
                    }
                }, "tablero nulo no es válido");
                Pruebas.afirmarIgual(4, new IA(4).getProfundidad(),
                        "la profundidad se conserva");
            }
        });
    }

    private static void afirmarSinGanador(Tablero tablero) {
        Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.HUMANO),
                "la posición de prueba no tiene victoria humana");
        Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.MAQUINA),
                "la posición de prueba no tiene victoria de la máquina");
    }
}
