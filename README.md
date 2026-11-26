# Connect Four (Cuatro en Raya)

A Connect Four game written entirely in Java. The interface is built with
Swing, the game logic is independent of the UI, and the computer opponent uses
minimax with alpha-beta pruning. The original Spanish UI and its controls are
preserved.

## Contents

- [Requirements](#requirements)
- [Build and run](#build-and-run)
- [How to play](#how-to-play)
- [Architecture](#architecture)
- [Tests](#tests)
- [AI](#ai)
- [Notable fixes](#notable-fixes)
- [Repository hygiene](#repository-hygiene)
- [Screenshots](#screenshots)
- [License](#license)

## Requirements

- JDK 8 or newer. The Ant build compiles with `javac.source=1.8` and
  `javac.target=1.8`.
- Apache Ant 1.9 or newer. NetBeans is **not** required.
- No external libraries: `javac.classpath` stays empty. The test runner is
  also dependency-free.

## Build and run

The project is a legacy NetBeans-style Ant project. `nbproject/build-impl.xml`
is generated; custom targets live in `build.xml`.

```bash
ant clean          # remove build/ and dist/
ant                # compile, run the tests and build dist/CuatroRaya.jar
ant test           # compile and run the unit tests only
ant run            # build and launch the game
ant javadoc        # generate dist/javadoc
java -jar dist/CuatroRaya.jar
```

A plain `javac` build also works:

```bash
javac -d build/classes $(find src -name '*.java')
java -cp build/classes CuatroRaya
```

The main class is `CuatroRaya` and the JAR keeps the original name,
`dist/CuatroRaya.jar`.

## How to play

1. **Recomenzar** (or `R`) starts a new game without resetting the score.
2. **Salir** (or `Esc`) closes the game.
3. **Turno** shows whether it is the human's or the machine's turn.
4. **Victorias** and **Derrotas** keep the historical score.
5. Click a column to drop a piece. The human plays red; the machine plays blue.
6. Four consecutive pieces horizontally, vertically or diagonally win. If the
   board fills without a winner, the game ends in a draw.

The board is 7x7, exactly as in the original version, and the human always
starts.

## Architecture

| File | Responsibility |
| --- | --- |
| `src/CuatroRaya.java` | Swing window and thin controller. Wires buttons, labels, clicks and the AI timer; contains no game rules. |
| `src/logica/Tablero.java` | Board state, gravity, column capacity and copying. No AWT/Swing imports. |
| `src/logica/Arbitro.java` | Pure win/draw detection in four directions. |
| `src/logica/Partida.java` | Turn alternation, legal moves, score, final state and winning line. |
| `src/logica/IA.java` | Minimax with alpha-beta pruning and a configurable depth. |
| `src/logica/EstadoPartida.java` | `EN_CURSO`, `VICTORIA_HUMANO`, `VICTORIA_MAQUINA`, `EMPATE`. |
| `src/logica/Celda.java` | Immutable row/column coordinate. |
| `src/ui/PanelTablero.java` | Repaint-based board painting, hover highlight, clicks and winning-line outline. |
| `src/ui/CuadroDialogo.java` | Modal result dialog (`OK`). |

Dependency direction: `ui` → `logica`; `CuatroRaya` → `ui` + `logica`.
`logica` has no dependency on AWT or Swing.

## Tests

The `test/` directory is a real Ant test source directory now:

```bash
ant test
```

`ant test` compiles `test/` into `build/test/classes` and runs
`pruebas.Pruebas`, a small built-in runner. It deliberately does **not** use
JUnit, so no external JAR is added to the classpath. The runner exits with a
non-zero status when any assertion fails, which makes Ant fail the build.

Covered cases include:

- horizontal, vertical and both diagonal wins, including board edges;
- three pieces and gaps do not win;
- full-board draws and full-board wins;
- gravity, full columns, invalid columns and invalid pieces;
- turn alternation, invalid moves after game over and score persistence;
- AI immediate wins, horizontal/vertical/diagonal blocks, full columns and
  board exhaustion.

The UI is intentionally not unit-tested; it was verified separately with a
local render and smoke test (click → AI reply → restart → victory dialog).

## AI

`IA` uses minimax with alpha-beta pruning:

- default depth: `6` plies;
- move ordering from the centre outwards (also the deterministic tie-break);
- evaluation over every four-cell window, plus a centre-column bonus;
- immediate wins and blocks are found before the heuristic;
- no external libraries, Java 8 only.

The depth is configurable in code with `new IA(4)`. The running application
also accepts a system property:

```bash
java -Dcuatroraya.profundidad=4 -jar dist/CuatroRaya.jar
```

Invalid or missing values fall back to the default depth.

## Notable fixes

The refactor corrected several bugs in the original implementation:

- **AI board corruption:** the old gravity lookup treated an occupied bottom
  row as a full column (`y = 6`) and simulated over an existing piece; restoring
  the simulation then erased that piece from the real board.
- **AI return encoding:** columns were encoded as `column` or `column * 10` and
  decoded with `(value / 10) - 1`, so some legal moves were sent to the wrong
  column.
- **State wiped on repaint:** `paint()` reset every cell of `estados` to zero,
  destroying the logical board whenever the window was repainted.
- **Missing draw:** a full board without a winner left the game stuck; draws are
  now detected and shown.
- **Out-of-range clicks:** clicks at the right edge could produce column `7`
  and throw an out-of-bounds exception; clicks on the top bar could also drop a
  piece. Clicks are now validated against the board rectangle.
- **Hidden controls:** `Salir`, `Victorias` and `Derrotas` were created but
  never added to the window, and the control panel was placed in a tiny
  `GridLayout` cell. The new top bar keeps all controls visible.
- **Dialog painting:** `CuadroDialogo.paint()` drew an always-null field and
  could fail; the dead code was removed.
- **EDT sleep:** the old result flow slept on the event dispatch thread; the
  result dialog is now modal and does not block painting.
- **Mixed AWT/Swing and direct painting:** the UI is now Swing throughout and
  uses `repaint()`/`paintComponent()` instead of `getGraphics()`.

## Repository hygiene

- `build/` and `dist/` are ignored by `.gitignore`.
- `nbproject/build-impl.xml`, `nbproject/genfiles.properties` and
  `manifest.mf` are left untouched.
- `build.xml` only adds the dependency-free `test` target.

## Screenshots

![Screenshot](https://github.com/Juanpalai/Porfolio/blob/main/images/connect4.png)

## License

This project is licensed under the [MIT License](LICENSE).
