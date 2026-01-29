package tdgame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing arayüzünü EDT üzerinde başlat
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	Assets.load();
                GameFrame frame = new GameFrame();
                frame.setVisible(true);
            }
        });
    }
}
