package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logica.Celda;
import logica.Tablero;

/**
 * Panel que dibuja el tablero y traduce los clics en columnas.
 *
 * <p>Toda la pintura pasa por {@link #paintComponent(Graphics)} y se solicita
 * con {@code repaint()}; no se usan llamadas directas a {@code getGraphics()}.
 * El panel no conoce las reglas del juego: solo muestra el {@link Tablero} y
 * avisa de la columna elegida.</p>
 */
@SuppressWarnings("serial")
public final class PanelTablero extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int MARGEN_IZQUIERDO = 20;
    private static final int MARGEN_SUPERIOR = 20;
    private static final int ANCHO_CELDA = 90;
    private static final int ALTO_CELDA = 80;
    private static final int DIAMETRO_FICHA = 70;
    private static final int ANCHO_PANEL = 650;
    private static final int ALTO_PANEL = 610;

    private static final Color COLOR_FONDO = Color.LIGHT_GRAY;
    private static final Color COLOR_HUECO = Color.WHITE;
    private static final Color COLOR_HUMANO = Color.RED;
    private static final Color COLOR_MAQUINA = Color.BLUE;
    private static final Color COLOR_LINEA_GANADORA = new Color(255, 193, 7);
    private static final Color COLOR_COLUMNA_RESALTADA = new Color(255, 255, 255, 90);
    private static final Color COLOR_ULTIMA_FICHA = new Color(255, 255, 255, 210);

    private Tablero tablero;
    private List<Celda> lineaGanadora = Collections.emptyList();
    private int ultimaFila = -1;
    private int ultimaColumna = -1;
    private int columnaResaltada = -1;
    private boolean interactivo;
    private IntConsumer alElegirColumna;

    /** Crea un panel vacío. */
    public PanelTablero() {
        setBackground(COLOR_FONDO);
        setOpaque(true);
        setPreferredSize(new Dimension(ANCHO_PANEL, ALTO_PANEL));

        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evento) {
                if (!interactivo || !SwingUtilities.isLeftMouseButton(evento)) {
                    return;
                }
                int columna = columnaEn(evento.getX(), evento.getY());
                if (columna >= 0 && alElegirColumna != null) {
                    alElegirColumna.accept(columna);
                }
            }

            @Override
            public void mouseMoved(MouseEvent evento) {
                int columna = interactivo ? columnaEn(evento.getX(), evento.getY()) : -1;
                if (columna != columnaResaltada) {
                    columnaResaltada = columna;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                if (columnaResaltada != -1) {
                    columnaResaltada = -1;
                    repaint();
                }
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    /**
     * Establece el tablero que se debe dibujar.
     *
     * @param tablero tablero actual; puede ser {@code null} antes de empezar
     */
    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
        repaint();
    }

    /**
     * Marca las celdas que forman la línea ganadora.
     *
     * @param linea celdas ganadoras; una lista vacía si no hay victoria
     */
    public void setLineaGanadora(List<Celda> linea) {
        lineaGanadora = linea == null
                ? Collections.<Celda>emptyList() : new ArrayList<Celda>(linea);
        repaint();
    }

    /**
     * Marca la última ficha colocada.
     *
     * @param fila    fila de la ficha, o {@code -1} si no hay
     * @param columna columna de la ficha, o {@code -1} si no hay
     */
    public void setUltimaJugada(int fila, int columna) {
        ultimaFila = fila;
        ultimaColumna = columna;
        repaint();
    }

    /**
     * Activa o desactiva la interacción con el tablero.
     *
     * @param interactivo {@code true} si el humano puede hacer clic
     */
    public void setInteractivo(boolean interactivo) {
        this.interactivo = interactivo;
        if (!interactivo && columnaResaltada != -1) {
            columnaResaltada = -1;
        }
        repaint();
    }

    /**
     * Registra el receptor de columnas elegidas.
     *
     * @param alElegirColumna acción que recibe la columna pulsada
     */
    public void setAlElegirColumna(IntConsumer alElegirColumna) {
        this.alElegirColumna = alElegirColumna;
    }

    /**
     * Calcula la columna correspondiente a un punto del panel.
     *
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @return columna válida, o {@code -1} si el punto está fuera del tablero
     */
    private int columnaEn(int x, int y) {
        int anchoTablero = Tablero.COLUMNAS * ANCHO_CELDA;
        int altoTablero = Tablero.FILAS * ALTO_CELDA;
        if (x < MARGEN_IZQUIERDO || x >= MARGEN_IZQUIERDO + anchoTablero
                || y < MARGEN_SUPERIOR || y >= MARGEN_SUPERIOR + altoTablero) {
            return -1;
        }
        return (x - MARGEN_IZQUIERDO) / ANCHO_CELDA;
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        if (tablero == null) {
            return;
        }

        Graphics2D g = (Graphics2D) grafico.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            pintarColumnaResaltada(g);
            pintarHuecos(g);
            pintarFichas(g);
            pintarLineaGanadora(g);
        } finally {
            g.dispose();
        }
    }

    private void pintarColumnaResaltada(Graphics2D g) {
        if (columnaResaltada < 0) {
            return;
        }
        int x = MARGEN_IZQUIERDO + columnaResaltada * ANCHO_CELDA;
        g.setColor(COLOR_COLUMNA_RESALTADA);
        g.fillRoundRect(x + 6, MARGEN_SUPERIOR - 6,
                ANCHO_CELDA - 12, Tablero.FILAS * ALTO_CELDA + 12, 20, 20);
    }

    private void pintarHuecos(Graphics2D g) {
        g.setColor(COLOR_HUECO);
        for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
            for (int fila = 0; fila < Tablero.FILAS; fila++) {
                g.fillOval(xDeColumna(columna), yDeFila(fila),
                        DIAMETRO_FICHA, DIAMETRO_FICHA);
            }
        }
    }

    private void pintarFichas(Graphics2D g) {
        for (int columna = 0; columna < Tablero.COLUMNAS; columna++) {
            for (int fila = 0; fila < Tablero.FILAS; fila++) {
                int ficha = tablero.fichaEn(fila, columna);
                if (ficha == Tablero.VACIA) {
                    continue;
                }
                g.setColor(ficha == Tablero.HUMANO ? COLOR_HUMANO : COLOR_MAQUINA);
                int x = xDeColumna(columna);
                int y = yDeFila(fila);
                g.fillOval(x, y, DIAMETRO_FICHA, DIAMETRO_FICHA);
                if (fila == ultimaFila && columna == ultimaColumna) {
                    g.setColor(COLOR_ULTIMA_FICHA);
                    g.fillOval(x + DIAMETRO_FICHA / 2 - 6,
                            y + DIAMETRO_FICHA / 2 - 6, 12, 12);
                }
            }
        }
    }

    private void pintarLineaGanadora(Graphics2D g) {
        if (lineaGanadora.isEmpty()) {
            return;
        }
        g.setColor(COLOR_LINEA_GANADORA);
        g.setStroke(new BasicStroke(4.0f));
        for (Celda celda : lineaGanadora) {
            g.drawOval(xDeColumna(celda.columna()) + 2,
                    yDeFila(celda.fila()) + 2,
                    DIAMETRO_FICHA - 4, DIAMETRO_FICHA - 4);
        }
    }

    private static int xDeColumna(int columna) {
        return MARGEN_IZQUIERDO + columna * ANCHO_CELDA;
    }

    private static int yDeFila(int fila) {
        return MARGEN_SUPERIOR + fila * ALTO_CELDA;
    }
}
