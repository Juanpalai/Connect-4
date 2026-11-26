import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import logica.EstadoPartida;
import logica.IA;
import logica.Partida;
import logica.Tablero;
import ui.CuadroDialogo;
import ui.PanelTablero;

/**
 * Ventana principal de Cuatro en Raya.
 *
 * <p>Esta clase es una capa fina de interfaz: presenta el tablero, traduce los
 * clics en jugadas y delega las reglas en {@link Partida} y la elección de
 * movimientos en {@link IA}.</p>
 */
@SuppressWarnings("serial")
public final class CuatroRaya extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String TITULO = "Cuatro en Raya";
    private static final int ANCHO_VENTANA = 650;
    private static final int ALTO_VENTANA = 690;
    private static final int ALTO_BARRA_SUPERIOR = 80;
    private static final int RETARDO_MAQUINA_MS = 250;
    private static final int ANCHO_ETIQUETA_JUGADOR = 130;
    private static final int ANCHO_ETIQUETA_VICTORIAS = 95;
    private static final int ANCHO_ETIQUETA_DERROTAS = 85;
    private static final int ALTO_ETIQUETA = 20;
    private static final float TAMANO_FUENTE_ESTADO = 14.0f;
    private static final String PROPIEDAD_PROFUNDIDAD = "cuatroraya.profundidad";

    private static final Color COLOR_FONDO = Color.LIGHT_GRAY;
    private static final Color COLOR_HUMANO = Color.RED;
    private static final Color COLOR_MAQUINA = Color.BLUE;
    private static final Color COLOR_EMPATE = Color.DARK_GRAY;

    private final Partida partida = new Partida();
    private final IA ia = crearIA();
    private final PanelTablero panelTablero = new PanelTablero();

    private final JLabel etiquetaTurno = new JLabel("Turno:");
    private final JLabel etiquetaJugador = new JLabel("Humano");
    private final JLabel etiquetaVictorias = new JLabel("Victorias: 0");
    private final JLabel etiquetaDerrotas = new JLabel("Derrotas: 0");
    private final JButton botonRecomenzar = new JButton("Recomenzar");
    private final JButton botonSalir = new JButton("Salir");

    private Timer temporizadorMaquina;
    private boolean maquinaPensando;

    /** Crea y configura la ventana. */
    public CuatroRaya() {
        super(TITULO);
        configurarVentana();
        construirInterfaz();
        conectarEventos();
        iniciarPartida();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        getContentPane().setLayout(new BorderLayout());
    }

    private void construirInterfaz() {
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(Color.WHITE);
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        barraSuperior.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_BARRA_SUPERIOR));

        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        panelEstado.setOpaque(false);
        etiquetaTurno.setFont(etiquetaTurno.getFont().deriveFont(Font.BOLD,
                TAMANO_FUENTE_ESTADO));
        etiquetaJugador.setFont(etiquetaJugador.getFont().deriveFont(Font.BOLD,
                TAMANO_FUENTE_ESTADO));
        etiquetaJugador.setForeground(COLOR_HUMANO);
        etiquetaJugador.setPreferredSize(
                new Dimension(ANCHO_ETIQUETA_JUGADOR, ALTO_ETIQUETA));
        etiquetaJugador.setHorizontalAlignment(SwingConstants.LEFT);
        etiquetaVictorias.setPreferredSize(
                new Dimension(ANCHO_ETIQUETA_VICTORIAS, ALTO_ETIQUETA));
        etiquetaDerrotas.setPreferredSize(
                new Dimension(ANCHO_ETIQUETA_DERROTAS, ALTO_ETIQUETA));
        panelEstado.add(etiquetaTurno);
        panelEstado.add(etiquetaJugador);
        panelEstado.add(etiquetaVictorias);
        panelEstado.add(etiquetaDerrotas);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        panelAcciones.setOpaque(false);
        botonRecomenzar.setToolTipText("Inicia una partida nueva (R)");
        botonSalir.setToolTipText("Cierra el juego (Esc)");
        panelAcciones.add(botonRecomenzar);
        panelAcciones.add(botonSalir);

        barraSuperior.add(panelEstado, BorderLayout.WEST);
        barraSuperior.add(panelAcciones, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
    }

    private void conectarEventos() {
        botonRecomenzar.addActionListener(evento -> iniciarPartida());
        botonSalir.addActionListener(evento -> salir());
        panelTablero.setAlElegirColumna(this::jugarHumano);

        registrarAtajo(KeyEvent.VK_R, "reiniciar", new Runnable() {
            @Override
            public void run() {
                iniciarPartida();
            }
        });
        registrarAtajo(KeyEvent.VK_ESCAPE, "salir", new Runnable() {
            @Override
            public void run() {
                salir();
            }
        });
    }

    private void registrarAtajo(int tecla, String nombre, final Runnable accion) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(tecla, 0), nombre);
        getRootPane().getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                accion.run();
            }
        });
    }

    private void iniciarPartida() {
        detenerTemporizador();
        maquinaPensando = false;
        partida.reiniciar();
        actualizarVista();
    }

    private void jugarHumano(int columna) {
        if (maquinaPensando || partida.getEstado() != EstadoPartida.EN_CURSO
                || !partida.esTurnoHumano()) {
            return;
        }
        if (!partida.jugar(columna)) {
            return;
        }
        actualizarVista();
        if (partida.getEstado().haTerminado()) {
            finalizarPartida();
        } else {
            programarJugadaMaquina();
        }
    }

    private void programarJugadaMaquina() {
        maquinaPensando = true;
        panelTablero.setInteractivo(false);
        final Timer temporizador = new Timer(RETARDO_MAQUINA_MS, evento -> {
            temporizadorMaquina = null;
            jugarMaquina();
        });
        temporizador.setRepeats(false);
        temporizadorMaquina = temporizador;
        temporizador.start();
    }

    private void jugarMaquina() {
        maquinaPensando = false;
        if (partida.getEstado() != EstadoPartida.EN_CURSO) {
            actualizarVista();
            return;
        }

        int columna = ia.elegirColumna(partida.getTablero(), Tablero.MAQUINA);
        if (columna >= 0) {
            partida.jugar(columna);
        }
        actualizarVista();
        if (partida.getEstado().haTerminado()) {
            finalizarPartida();
        }
    }

    private void finalizarPartida() {
        detenerTemporizador();
        maquinaPensando = false;
        actualizarVista();

        final String mensaje = mensajeResultado();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CuadroDialogo(CuatroRaya.this, mensaje).setVisible(true);
            }
        });
    }

    private void actualizarVista() {
        panelTablero.setTablero(partida.getTablero());
        panelTablero.setLineaGanadora(partida.getLineaGanadora());
        panelTablero.setUltimaJugada(partida.getUltimaFila(), partida.getUltimaColumna());
        etiquetaVictorias.setText("Victorias: " + partida.getVictoriasHumano());
        etiquetaDerrotas.setText("Derrotas: " + partida.getVictoriasMaquina());
        actualizarIndicador();
        panelTablero.setInteractivo(partida.getEstado() == EstadoPartida.EN_CURSO
                && partida.esTurnoHumano() && !maquinaPensando);
    }

    private void actualizarIndicador() {
        switch (partida.getEstado()) {
            case EN_CURSO:
                etiquetaTurno.setText("Turno:");
                if (partida.esTurnoHumano()) {
                    etiquetaJugador.setText("Humano");
                    etiquetaJugador.setForeground(COLOR_HUMANO);
                } else {
                    etiquetaJugador.setText("Maquina");
                    etiquetaJugador.setForeground(COLOR_MAQUINA);
                }
                break;
            case VICTORIA_HUMANO:
                etiquetaTurno.setText("Resultado:");
                etiquetaJugador.setText("¡Ganaste!");
                etiquetaJugador.setForeground(COLOR_HUMANO);
                break;
            case VICTORIA_MAQUINA:
                etiquetaTurno.setText("Resultado:");
                etiquetaJugador.setText("Ganó la máquina");
                etiquetaJugador.setForeground(COLOR_MAQUINA);
                break;
            case EMPATE:
                etiquetaTurno.setText("Resultado:");
                etiquetaJugador.setText("Empate");
                etiquetaJugador.setForeground(COLOR_EMPATE);
                break;
            default:
                break;
        }
    }

    private String mensajeResultado() {
        switch (partida.getEstado()) {
            case VICTORIA_HUMANO:
                return "GANASTE JUGADOR HUMANO";
            case VICTORIA_MAQUINA:
                return "GANÓ LA MÁQUINA!!!";
            case EMPATE:
                return "EMPATE";
            default:
                return "";
        }
    }

    private void detenerTemporizador() {
        if (temporizadorMaquina != null) {
            temporizadorMaquina.stop();
            temporizadorMaquina = null;
        }
    }

    private void salir() {
        detenerTemporizador();
        dispose();
        System.exit(0);
    }

    private static IA crearIA() {
        String configurada = System.getProperty(PROPIEDAD_PROFUNDIDAD);
        if (configurada != null) {
            try {
                int profundidad = Integer.parseInt(configurada.trim());
                if (profundidad >= 1) {
                    return new IA(profundidad);
                }
            } catch (NumberFormatException ignorada) {
                // Se usa la profundidad por defecto.
            }
        }
        return new IA();
    }

    /**
     * Punto de entrada del juego.
     *
     * @param argumentos no se usan
     */
    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CuatroRaya().setVisible(true);
            }
        });
    }
}
