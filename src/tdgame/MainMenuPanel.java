package tdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel {

    private GameFrame parentFrame;

    private JButton startButton;
    private JButton loginButton;
    private JButton creditsButton;
    private JButton highScoresButton;

    public MainMenuPanel(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout()); // ortada dikey kolon

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        // ---- TITLE ----
        // Aralara boşluk koyduk, daha geniş bir görünüm için:
        JLabel titleLabel = new JLabel("T O W E R   D E F E N S E");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
        titleLabel.setForeground(new Color(240, 240, 255));

        column.add(Box.createVerticalStrut(40));
        column.add(titleLabel);
        column.add(Box.createVerticalStrut(40));

        // ---- BUTTONS ----
        startButton      = createMenuButton("START");
        loginButton      = createMenuButton("LOGIN");
        creditsButton    = createMenuButton("CREDITS");
        highScoresButton = createMenuButton("High Scores");

        // Mavi butonlar (start/login/credits)
        Color blue = new Color(0, 140, 255);
        startButton.setBackground(blue);
        loginButton.setBackground(blue);
        creditsButton.setBackground(blue);

        // HighScores turuncu
        Color orange = new Color(230, 120, 40);
        highScoresButton.setBackground(orange);

        column.add(startButton);
        column.add(Box.createVerticalStrut(15));
        column.add(loginButton);
        column.add(Box.createVerticalStrut(15));
        column.add(creditsButton);
        column.add(Box.createVerticalStrut(15));
        column.add(highScoresButton);
        column.add(Box.createVerticalStrut(40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(column, gbc);

        // --- BUTON DAVRANIŞLARI ---

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (UserManager.getCurrentUser() == null) {
                    JOptionPane.showMessageDialog(
                            parentFrame,
                            "You must login before starting the game.",
                            "Access Denied",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                LevelSelectDialog dialog = new LevelSelectDialog(parentFrame);
                dialog.setVisible(true);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginDialog dialog = new LoginDialog(parentFrame);
                dialog.setVisible(true);
            }
        });

        creditsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg =
                        "Tower Defense Game\n" +
                        "CSE212 Term Project\n\n" +
                        "Instructor: Burcu Selçuk\n\n" +
                        "Developer : Gülce Çelik\n" ;
                JOptionPane.showMessageDialog(
                        parentFrame,
                        msg,
                        "Credits",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        /*
        creditsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        parentFrame,
                        "Tower Defense\nCSE212 Term Project",
                        "Credits",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });*/

        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User current = UserManager.getCurrentUser();
                if (current == null) {
                    JOptionPane.showMessageDialog(
                            parentFrame,
                            "You must login first to see high scores.",
                            "High Scores",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                String text = GameResultLogger.buildHighScoreTextForUser(current);

                JOptionPane.showMessageDialog(
                        parentFrame,
                        text,
                        "High Scores",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        Image dirt = Assets.towerSpotTile;
        Image road = Assets.pathTile;

        int tileSize = 64;

        // 1) Arka plan: toprak (kahverengi)
        if (dirt != null) {
            for (int y = 0; y < h; y += tileSize) {
                for (int x = 0; x < w; x += tileSize) {
                    g2.drawImage(dirt, x, y, tileSize, tileSize, null);
                }
            }
        } else {
            g2.setColor(new Color(180, 120, 70));
            g2.fillRect(0, 0, w, h);
        }

        // 2) Ortada kalın yatay path 
        if (road != null) {
            int pathHeight = h / 3;
            int pathY = h / 2 - pathHeight / 2;
            for (int y = pathY; y < pathY + pathHeight; y += tileSize) {
                for (int x = 0; x < w; x += tileSize) {
                    g2.drawImage(road, x, y, tileSize, tileSize, null);
                }
            }
        }

        // 3) Hafif koyu overlay, butonlar daha net görünsün
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRect(0, 0, w, h);
    }
}
