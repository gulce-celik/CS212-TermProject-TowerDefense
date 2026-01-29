package tdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LevelSelectDialog extends JDialog {

    private final GameFrame parent;
    private int currentLevel = 1;

    private JLabel levelLabel;
    private JLabel bestScoreLabel;
    private MapPreviewPanel previewPanel;

    public LevelSelectDialog(GameFrame parent) {
        super(parent, "Select Level", true);
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ---- Title ----
        levelLabel = new JLabel("", SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        updateLevelLabel();
        add(levelLabel, BorderLayout.NORTH);

        // ---- Preview + Best Score ----
        previewPanel = new MapPreviewPanel(currentLevel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(previewPanel, BorderLayout.CENTER);

        bestScoreLabel = new JLabel("", SwingConstants.CENTER);
        bestScoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        updateBestScoreLabel();
        centerPanel.add(bestScoreLabel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // ---- Buttons ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton prevButton   = new JButton("<<");
        JButton playButton   = new JButton("Play");
        JButton nextButton   = new JButton(">>");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(prevButton);
        buttonPanel.add(playButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> changeLevel(-1));
        nextButton.addActionListener(e -> changeLevel(1));
        playButton.addActionListener(e -> startSelectedLevel());
        cancelButton.addActionListener(e -> dispose());

        addKeyBindings();

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void updateLevelLabel() {
        String text;
        switch (currentLevel) {
            case 1: text = "- MAP 1 - "; break;
            case 2: text = "- MAP 2 -"; break;
            case 3: text = "- MAP 3 -"; break;
            default: text = "Map " + currentLevel; break;
        }
        levelLabel.setText(text);
    }

    private void updateBestScoreLabel() {
        User u = UserManager.getCurrentUser();
        int best = (u != null) ? u.getBestScore(currentLevel) : 0;
        bestScoreLabel.setText("Best Score for this map: " + best);
    }

    private void changeLevel(int delta) {
        int oldLevel = currentLevel;

        currentLevel += delta;
        if (currentLevel < 1) currentLevel = 3;
        if (currentLevel > 3) currentLevel = 1;

        updateLevelLabel();
        updateBestScoreLabel();
        previewPanel.startSlideAnimation(oldLevel, currentLevel, delta);
    }

    private void startSelectedLevel() {
        parent.startGame(currentLevel);
        dispose();
    }

    // KeyListener
    private void addKeyBindings() {
        // Tuşları dinlemek için pencereye odaklanma yeteneği ver
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_LEFT) {
                    changeLevel(-1); // Sol tuş -> Önceki
                } 
                else if (code == KeyEvent.VK_RIGHT) {
                    changeLevel(1);  // Sağ tuş -> Sonraki
                } 
                else if (code == KeyEvent.VK_ENTER) {
                    startSelectedLevel(); // Enter -> Oyna
                } 
                else if (code == KeyEvent.VK_ESCAPE) {
                    dispose(); // Esc -> Kapat
                }
            }
        });
    }

    // ---------- INNER CLASS: MAP PREVIEW WITH SLIDE ANIMATION ----------
    private static class MapPreviewPanel extends JPanel {

        private static final int TILE_SIZE = 24;

        private GameMap currentMap;
        private GameMap nextMap;

        private int currentMapLevel;
        private int nextMapLevel;

        private boolean animating = false;
        private int animOffset = 0;
        private int animDirection = 0; // +1 => next geliyor, -1 => prev
        private Timer animTimer;

        public MapPreviewPanel(int level) {
            this.currentMapLevel = level;
            this.currentMap = new GameMap(level);
            setBackground(Color.BLACK);

            int totalWidth  = currentMap.getCols() * TILE_SIZE;
            int totalHeight = currentMap.getRows() * TILE_SIZE;
            setPreferredSize(new Dimension(totalWidth + 20, totalHeight + 20));
        }

        public void startSlideAnimation(int fromLevel, int toLevel, int delta) {
            if (animTimer != null && animTimer.isRunning()) { // old animasyon
                animTimer.stop();
            }

            this.currentMap      = new GameMap(fromLevel);
            this.currentMapLevel = fromLevel;

            this.nextMap         = new GameMap(toLevel);
            this.nextMapLevel    = toLevel;

            this.animating = true;
            this.animDirection = (delta > 0) ? 1 : -1;
            this.animOffset = 0;

            int slideDistance = currentMap.getCols() * TILE_SIZE; //harite genislihi kdar kay
            int step = 16;

            animTimer = new Timer(16, e -> {
                animOffset += step; //sürekli kayma miktarını arttır
                if (animOffset >= slideDistance) { //harita tamamen kaydı mı
                    animating = false;
                    ((Timer) e.getSource()).stop();

                    // anim bitince sadece yeni map kalsın
                    currentMap      = new GameMap(toLevel);
                    currentMapLevel = toLevel;
                    nextMap         = null;
                    animOffset      = 0;
                }
                repaint();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (currentMap == null) return;

            Graphics2D g2 = (Graphics2D) g;

            int rows = currentMap.getRows();
            int cols = currentMap.getCols();

            int mapWidth  = cols * TILE_SIZE;
            int mapHeight = rows * TILE_SIZE;

            int baseX = (getWidth()  - mapWidth)  / 2;
            int baseY = (getHeight() - mapHeight) / 2;

            if (!animating || nextMap == null) {
                drawMap(g2, currentMap, currentMapLevel, baseX, baseY);
            } else {
                int offset = animOffset * animDirection;

                // Eski map
                drawMap(g2, currentMap, currentMapLevel, baseX - offset, baseY);

                // Yeni map
                drawMap(
                        g2,
                        nextMap,
                        nextMapLevel,
                        baseX - offset + (animDirection > 0 ? mapWidth : -mapWidth),
                        baseY
                );
            }

            g2.setColor(Color.WHITE);
            g2.drawRect(baseX - 1, baseY - 1, mapWidth + 2, mapHeight + 2);
        }

        private void drawMap(Graphics2D g2, GameMap map, int level, int startX, int startY) {
            int rows = map.getRows();
            int cols = map.getCols();

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    int tile = map.getTile(r, c);
                    int x = startX + c * TILE_SIZE;
                    int y = startY + r * TILE_SIZE;

                    // --- Base zemin ---
                    Image baseGround;
                    if (level == 2) {
                        baseGround = Assets.towerSpotTile; // level 2: toprak zemin
                    } else {
                        baseGround = Assets.grassTile;
                    }

                    if (baseGround != null) {
                        g2.drawImage(baseGround, x, y, TILE_SIZE, TILE_SIZE, null);
                    }

                    // --- Path / spawn / goal overlay ---
                    if (tile == GameMap.TILE_PATH ||
                        tile == GameMap.TILE_SPAWN ||
                        tile == GameMap.TILE_GOAL) {

                        Image pathImg;
                        if (level == 1) {
                            pathImg = Assets.towerSpotTile; // kahverengi yol
                        } else {
                            pathImg = Assets.pathTile;       // gri yol
                        }

                        if (pathImg != null) {
                            g2.drawImage(pathImg, x, y, TILE_SIZE, TILE_SIZE, null);
                        }
                    }
                    // --- Taş / çalı overlay ---
                    else if (tile == GameMap.TILE_STONE) {
                        if (Assets.stoneImage != null) {
                            g2.drawImage(Assets.stoneImage, x, y, TILE_SIZE, TILE_SIZE, null);
                        }
                    } else if (tile == GameMap.TILE_BUSH_SMALL) {
                        if (Assets.bushSmallImage != null) {
                            g2.drawImage(Assets.bushSmallImage, x, y, TILE_SIZE, TILE_SIZE, null);
                        }
                    } else if (tile == GameMap.TILE_BUSH_ROUND) {
                        if (Assets.bushRoundImage != null) {
                            g2.drawImage(Assets.bushRoundImage, x, y, TILE_SIZE, TILE_SIZE, null);
                        }
                    } else if (tile == GameMap.TILE_BUSH_BIG) {
                        if (Assets.bushBigImage != null) {
                            g2.drawImage(Assets.bushBigImage, x, y, TILE_SIZE, TILE_SIZE, null);
                        }
                    }
                }
            }
        }
    }
}


