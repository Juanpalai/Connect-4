# Connect Four (Cuatro en Raya)

The classic Connect Four game written in Java 8. The interface is built with
Swing, the game logic has no AWT/Swing dependency, and the computer opponent
uses minimax with alpha-beta pruning. The original Spanish UI, controls and
rules are preserved.

## Features

- **7x7** board with four-in-a-row to win.
- Human (red) against the machine (blue), with the human always starting.
- Win detection horizontally, vertically and on both diagonals, plus draws when
  the board fills up.
- Lifetime score of wins and losses that survives a restart.
- Configurable AI search depth.
- No external dependencies: just the JDK and Ant.

## Requirements

- JDK 8 or newer (`javac.source` and `javac.target` are pinned to `1.8`).
- Apache Ant 1.9 or newer for the included build.
- NetBeans is **not** required.

## Build and run

This is a NetBeans-style Ant project. `nbproject/build-impl.xml` is generated;
custom build logic lives in `build.xml`.

```bash
ant clean     # remove build/ and dist/
ant           # compile, run the tests and build dist/CuatroRaya.jar
ant test      # compile and run the unit tests only
ant run       # build and launch the game
ant javadoc   # generate Javadoc in dist/javadoc
```

You can also run the generated JAR or compile by hand:

```bash
java -jar dist/CuatroRaya.jar

javac -d build/classes $(find src -name '*.java')
java -cp build/classes CuatroRaya
```

The main class is `CuatroRaya` and the JAR keeps its original name,
`dist/CuatroRaya.jar`.

## How to play

| Control | Action |
| --- | --- |
| Click a column | Drop a piece into that column |
| **Recomenzar** or `R` | Start a new game without resetting the score |
| **Salir** or `Esc` | Close the game |

The top bar shows whose turn it is and, at the end, the result. The first player
to line up four pieces wins; if the board fills without a winner, the game ends
in a draw.

## Architecture

| File | Responsibility |
| --- | --- |
| `src/CuatroRaya.java` | Swing window and thin controller: wires buttons, clicks, the score and the machine timer. |
| `src/logica/Tablero.java` | Board state, gravity, per-column capacity and copies. |
| `src/logica/Arbitro.java` | Pure win and draw detection in four directions. |
| `src/logica/Partida.java` | Turns, legal moves, score, final state and winning line. |
| `src/logica/IA.java` | Minimax with alpha-beta pruning and configurable depth. |
| `src/logica/EstadoPartida.java` | Possible states: `EN_CURSO`, `VICTORIA_HUMANO`, `VICTORIA_MAQUINA`, `EMPATE`. |
| `src/logica/Celda.java` | Immutable row/column coordinate. |
| `src/ui/PanelTablero.java` | Repaint-based board painting, hover highlight, clicks and winning-line outline. |
| `src/ui/CuadroDialogo.java` | Modal dialog with the game result. |

The dependency direction is `ui → logica` and `CuatroRaya → ui + logica`. The
`logica` package imports nothing from `java.awt` or `javax.swing`, so it can be
tested in isolation.

> [!NOTE]
> The board is 7x7 and four in a row wins, exactly as in the original version.
> It is not the classic 7x6 Connect Four.

## Tests

The tests use no JUnit and add nothing to the classpath: `test/pruebas/Pruebas.java`
is a small built-in runner that exits non-zero when an assertion fails.

```bash
ant test
```

`ant test` compiles `test/` into `build/test/classes` and runs `pruebas.Pruebas`.
Current coverage includes:

- horizontal, vertical and diagonal wins, including board edges;
- boards with three pieces or with gaps, which must not win;
- full-board draws and wins on the last move;
- gravity, full columns, invalid columns and invalid pieces;
- turn alternation, moves after game over and score persistence;
- AI immediate wins and blocks in all four directions.

The UI has no unit tests; it is verified manually.

## AI

`IA` uses minimax with alpha-beta pruning:

- default depth of **6** plies (`IA.PROFUNDIDAD_POR_DEFECTO`);
- move ordering from the centre outwards, which also breaks ties towards the
  centre;
- evaluation over every four-cell window plus a centre-column bonus;
- immediate wins and blocks detected before the heuristic.

The depth can be set in code with `new IA(4)` or, when running the JAR, with a
system property:

```bash
java -Dcuatroraya.profundidad=4 -jar dist/CuatroRaya.jar
```

Invalid or missing values fall back to the default depth.

## Project layout

```text
src/CuatroRaya.java      Entry point and Swing controller
src/logica/              Rules, board, game and AI (no Swing)
src/ui/                  Swing panels and dialogs
test/pruebas/            Unit tests and the built-in runner
nbproject/               Ant project metadata (generated)
build.xml                Compilation, tests and packaging
```

`build/` and `dist/` are generated artifacts and are excluded from the
repository.
