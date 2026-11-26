package pruebas;

import logica.EstadoPartida;
import logica.Partida;
import logica.Tablero;

/**
 * Pruebas de turnos, jugadas válidas, marcador y final de partida.
 */
public final class PartidaPruebas {

    private PartidaPruebas() {
    }

    public static void ejecutar() {
        Pruebas.ejecutar("Partida: empieza el humano con el tablero vacío", new Runnable() {
            @Override
            public void run() {
                Partida partida = new Partida();
                Pruebas.afirmar(partida.esTurnoHumano(), "el primer turno es del humano");
                Pruebas.afirmarIgual(Tablero.HUMANO, partida.getTurnoActual(),
                        "la ficha inicial es la del humano");
                Pruebas.afirmarIgual(EstadoPartida.EN_CURSO, partida.getEstado(),
                        "la partida está en curso");
                Pruebas.afirmarIgual(0, partida.getTablero().fichasColocadas(),
                        "el tablero está vacío");
                Pruebas.afirmarIgual(0, partida.getVictoriasHumano(), "sin victorias");
                Pruebas.afirmarIgual(0, partida.getVictoriasMaquina(), "sin derrotas");
                Pruebas.afirmarIgual(-1, partida.getUltimaFila(), "no hay última fila");
                Pruebas.afirmarIgual(-1, partida.getUltimaColumna(), "no hay última columna");
            }
        });

        Pruebas.ejecutar("Partida: los turnos alternan", new Runnable() {
            @Override
            public void run() {
                Partida partida = new Partida();
                Pruebas.afirmar(partida.jugar(0), "el humano juega la columna 0");
                Pruebas.afirmarIgual(Tablero.MAQUINA, partida.getTurnoActual(),
                        "después juega la máquina");
                Pruebas.afirmar(!partida.esTurnoHumano(), "ya no es turno del humano");

                Pruebas.afirmar(partida.jugar(1), "la máquina juega la columna 1");
                Pruebas.afirmarIgual(Tablero.HUMANO, partida.getTurnoActual(),
                        "vuelve a jugar el humano");
                Pruebas.afirmar(partida.esTurnoHumano(), "es turno del humano");
            }
        });

        Pruebas.ejecutar("Partida: jugarHumano respeta el turno", new Runnable() {
            @Override
            public void run() {
                Partida partida = new Partida();
                Pruebas.afirmar(partida.jugarHumano(0), "el humano puede jugar en su turno");
                Pruebas.afirmar(!partida.jugarHumano(1),
                        "el humano no puede jugar en el turno de la máquina");
                Pruebas.afirmar(partida.jugar(1), "la máquina sí puede jugar");
                Pruebas.afirmar(partida.jugarHumano(2), "el humano vuelve a poder jugar");
            }
        });

        Pruebas.ejecutar("Partida: rechaza columnas inválidas", new Runnable() {
            @Override
            public void run() {
                Partida partida = new Partida();
                int antes = partida.getTablero().fichasColocadas();
                Pruebas.afirmar(!partida.jugar(-1), "columna -1 inválida");
                Pruebas.afirmar(!partida.jugar(Tablero.COLUMNAS), "columna fuera del tablero");
                Pruebas.afirmar(partida.esTurnoHumano(), "el turno no cambia");
                Pruebas.afirmarIgual(antes, partida.getTablero().fichasColocadas(),
                        "no se coloca ninguna ficha");
                Pruebas.afirmar(!partida.puedeJugar(-1), "puedeJugar rechaza -1");
                Pruebas.afirmar(!partida.puedeJugar(Tablero.COLUMNAS),
                        "puedeJugar rechaza una columna fuera del tablero");
            }
        });

        Pruebas.ejecutar("Partida: una columna llena no consume el turno", new Runnable() {
            @Override
            public void run() {
                Partida partida = new Partida();
                for (int jugada = 0; jugada < Tablero.FILAS; jugada++) {
                    Pruebas.afirmar(partida.jugar(0),
                            "se llena la columna 0, jugada " + jugada);
                }
                Pruebas.afirmar(partida.getTablero().columnaLlena(0),
                        "la columna 0 está llena");
                Pruebas.afirmar(!partida.puedeJugar(0),
                        "puedeJugar rechaza una columna llena");
                int turnoAntes = partida.getTurnoActual();
                int fichasAntes = partida.getTablero().fichasColocadas();
                Pruebas.afirmar(!partida.jugar(0), "no se puede jugar en una columna llena");
                Pruebas.afirmarIgual(turnoAntes, partida.getTurnoActual(),
                        "el turno no cambia");
                Pruebas.afirmarIgual(fichasAntes, partida.getTablero().fichasColocadas(),
                        "el número de fichas no cambia");
            }
        });

        Pruebas.ejecutar("Partida: victoria del humano y marcador", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.HUMANO);
                }
                Partida partida = new Partida(Utilidades.tablero(matriz), Tablero.HUMANO);

