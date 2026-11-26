package pruebas;

import java.util.List;

import logica.Arbitro;
import logica.Celda;
import logica.Tablero;

/**
 * Pruebas de detección de victorias y empates.
 */
public final class ArbitroPruebas {

    private ArbitroPruebas() {
    }

    public static void ejecutar() {
        Pruebas.ejecutar("Arbitro: victoria horizontal", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 4; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.HUMANO);
                }
                comprobarLinea("horizontal inferior", matriz, Tablero.HUMANO, 6, 0, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: victoria horizontal en el borde superior", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 3; columna < Tablero.COLUMNAS; columna++) {
                    Utilidades.poner(matriz, 0, columna, Tablero.MAQUINA);
                }
                comprobarLinea("horizontal superior derecha", matriz, Tablero.MAQUINA, 0, 6, 4);
                comprobarLinea("horizontal superior izquierda", matriz, Tablero.MAQUINA, 0, 3, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: victoria vertical", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int fila = 3; fila < Tablero.FILAS; fila++) {
                    Utilidades.poner(matriz, fila, 0, Tablero.HUMANO);
                }
                comprobarLinea("vertical izquierda", matriz, Tablero.HUMANO, 6, 0, 4);
                comprobarLinea("vertical izquierda desde arriba", matriz, Tablero.HUMANO, 3, 0, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: victoria vertical en el borde derecho", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int fila = 0; fila < 4; fila++) {
                    Utilidades.poner(matriz, fila, 6, Tablero.MAQUINA);
                }
                comprobarLinea("vertical derecha", matriz, Tablero.MAQUINA, 0, 6, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: victoria diagonal descendente", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int desplazamiento = 0; desplazamiento < 4; desplazamiento++) {
                    Utilidades.poner(matriz, desplazamiento, desplazamiento,
                            Tablero.HUMANO);
                }
                comprobarLinea("diagonal desde la esquina", matriz, Tablero.HUMANO, 0, 0, 4);
                comprobarLinea("diagonal desde abajo", matriz, Tablero.HUMANO, 3, 3, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: victoria diagonal ascendente", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int desplazamiento = 0; desplazamiento < 4; desplazamiento++) {
                    Utilidades.poner(matriz, desplazamiento, 3 - desplazamiento,
                            Tablero.MAQUINA);
                }
                comprobarLinea("diagonal ascendente", matriz, Tablero.MAQUINA, 0, 3, 4);
                comprobarLinea("diagonal ascendente desde abajo", matriz, Tablero.MAQUINA, 3, 0, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: victoria diagonal en la esquina inferior derecha",
                new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int desplazamiento = 0; desplazamiento < 4; desplazamiento++) {
                    Utilidades.poner(matriz, 3 + desplazamiento, 3 + desplazamiento,
                            Tablero.HUMANO);
                }
                comprobarLinea("diagonal inferior derecha", matriz, Tablero.HUMANO, 6, 6, 4);
            }
        });

        Pruebas.ejecutar("Arbitro: tres fichas no son victoria", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.HUMANO);
                }
                Tablero tablero = Utilidades.tablero(matriz);
                Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.HUMANO),
                        "tres en línea no ganan");
                Pruebas.afirmar(Arbitro.lineaGanadora(tablero, 6, 0).isEmpty(),
                        "no hay línea ganadora");

                Utilidades.poner(matriz, 6, 3, Tablero.MAQUINA);
                Tablero conRival = Utilidades.tablero(matriz);
                Pruebas.afirmar(!Arbitro.hayVictoria(conRival, Tablero.HUMANO),
                        "una ficha rival corta la línea");
                Pruebas.afirmar(!Arbitro.hayVictoria(conRival, Tablero.MAQUINA),
                        "la ficha rival tampoco forma línea");
            }
        });

        Pruebas.ejecutar("Arbitro: un hueco corta la línea", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                Utilidades.poner(matriz, 6, 0, Tablero.HUMANO);
                Utilidades.poner(matriz, 6, 1, Tablero.HUMANO);
                Utilidades.poner(matriz, 6, 3, Tablero.HUMANO);
                Utilidades.poner(matriz, 6, 4, Tablero.HUMANO);
                Tablero tablero = Utilidades.tablero(matriz);
                Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.HUMANO),
                        "el hueco impide la victoria");
            }
        });

        Pruebas.ejecutar("Arbitro: devuelve todas las fichas de la línea", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 5; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.MAQUINA);
                }
                Tablero tablero = Utilidades.tablero(matriz);
                List<Celda> linea = Arbitro.lineaGanadora(tablero, 6, 2);
                Pruebas.afirmarIgual(5, linea.size(), "la línea de cinco se devuelve completa");
                Pruebas.afirmar(linea.contains(new Celda(6, 0)), "contiene el primer extremo");
                Pruebas.afirmar(linea.contains(new Celda(6, 4)), "contiene el segundo extremo");
            }
        });

        Pruebas.ejecutar("Arbitro: una casilla vacía no tiene línea", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = new Tablero();
                Pruebas.afirmar(Arbitro.lineaGanadora(tablero, 3, 3).isEmpty(),
                        "una casilla vacía no forma línea");
                Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.HUMANO),
                        "un tablero vacío no tiene victoria");
                Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.MAQUINA),
                        "un tablero vacío no tiene victoria de la máquina");
            }
        });

        Pruebas.ejecutar("Arbitro: tablero completo sin cuatro es empate", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = Utilidades.tableroCompletoSinCuatro();
                Pruebas.afirmar(tablero.estaLleno(), "el tablero está lleno");
                Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.HUMANO),
                        "el humano no tiene cuatro");
                Pruebas.afirmar(!Arbitro.hayVictoria(tablero, Tablero.MAQUINA),
                        "la máquina no tiene cuatro");
                Pruebas.afirmar(Arbitro.hayEmpate(tablero), "es empate");
            }
        });

        Pruebas.ejecutar("Arbitro: tablero lleno con cuatro no es empate", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizCompletaSinCuatro();
                for (int columna = 0; columna < 4; columna++) {
                    matriz[6][columna] = Tablero.HUMANO;
                }
                Tablero tablero = Utilidades.tablero(matriz);
                Pruebas.afirmar(tablero.estaLleno(), "el tablero está lleno");
                Pruebas.afirmar(Arbitro.hayVictoria(tablero, Tablero.HUMANO),
                        "hay cuatro en línea");
                Pruebas.afirmar(!Arbitro.hayEmpate(tablero), "no es empate");
            }
        });

        Pruebas.ejecutar("Arbitro: ficha inválida", new Runnable() {
            @Override
            public void run() {
                final Tablero tablero = new Tablero();
                Pruebas.afirmarLanza(IllegalArgumentException.class, new Runnable() {
                    @Override
                    public void run() {
                        Arbitro.hayVictoria(tablero, Tablero.VACIA);
                    }
                }, "hayVictoria rechaza la ficha vacía");
            }
        });
    }

    private static void comprobarLinea(String nombre, int[][] matriz, int ficha,
            int fila, int columna, int tamanoEsperado) {
        Tablero tablero = Utilidades.tablero(matriz);
        Pruebas.afirmar(Arbitro.hayVictoria(tablero, ficha), nombre + ": hay victoria");
        List<Celda> linea = Arbitro.lineaGanadora(tablero, fila, columna);
        Pruebas.afirmarIgual(tamanoEsperado, linea.size(), nombre + ": tamaño de la línea");
        Pruebas.afirmar(linea.contains(new Celda(fila, columna)),
                nombre + ": la línea contiene la ficha consultada");
        for (Celda celda : linea) {
            Pruebas.afirmarIgual(ficha, tablero.fichaEn(celda.fila(), celda.columna()),
                    nombre + ": todas las celdas son de la misma ficha");
        }
    }
}
