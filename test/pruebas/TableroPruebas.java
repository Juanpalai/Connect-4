package pruebas;

import logica.Tablero;

/**
 * Pruebas del estado y de las operaciones del tablero.
 */
public final class TableroPruebas {

    private TableroPruebas() {
    }

    public static void ejecutar() {
        Pruebas.ejecutar("Tablero: un tablero nuevo está vacío", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = new Tablero();
                for (int fila = 0; fila < Tablero.FILAS; fila++) {
                    for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
                        Pruebas.afirmarIgual(Tablero.VACIA, tablero.fichaEn(fila, columna),
                                "casilla vacía en [" + fila + "][" + columna + "]");
                    }
                }
                Pruebas.afirmar(!tablero.estaLleno(), "el tablero nuevo no está lleno");
                Pruebas.afirmarIgual(0, tablero.fichasColocadas(), "no hay fichas");
            }
        });

        Pruebas.ejecutar("Tablero: las fichas caen a la fila inferior", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = new Tablero();
                Pruebas.afirmarIgual(Tablero.FILAS - 1, tablero.filaDisponible(3),
                        "la primera ficha cae al fondo");

                Pruebas.afirmar(tablero.colocar(3, Tablero.HUMANO), "se coloca la primera ficha");
                Pruebas.afirmarIgual(Tablero.HUMANO, tablero.fichaEn(Tablero.FILAS - 1, 3),
                        "la ficha está en la fila inferior");
                Pruebas.afirmarIgual(Tablero.FILAS - 2, tablero.filaDisponible(3),
                        "la siguiente ficha cae encima");

                Pruebas.afirmar(tablero.colocar(3, Tablero.MAQUINA), "se coloca la segunda ficha");
                Pruebas.afirmarIgual(Tablero.MAQUINA, tablero.fichaEn(Tablero.FILAS - 2, 3),
                        "la segunda ficha queda encima de la primera");
                Pruebas.afirmarIgual(2, tablero.fichasColocadas(), "hay dos fichas");
            }
        });

        Pruebas.ejecutar("Tablero: una columna llena rechaza más fichas", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = new Tablero();
                for (int fila = 0; fila < Tablero.FILAS; fila++) {
                    int ficha = fila % 2 == 0 ? Tablero.HUMANO : Tablero.MAQUINA;
                    Pruebas.afirmar(tablero.colocar(0, ficha),
                            "se coloca la ficha " + fila + " de la columna 0");
                }
                Pruebas.afirmar(tablero.columnaLlena(0), "la columna 0 está llena");
                Pruebas.afirmarIgual(-1, tablero.filaDisponible(0),
                        "no hay fila disponible en una columna llena");
                Pruebas.afirmar(!tablero.colocar(0, Tablero.HUMANO),
                        "no se puede colocar en una columna llena");
                Pruebas.afirmarIgual(Tablero.FILAS, tablero.fichasColocadas(),
                        "el número de fichas no cambia");
            }
        });

        Pruebas.ejecutar("Tablero: columnas fuera de rango son inválidas", new Runnable() {
            @Override
            public void run() {
                final Tablero tablero = new Tablero();
                Pruebas.afirmarIgual(-1, tablero.filaDisponible(-1), "columna -1 inválida");
                Pruebas.afirmarIgual(-1, tablero.filaDisponible(Tablero.COLUMNAS),
                        "columna fuera del tablero inválida");
                Pruebas.afirmar(!tablero.colocar(-1, Tablero.HUMANO),
                        "no se coloca en una columna negativa");
                Pruebas.afirmar(!tablero.colocar(Tablero.COLUMNAS, Tablero.HUMANO),
                        "no se coloca en una columna fuera del tablero");
                Pruebas.afirmarIgual(0, tablero.fichasColocadas(),
                        "las jugadas inválidas no colocan fichas");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        tablero.colocar(0, Tablero.VACIA);
                    }
                }, "colocar una ficha vacía lanza IllegalArgumentException");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        tablero.colocar(0, 2);
                    }
                }, "colocar una ficha desconocida lanza IllegalArgumentException");
            }
        });

        Pruebas.ejecutar("Tablero: la copia es independiente", new Runnable() {
            @Override
            public void run() {
                Tablero original = new Tablero();
                original.colocar(2, Tablero.HUMANO);
                Tablero copia = new Tablero(original);

                Pruebas.afirmarIgual(Tablero.HUMANO, copia.fichaEn(Tablero.FILAS - 1, 2),
                        "la copia conserva la ficha");
                copia.colocar(2, Tablero.MAQUINA);

                Pruebas.afirmarIgual(Tablero.HUMANO,
                        original.fichaEn(Tablero.FILAS - 1, 2),
                        "modificar la copia no cambia el original");
                Pruebas.afirmarIgual(Tablero.VACIA,
                        original.fichaEn(Tablero.FILAS - 2, 2),
                        "la ficha nueva solo existe en la copia");
                Pruebas.afirmarIgual(Tablero.MAQUINA,
                        copia.fichaEn(Tablero.FILAS - 2, 2),
                        "la copia tiene su propia ficha");
                Pruebas.afirmarIgual(1, original.fichasColocadas(),
                        "el contador del original no cambia");
                Pruebas.afirmarIgual(2, copia.fichasColocadas(),
                        "el contador de la copia sí cambia");
            }
        });

        Pruebas.ejecutar("Tablero: desdeMatriz copia, cuenta y valida", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                Utilidades.poner(matriz, 6, 0, Tablero.HUMANO);
                Utilidades.poner(matriz, 5, 0, Tablero.MAQUINA);
                Tablero tablero = Utilidades.tablero(matriz);

                Pruebas.afirmarIgual(2, tablero.fichasColocadas(), "cuenta las fichas de la matriz");
                matriz[6][0] = Tablero.MAQUINA;
                Pruebas.afirmarIgual(Tablero.HUMANO, tablero.fichaEn(6, 0),
                        "la matriz original no comparte estado");

                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        Utilidades.tablero(new int[Tablero.FILAS - 1][Tablero.COLUMNAS]);
                    }
                }, "una matriz con filas incorrectas es rechazada");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        Utilidades.tablero(new int[Tablero.FILAS][Tablero.COLUMNAS - 1]);
                    }
                }, "una matriz con columnas incorrectas es rechazada");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        int[][] invalida = Utilidades.matrizVacia();
                        invalida[0][0] = 7;
                        Utilidades.tablero(invalida);
                    }
                }, "una ficha desconocida en la matriz es rechazada");
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        Tablero.desdeMatriz(null);
                    }
                }, "una matriz nula es rechazada");
            }
        });

        Pruebas.ejecutar("Tablero: estaLleno y limpiar", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = new Tablero();
                for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
                    for (int fila = 0; fila < Tablero.FILAS; fila++) {
                        int ficha = (fila + columna) % 2 == 0
                                ? Tablero.HUMANO : Tablero.MAQUINA;
                        Pruebas.afirmar(tablero.colocar(columna, ficha),
                                "se llena la columna " + columna);
                    }
                }
                Pruebas.afirmar(tablero.estaLleno(), "el tablero está lleno");
                Pruebas.afirmarIgual(Tablero.FILAS * Tablero.COLUMNAS,
                        tablero.fichasColocadas(), "hay 49 fichas");

                tablero.limpiar();
                Pruebas.afirmar(!tablero.estaLleno(), "tras limpiar no está lleno");
                Pruebas.afirmarIgual(0, tablero.fichasColocadas(), "tras limpiar no hay fichas");
                Pruebas.afirmarIgual(Tablero.VACIA, tablero.fichaEn(3, 3),
                        "las casillas quedan vacías");
            }
        });

        Pruebas.ejecutar("Tablero: fichaEn valida los índices", new Runnable() {
            @Override
            public void run() {
                final Tablero tablero = new Tablero();
                Pruebas.afirmarLanza(IndexOutOfBoundsException.class, new Runnable() {
                    @Override
                    public void run() {
                        tablero.fichaEn(-1, 0);
                    }
                }, "fila negativa");
                Pruebas.afirmarLanza(IndexOutOfBoundsException.class, new Runnable() {
                    @Override
                    public void run() {
                        tablero.fichaEn(0, Tablero.COLUMNAS);
                    }
                }, "columna fuera del tablero");
            }
        });
    }
}
