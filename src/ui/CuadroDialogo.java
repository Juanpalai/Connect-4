package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Diálogo modal para mostrar el resultado de la partida.
 */
public final class CuadroDialogo extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Crea y configura el diálogo.
     *
     * @param propietario ventana propietaria
     * @param mensaje     texto que se muestra
     */
    public CuadroDialogo(Window propietario, String mensaje) {
        super(propietario, "informacion", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(400, 130);
        setLocationRelativeTo(propietario);

        JLabel texto = new JLabel(mensaje, SwingConstants.CENTER);
        texto.setFont(texto.getFont().deriveFont(Font.BOLD, 14.0f));

        JButton aceptar = new JButton("OK");
        aceptar.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                dispose();
            }
        });

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(aceptar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));
        contenido.add(texto, BorderLayout.CENTER);
        contenido.add(panelBoton, BorderLayout.SOUTH);
        setContentPane(contenido);

        getRootPane().setDefaultButton(aceptar);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                dispose();
            }
        });
    }
}
