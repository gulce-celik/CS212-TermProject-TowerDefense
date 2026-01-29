package tdgame;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("Tower Defense");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel(this, 1);  // default level 1

        mainPanel.add(mainMenuPanel, "menu");
        mainPanel.add(gamePanel, "game");

        setContentPane(mainPanel);

        //  start menu background music on startup 
        SoundManager.playMenuMusic();
    }

    public void showMainMenu() {
        // switch back to menu music when going to main menu 
        SoundManager.stopMusic();
        SoundManager.playMenuMusic();

        cardLayout.show(mainPanel, "menu");
    }

    public void showGameScreen() {
        cardLayout.show(mainPanel, "game");
    }

    public void startGame(int level) {
        //  switch to in-game music when starting a level 
        SoundManager.stopMusic();
        SoundManager.playGameMusic();

        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
        }

        // Yeni level için yeni GamePanel oluştur
        gamePanel = new GamePanel(this, level);
        mainPanel.add(gamePanel, "game");

        showGameScreen();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

}


