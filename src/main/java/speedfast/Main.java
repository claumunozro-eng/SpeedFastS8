package speedfast;

import speedfast.view.MainFrame;

import javax.swing.SwingUtilities;

/** Punto de entrada de la aplicación Swing de SpeedFast. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame();
            ventana.setVisible(true);
        });
    }
}