                Pruebas.afirmar(partida.jugar(3), "el humano completa cuatro en línea");
                Pruebas.afirmarIgual(EstadoPartida.VICTORIA_HUMANO, partida.getEstado(),
                        "gana el humano");
                Pruebas.afirmarIgual(1, partida.getVictoriasHumano(), "una victoria");
                Pruebas.afirmarIgual(0, partida.getVictoriasMaquina(), "ninguna derrota");
                Pruebas.afirmarIgual(4, partida.getLineaGanadora().size(),
                        "la línea ganadora tiene cuatro fichas");
                Pruebas.afirmarIgual(6, partida.getUltimaFila(), "la última ficha está abajo");
                Pruebas.afirmarIgual(3, partida.getUltimaColumna(), "la última ficha está en la columna 3");
            }
        });

        Pruebas.ejecutar("Partida: victoria de la máquina y marcador", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.MAQUINA);
                }
                Partida partida = new Partida(Utilidades.tablero(matriz), Tablero.MAQUINA);

                Pruebas.afirmar(partida.jugar(3), "la máquina completa cuatro en línea");
                Pruebas.afirmarIgual(EstadoPartida.VICTORIA_MAQUINA, partida.getEstado(),
                        "gana la máquina");
                Pruebas.afirmarIgual(0, partida.getVictoriasHumano(), "ninguna victoria");
                Pruebas.afirmarIgual(1, partida.getVictoriasMaquina(), "una derrota");
                Pruebas.afirmarIgual(4, partida.getLineaGanadora().size(),
                        "la línea ganadora tiene cuatro fichas");
            }
        });

        Pruebas.ejecutar("Partida: tras terminar no se aceptan más jugadas", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 4; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.HUMANO);
                }
                Partida partida = new Partida(Utilidades.tablero(matriz), Tablero.MAQUINA);
                Pruebas.afirmarIgual(EstadoPartida.VICTORIA_HUMANO, partida.getEstado(),
                        "la posición inicial ya tiene ganador");
                Pruebas.afirmarIgual(4, partida.getLineaGanadora().size(),
                        "la posición inicial conserva su línea ganadora");
                int turnoAntes = partida.getTurnoActual();
                Pruebas.afirmar(!partida.jugar(5), "no se puede seguir jugando");
                Pruebas.afirmarIgual(turnoAntes, partida.getTurnoActual(),
                        "el turno no cambia");
                Pruebas.afirmarIgual(0, partida.getVictoriasHumano(),
                        "cargar una posición no altera el marcador");
                Pruebas.afirmar(!partida.jugarHumano(5), "jugarHumano tampoco permite jugar");
            }
        });

        Pruebas.ejecutar("Partida: empate cuando se llena el tablero", new Runnable() {
            @Override
            public void run() {
                Tablero tablero = Utilidades.tableroCompletoSinCuatroConHueco(0, 6);
                Partida partida = new Partida(tablero, Tablero.HUMANO);
                Pruebas.afirmar(partida.puedeJugar(6), "la última columna tiene hueco");

                Pruebas.afirmar(partida.jugar(6), "se coloca la última ficha");
                Pruebas.afirmarIgual(EstadoPartida.EMPATE, partida.getEstado(),
                        "la partida termina en empate");
                Pruebas.afirmar(partida.getTablero().estaLleno(), "el tablero está lleno");
                Pruebas.afirmar(partida.getLineaGanadora().isEmpty(),
                        "no hay línea ganadora");
                Pruebas.afirmarIgual(0, partida.getVictoriasHumano(), "el empate no suma victoria");
                Pruebas.afirmarIgual(0, partida.getVictoriasMaquina(), "el empate no suma derrota");
                Pruebas.afirmar(!partida.jugar(0), "no se puede jugar tras el empate");
            }
        });

        Pruebas.ejecutar("Partida: reiniciar conserva el marcador", new Runnable() {
            @Override
            public void run() {
                int[][] matriz = Utilidades.matrizVacia();
                for (int columna = 0; columna < 3; columna++) {
                    Utilidades.poner(matriz, 6, columna, Tablero.HUMANO);
                }
                Partida partida = new Partida(Utilidades.tablero(matriz), Tablero.HUMANO);
                partida.jugar(3);
                Pruebas.afirmarIgual(1, partida.getVictoriasHumano(), "hay una victoria");

                partida.reiniciar();

                Pruebas.afirmarIgual(EstadoPartida.EN_CURSO, partida.getEstado(),
                        "la nueva partida está en curso");
                Pruebas.afirmar(partida.esTurnoHumano(), "vuelve a empezar el humano");
                Pruebas.afirmarIgual(0, partida.getTablero().fichasColocadas(),
                        "el tablero queda vacío");
                Pruebas.afirmar(partida.getLineaGanadora().isEmpty(),
                        "no queda línea ganadora");
                Pruebas.afirmarIgual(-1, partida.getUltimaFila(), "no queda última fila");
                Pruebas.afirmarIgual(-1, partida.getUltimaColumna(), "no queda última columna");
                Pruebas.afirmarIgual(1, partida.getVictoriasHumano(),
                        "el marcador se conserva");
                Pruebas.afirmarIgual(0, partida.getVictoriasMaquina(),
                        "las derrotas se conservan");
            }
        });
    }
}
