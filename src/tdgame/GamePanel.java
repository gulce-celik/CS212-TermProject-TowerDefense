package tdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {

    private GameFrame parentFrame;
    private int level;
    private GameMap map;

    // oyun durumu
    private int health = 100;
    private int money = 700;
    private int wave = 1;
    private int score = 0;

    // final skor hesabı için
    private int enemiesDefeated = 0; // öldürülen düşman sayısı
    private int moneySpent = 0; // harcanan para

    private Tower.Type selectedTowerType = null;

    // SELL modu
    private boolean sellingMode = false;

    // PAUSE modu
    private boolean paused = false;

    // "Ready – Set – Go" countdown state ---
    private boolean showCountdown = false;
    private int countdownValue = 3; // 3 → 2 → 1 → 0("GO!")
    private Timer countdownTimer;

    // rakam sprite’lı label’lar
    private DigitLabel healthDigits;
    private DigitLabel moneyDigits;

    private MapPanel mapPanel;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();

    // tank mermileri
    private List<TankShot> tankShots = new ArrayList<>();

    // +20 / -20 efektleri
    private List<FloatingText> floatingTexts = new ArrayList<>();

    // Uçaklar ve duman efektleri
    private List<Plane> planes = new ArrayList<>();
    private List<PlaneSmoke> planeSmokes = new ArrayList<>();

    // Kule yanma efektleri
    private List<TowerFireEffect> towerFireEffects = new ArrayList<>();

    private Thread gameThread;
    private volatile boolean running = false;

    // wave / spawn sistemi
    private int enemiesPerWave = 5;
    private int enemiesSpawnedInWave = 0;
    private int spawnCooldown = 0;
    private int spawnRate = 40;

    private boolean gameOver = false;

    // wave ortası banner
    private boolean showWaveBanner = false;
    private String waveBannerText = "";
    private int waveBannerTimer = 0;

    private final Random rng = new Random();

    public GamePanel(GameFrame parentFrame, int level) {
        this.parentFrame = parentFrame;
        this.level = level;
        this.map = new GameMap(level);

        initUI();
        startGameLoop();
        startReadyCountdown(); // start 3-2-1 countdown before wave 1
    }

    // -------------------------------------------------
    // UI
    // -------------------------------------------------
    private void initUI() {
        setLayout(new BorderLayout());

        // ---- TOP BAR ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0, 160, 230));
        topBar.setOpaque(true);

        // SOL KISIM: Health, Money, kule butonları, SELL
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftPanel.setOpaque(false);

        JLabel healthText = new JLabel("Health:");
        healthText.setFont(new Font("Arial", Font.BOLD, 16));
        healthText.setForeground(Color.WHITE);

        healthDigits = new DigitLabel(3);
        healthDigits.setValue(health);

        JLabel moneyText = new JLabel("Money:");
        moneyText.setFont(new Font("Arial", Font.BOLD, 16));
        moneyText.setForeground(Color.WHITE);

        moneyDigits = new DigitLabel(4);
        moneyDigits.setValue(money);

        Dimension towerBtnSize = new Dimension(70, 60);

        JButton tower1Button = new JButton("100$"); // FAST
        JButton tower2Button = new JButton("400$"); // STRONG
        JButton tower3Button = new JButton("800$"); // ROCKET_DOUBLE
        JButton tower4Button = new JButton("1200$"); // ROCKET_BIG

        // ikon + metin alt alta
        if (Assets.greenTowerImage != null) {
            Image gIcon = Assets.greenTowerImage.getScaledInstance(45, 40, Image.SCALE_SMOOTH);
            tower1Button.setIcon(new ImageIcon(gIcon));
        }
        if (Assets.redTowerImage != null) {
            Image rIcon = Assets.redTowerImage.getScaledInstance(40, 35, Image.SCALE_SMOOTH);
            tower2Button.setIcon(new ImageIcon(rIcon));
        }
        if (Assets.rocketTowerDoubleImage != null) {
            Image img = Assets.rocketTowerDoubleImage.getScaledInstance(45, 40, Image.SCALE_SMOOTH);
            tower3Button.setIcon(new ImageIcon(img));
        }
        if (Assets.rocketTowerBigImage != null) {
            Image img = Assets.rocketTowerBigImage.getScaledInstance(45, 40, Image.SCALE_SMOOTH);
            tower4Button.setIcon(new ImageIcon(img));
        }

        for (JButton b : new JButton[] { tower1Button, tower2Button, tower3Button, tower4Button }) {
            b.setPreferredSize(towerBtnSize);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.BOTTOM);
            b.setFocusPainted(false);
        }

        JButton sellButton = new JButton("SELL");
        styleOrangeButton(sellButton);
        if (Assets.dollarIcon != null) {
            Image d = Assets.dollarIcon.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            sellButton.setIcon(new ImageIcon(d));
            sellButton.setHorizontalTextPosition(SwingConstants.CENTER);
            sellButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            sellButton.setPreferredSize(new Dimension(75, 50));
        }

        leftPanel.add(healthText);
        leftPanel.add(healthDigits);
        leftPanel.add(Box.createHorizontalStrut(15));

        leftPanel.add(moneyText);
        leftPanel.add(moneyDigits);

        leftPanel.add(Box.createHorizontalStrut(15));
        leftPanel.add(tower1Button);
        leftPanel.add(tower2Button);
        leftPanel.add(tower3Button);
        leftPanel.add(tower4Button);

        leftPanel.add(Box.createHorizontalStrut(15));

        // SAĞ KISIM: PAUSE/PLAY + LEAVE + SELL’i de buraya sığsın
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);

        // Başlangıçta PAUSE ikonu ("||")
        JButton playPauseButton = new JButton("||");
        styleBlueButton(playPauseButton);
        playPauseButton.setPreferredSize(new Dimension(50, 40));
        playPauseButton.setFont(new Font("Arial", Font.BOLD, 20));
        playPauseButton.setToolTipText("Pause / Resume");

        JButton leaveButton = new JButton("LEAVE");
        styleOrangeButton(leaveButton);
        leaveButton.setPreferredSize(new Dimension(90, 40));

        rightPanel.add(sellButton);
        rightPanel.add(playPauseButton);
        rightPanel.add(leaveButton);

        // topBar’a yerleştir
        topBar.add(leftPanel, BorderLayout.CENTER);
        topBar.add(rightPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ---------- MAP AREA ----------
        mapPanel = new MapPanel();
        add(mapPanel, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------
        tower1Button.addActionListener(e -> {
            selectedTowerType = Tower.Type.FAST;
            sellingMode = false;
        });
        tower2Button.addActionListener(e -> {
            selectedTowerType = Tower.Type.STRONG;
            sellingMode = false;
        });
        tower3Button.addActionListener(e -> {
            selectedTowerType = Tower.Type.ROCKET_DOUBLE;
            sellingMode = false;
        });
        tower4Button.addActionListener(e -> {
            selectedTowerType = Tower.Type.ROCKET_BIG;
            sellingMode = false;
        });

        // SELL butonu -> satma modu
        sellButton.addActionListener(e -> {
            selectedTowerType = null; // cancel the tower that is selected
            sellingMode = true; // sell mode on
        });

        // PAUSE/PLAY butonu
        playPauseButton.addActionListener(e -> {
            // Do not block pause/resume, even after countdown.
            paused = !paused;
            if (paused) {
                // oyun durdu -> PLAY ikonu göster
                playPauseButton.setText(">");
            } else {
                // oyun devam -> PAUSE ikonu göster
                playPauseButton.setText("||");
            }
        });

        // LEAVE butonu -> onay iste + logla + ana menü
        leaveButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "Are you sure you want to leave the game?",
                    "Confirm Leave",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                User current = UserManager.getCurrentUser();
                GameResultLogger.logGameResult(current, level, score, money, false);

                gameOver = true;
                stopGame();
                parentFrame.showMainMenu();
            }
        });
    }

    private void styleOrangeButton(AbstractButton b) {
        b.setBackground(new Color(255, 140, 0));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setOpaque(true);
    }

    private void styleBlueButton(AbstractButton b) {
        b.setBackground(new Color(0, 120, 230));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
    }

    // -------------------------------------------------
    // READY / SET / GO COUNTDOWN
    // -------------------------------------------------
    private void startReadyCountdown() {
        // Start a 3-2-1-GO overlay and keep the game paused until GO.
        showCountdown = true;
        countdownValue = 3;
        paused = true; // make sure game loop is paused at the beginning

        // Play "ready-set-go" voice line once at the start.
        SoundManager.playReadySetGo();

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(1000, e -> {
            countdownValue--;

            // When value reaches 0, we show "GO!" and unpause the game.
            if (countdownValue == 0) {
                paused = false;
            }
            // After GO! is shown for one extra second, hide countdown overlay.
            else if (countdownValue < 0) {
                showCountdown = false;
                countdownTimer.stop();
            }

            mapPanel.repaint();
        });
        countdownTimer.setInitialDelay(1000); // first tick after 1 second
        countdownTimer.start();
    }

    // -------------------------------------------------
    // WAVE & SPAWN
    // -------------------------------------------------
    private void startWave() {
        enemiesSpawnedInWave = 0;

        enemiesPerWave = 5 + (wave - 1) * 2; // her wave de +2 enemy
        spawnRate = Math.max(10, 40 - (wave - 1) * 2); // enemy çıkış sıklığı
        spawnCooldown = 0;

        waveBannerText = "WAVE " + wave;
        showWaveBanner = true;
        waveBannerTimer = 60; // ~2 saniye
    }

    private Enemy.Type chooseEnemyTypeForCurrentWave() {
        if (wave <= 2) {
            return Enemy.Type.GREEN;
        } else if (wave <= 4) {
            return (rng.nextDouble() < 0.7)
                    ? Enemy.Type.GREEN
                    : Enemy.Type.WHITE;
        } else if (wave <= 7) {
            double r = rng.nextDouble();
            if (r < 0.5)
                return Enemy.Type.GREEN;
            if (r < 0.85)
                return Enemy.Type.WHITE;
            return Enemy.Type.ORANGE;
        } else {
            double r = rng.nextDouble();
            if (r < 0.35)
                return Enemy.Type.GREEN;
            if (r < 0.7)
                return Enemy.Type.WHITE;
            if (r < 0.9)
                return Enemy.Type.ORANGE;
            return Enemy.Type.TANK_WHITE;
        }
    }

    private void startGameLoop() {
        enemies.clear();
        towers.clear();
        projectiles.clear();
        tankShots.clear();
        floatingTexts.clear();
        planes.clear();
        planeSmokes.clear();
        towerFireEffects.clear();

        wave = 1;
        gameOver = false;
        startWave();

        running = true;
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                gameLoop();
            }
        });
        gameThread.start();
    }

    private void gameLoop() {
        while (running) {
            long loopStart = System.currentTimeMillis();

            try {
                SwingUtilities.invokeLater(this::updateGame);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long elapsed = System.currentTimeMillis() - loopStart; // işlem süresi
            long wait = 30 - elapsed; // 30 ms ne kadar arttı
            if (wait < 5)
                wait = 5; // bekleme süresi min

            try {
                Thread.sleep(wait); // sabit hız
            } catch (InterruptedException e) {
                // stop signal
            }
        }
    }

    private void updateGame() {
        if (gameOver)
            return;

        // PAUSE: hiçbir şey hareket etmesin, sadece ekranı çiz
        if (paused) {
            mapPanel.repaint();
            return;
        }

        int tileW = mapPanel.getTileWidth();
        int tileH = mapPanel.getTileHeight();
        int startX = 0;
        int startY = 0;

        // wave banner süresi
        if (showWaveBanner) {
            waveBannerTimer--;
            if (waveBannerTimer <= 0) {
                showWaveBanner = false;
            }
        }

        // ---- 0) enemy spawn ----
        if (enemiesSpawnedInWave < enemiesPerWave) {
            spawnCooldown--;
            if (spawnCooldown <= 0) {
                Enemy.Type t = chooseEnemyTypeForCurrentWave();
                enemies.add(Enemy.create(map, t));
                enemiesSpawnedInWave++;
                spawnCooldown = spawnRate;
            }
        }

        // ---- 0.5) plane spawn ----
        double planeChance = (wave < 3) ? 0.0 : 0.004 + (wave - 3) * 0.001;
        if (planeChance > 0.02)
            planeChance = 0.02;

        int panelW = mapPanel.getWidth();
        int panelH = mapPanel.getHeight();

        if (panelW > 0 && panelH > 0 && rng.nextDouble() < planeChance) {
            planes.add(Plane.createRandomPlane(panelW, panelH, rng));
        }

        GameContext ctx = new GameContext(enemies, towers, projectiles, tankShots, tileW, tileH, startX, startY);

        // ---- 1) düşmanları güncelle ----
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(ctx);

            if (enemy.isFinished() && !enemy.isDead()) {
                handleEntityDeath(enemy);
                enemies.remove(i);
            }
        }

        // wave bitti mi?
        boolean allEnemiesGone = true;
        for (Enemy en : enemies) {
            // Eğer hayatta olan ve işi bitmemiş biri varsa -> Wave bitmemiş demektir.
            if (!en.isDead() && !en.isFinished()) {
                allEnemiesGone = false;
                break; // Bir kişi bile bulsam yeter, çık.
            }
        }

        if (enemiesSpawnedInWave == enemiesPerWave && allEnemiesGone) {
            wave++;
            startWave();
        }

        // ---- 1.5) uçakları güncelle ----
        if (tileW > 0 && tileH > 0) {
            for (int i = planes.size() - 1; i >= 0; i--) {
                Plane plane = planes.get(i);
                plane.update(towers, planeSmokes, tileW, tileH, startX, startY);
                if (plane.isOutOfBounds(panelW, panelH)) {
                    planes.remove(i);
                }
            }
        }

        // ---- 2) kuleler hedef bulsun + mermi oluştursun ----
        if (tileW > 0 && tileH > 0) {
            for (Tower tower : towers) {
                tower.update(ctx);
            }

            // ---- 3) kule mermilerini güncelle ----
            for (int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile p = projectiles.get(i);
                p.update(ctx);
                if (p.isFinished()) {
                    projectiles.remove(i);
                }
            }

            // ---- 3.3) tank mermilerini güncelle ----
            for (int i = tankShots.size() - 1; i >= 0; i--) {
                TankShot ts = tankShots.get(i);
                ts.update(ctx); // Fixed: passing context
                if (ts.isFinished()) {
                    tankShots.remove(i);
                }
            }
        }

        // ---- 3.4) uçak dumanlarını güncelle ----
        for (int i = planeSmokes.size() - 1; i >= 0; i--) {
            PlaneSmoke s = planeSmokes.get(i);
            s.update();
            if (s.isFinished()) {
                planeSmokes.remove(i);
            }
        }

        // ---- 3.45) kule ateş efektlerini güncelle ----
        for (int i = towerFireEffects.size() - 1; i >= 0; i--) {
            TowerFireEffect f = towerFireEffects.get(i);
            f.update();
            if (f.isFinished()) {
                towerFireEffects.remove(i);
            }
        }

        // ---- 3.5) HP’si biten kuleleri sil ----
        for (int i = towers.size() - 1; i >= 0; i--) {
            Tower t = towers.get(i);
            if (t.isDead()) {
                handleEntityDeath(t);
                towers.remove(i);
            }
        }

        // ---- 4) Ölen düşmanları kaldır, para / skor ekle ----
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.isDead()) {
                handleEntityDeath(enemy);
                enemies.remove(i);
            }
        }

        // ---- 4.5) floating text’leri güncelle ----
        for (int i = floatingTexts.size() - 1; i >= 0; i--) {
            FloatingText ft = floatingTexts.get(i);
            ft.update();
            if (ft.isFinished()) {
                floatingTexts.remove(i);
            }
        }

        // ---- 5) ekranı yenile ----
        mapPanel.repaint();
    }

    private void endGame(boolean becauseOfHealthZero) {
        if (gameOver)
            return;
        gameOver = true;

        if (running) {
            running = false;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        // --- FINAL SCORE calc (PDF formula) ---
        // Final Score = (defeatedEnemies * 20) + remainingHealth + moneySpent
        int finalScore = enemiesDefeated * 20 + health + moneySpent;
        this.score = finalScore; // skor değişkenini de bu değere sabitleyelim

        User current = UserManager.getCurrentUser();

        // kullanıcının ilgili level için en iyi skorunu güncelle
        // (UserManager tarafındaki imzaya dokunmadım)
        UserManager.updateBestScore(current, level, finalScore);

        // oyun sonucunu CSV’ye yaz
        GameResultLogger.logGameResult(current, level, finalScore, money, becauseOfHealthZero);

        // play game-over sound and stop in-game music
        SoundManager.playGameOver();
        SoundManager.stopMusic();

        // bu level için highscore (CSV’den)
        int levelHighScore = GameResultLogger.getBestScoreForUserOnLevel(current, level);

        String title = becauseOfHealthZero ? "Game Over" : "Game Ended";

        String message = "<html><center>" +
                title + "<br/><br/>" +
                "Kills: " + enemiesDefeated + "<br/>" +
                "Score: " + finalScore + "<br/>" +
                "Highscore: " + levelHighScore +
                "</center></html>";

        Object[] options = { "LEAVE", "RESTART" };

        int choice = JOptionPane.showOptionDialog(
                parentFrame,
                message,
                "Game Result",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 1) { // RESTART
            parentFrame.startGame(level);
        } else { // LEAVE veya pencereyi kapatma
            parentFrame.showMainMenu();
        }
    }

    public void stopGame() {
        if (running) {
            running = false;
        }
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }

    // Harita koordinatına tower yerleştirme
    private void placeTower(int row, int col) {
        if (selectedTowerType == null)
            return;
        if (sellingMode)
            return;

        int tile = map.getTile(row, col);
        if (tile == GameMap.TILE_PATH ||
                tile == GameMap.TILE_SPAWN ||
                tile == GameMap.TILE_GOAL ||
                tile == GameMap.TILE_STONE) { // taş olan yere tower yok
            return;
        }

        for (Tower t : towers) {
            if (t.getRow() == row && t.getCol() == col) {
                return;
            }
        }

        int cost = Tower.getCost(selectedTowerType);
        if (money < cost) { // paran yok
            return;
        }

        towers.add(Tower.create(row, col, selectedTowerType));
        money -= cost;
        if (money < 0)
            money = 0;
        moneyDigits.setValue(money);

        // final score calculation
        moneySpent += cost;

        mapPanel.repaint();
    }

    // Belirli bir hücredeki kuleyi sat
    private void sellTowerAt(int row, int col) {
        for (int i = 0; i < towers.size(); i++) {
            Tower t = towers.get(i);
            if (t.getRow() == row && t.getCol() == col) {

                int refund = Tower.getCost(t.getType()) / 2;
                towers.remove(i);

                money += refund;
                moneyDigits.setValue(money);

                sellingMode = false;
                mapPanel.repaint();
                return;
            }
        }

        // tıklanan yerde kule yoksa da moddan çık
        sellingMode = false;
    }

    // küçük helper: listeye floating text ekle
    private void addFloatingText(String text, int x, int y, Color color) {
        floatingTexts.add(new FloatingText(text, x, y, color));
    }

    // what happens when an entity dies
    // or finishes its task.
    private void handleEntityDeath(Entity e) {
        int tileW = mapPanel.getTileWidth();
        int tileH = mapPanel.getTileHeight();
        int startX = 0;
        int startY = 0;

        // 1. Düşman Öldü veya Bitti
        if (e instanceof Enemy) {
            Enemy enemy = (Enemy) e;

            if (enemy.isFinished() && !enemy.isDead()) {
                // Base'e ulaştı -> Can götür
                if (tileW > 0 && tileH > 0) {
                    Point p = enemy.getPixelCenter(tileW, tileH, startX, startY);
                    addFloatingText("-" + enemy.getLifeDamage(), p.x, p.y, Color.RED);
                }

                health -= enemy.getLifeDamage();
                if (health < 0)
                    health = 0;
                healthDigits.setValue(health);

                if (health <= 0) {
                    endGame(true);
                }
            } else if (enemy.isDead()) {
                // Oyuncu öldürdü -> Para kazan
                enemiesDefeated++;

                int rewardMoney = enemy.getRewardMoney();
                money += rewardMoney;
                moneyDigits.setValue(money);

                if (tileW > 0 && tileH > 0) {
                    Point p = enemy.getPixelCenter(tileW, tileH, startX, startY);
                    addFloatingText("+" + rewardMoney, p.x, p.y, Color.GREEN);
                }
            }
        }
        // 2. Kule Yıkıldı
        else if (e instanceof Tower) {
            Tower t = (Tower) e;
            SoundManager.playTowerExplosion();

            if (tileW > 0 && tileH > 0) {
                int cx = startX + t.getCol() * tileW + tileW / 2;
                int cy = startY + t.getRow() * tileH + tileH / 2;
                towerFireEffects.add(new TowerFireEffect(cx, cy));
            }
        }
    }

    // =================================================
    // INNER CLASS: MAP PANEL
    // =================================================
    private class MapPanel extends JPanel {

        private int tileWidth;
        private int tileHeight;

        private int hoverRow = -1;
        private int hoverCol = -1;

        public MapPanel() {
            setBackground(Color.BLACK);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleClick(e);
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (tileWidth <= 0 || tileHeight <= 0)
                        return;

                    hoverCol = e.getX() / tileWidth;
                    hoverRow = e.getY() / tileHeight;

                    repaint();
                }
            });
        }

        public int getTileWidth() {
            return tileWidth;
        }

        public int getTileHeight() {
            return tileHeight;
        }

        private void handleClick(MouseEvent e) {
            if (tileWidth <= 0 || tileHeight <= 0)
                return;

            int col = e.getX() / tileWidth;
            int row = e.getY() / tileHeight;

            if (row < 0 || row >= map.getRows() ||
                    col < 0 || col >= map.getCols()) {
                return;
            }

            if (sellingMode) {
                sellTowerAt(row, col); // SELL tower
            } else {
                placeTower(row, col);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            int rows = map.getRows();
            int cols = map.getCols();

            if (rows <= 0 || cols <= 0) {
                return;
            }

            // yeni
            tileWidth = (int) Math.ceil(getWidth() / (double) cols);
            tileHeight = (int) Math.ceil(getHeight() / (double) rows);

            if (tileWidth <= 0 || tileHeight <= 0) {
                return;
            }

            int startX = 0;
            int startY = 0;

            // 1) Zemin + overlay (path / çalı / taş)
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {

                    int tile = map.getTile(r, c);
                    int x = startX + c * tileWidth;
                    int y = startY + r * tileHeight;

                    // --- Base zemin ---
                    Image baseGround;
                    if (level == 2) {
                        // Level 2: tüm zemin toprak olsun
                        baseGround = Assets.towerSpotTile;
                    } else {
                        baseGround = Assets.grassTile;
                    }

                    if (baseGround != null) {
                        g2.drawImage(baseGround, x, y, tileWidth, tileHeight, null);
                    }

                    // --- Path / spawn / goal overlay ---
                    if (tile == GameMap.TILE_PATH ||
                            tile == GameMap.TILE_SPAWN ||
                            tile == GameMap.TILE_GOAL) {

                        Image pathImg;
                        if (level == 1) {
                            // level 1: kahverengi yol
                            pathImg = Assets.towerSpotTile;
                        } else {
                            // diğer level’lar: gri yol
                            pathImg = Assets.pathTile;
                        }

                        if (pathImg != null) {
                            g2.drawImage(pathImg, x, y, tileWidth, tileHeight, null);
                        }
                    }
                    // --- Taş / çalı overlay ---
                    else if (tile == GameMap.TILE_STONE) {
                        if (Assets.stoneImage != null) {
                            g2.drawImage(Assets.stoneImage, x, y, tileWidth, tileHeight, null);
                        }
                    } else if (tile == GameMap.TILE_BUSH_SMALL) {
                        if (Assets.bushSmallImage != null) {
                            g2.drawImage(Assets.bushSmallImage, x, y, tileWidth, tileHeight, null);
                        }
                    } else if (tile == GameMap.TILE_BUSH_ROUND) {
                        if (Assets.bushRoundImage != null) {
                            g2.drawImage(Assets.bushRoundImage, x, y, tileWidth, tileHeight, null);
                        }
                    } else if (tile == GameMap.TILE_BUSH_BIG) {
                        if (Assets.bushBigImage != null) {
                            g2.drawImage(Assets.bushBigImage, x, y, tileWidth, tileHeight, null);
                        }
                    }
                }
            }

            // 2) Enemy'leri çiz
            int tileSizeForEnemy = Math.min(tileWidth, tileHeight);
            for (Enemy enemy : enemies) {
                enemy.draw(g2, tileWidth, tileHeight, startX, startY, tileSizeForEnemy);
            }

            // 3) Tower'ları çiz
            for (Tower tower : towers) {
                tower.draw(g2, tileWidth, tileHeight, startX, startY);
            }

            // 3.3) tank mermilerini çiz
            for (TankShot ts : tankShots) {
                ts.draw(g2);
            }

            // 3.4) kule mermilerini çiz
            for (Projectile p : projectiles) {
                p.draw(g2);
            }

            // 3.45) kule ateş efektlerini çiz
            for (TowerFireEffect f : towerFireEffects) {
                f.draw(g2);
            }

            // 3.5) uçak dumanlarını çiz
            for (PlaneSmoke s : planeSmokes) {
                s.draw(g2);
            }

            // 3.6) uçakları çiz
            for (Plane plane : planes) {
                plane.draw(g2);
            }

            // 3.7) floating text’leri çiz
            for (FloatingText ft : floatingTexts) {
                ft.draw(g2);
            }

            // 4) Tower yerleştirme gölgesi (ghost)
            if (!sellingMode &&
                    selectedTowerType != null &&
                    hoverRow >= 0 && hoverCol >= 0 &&
                    hoverRow < map.getRows() && hoverCol < map.getCols()) {
                int tile = map.getTile(hoverRow, hoverCol);
                
                // döngü ile kule var mı kontrolü
                boolean towerExists = false;
                for (Tower t : towers) {
                    if (t.getRow() == hoverRow && t.getCol() == hoverCol) {
                        towerExists = true;
                        break;
                    }
                }
                boolean canPlace = tile != GameMap.TILE_PATH &&
                                   tile != GameMap.TILE_SPAWN &&
                                   tile != GameMap.TILE_GOAL &&
                                   tile != GameMap.TILE_STONE && 
                                   !towerExists;
                int x = startX + hoverCol * tileWidth;
                int y = startY + hoverRow * tileHeight;
                // ŞEFFAFLIK YOK! Sadece normal resmi çiziyoruz.
                // Kullanıcı bunun 'Ghost' olduğunu anlasın diye etrafına kutu çizeceğiz.
                Image icon;
                if (selectedTowerType == Tower.Type.STRONG) icon = Assets.redTowerImage;
                else if (selectedTowerType == Tower.Type.ROCKET_DOUBLE) icon = Assets.rocketTowerDoubleImage;
                else if (selectedTowerType == Tower.Type.ROCKET_BIG) icon = Assets.rocketTowerBigImage;
                else icon = Assets.greenTowerImage; // FAST
                if (icon != null) {
                    g2.drawImage(icon, x, y, tileWidth, tileHeight, null);
                }
                // EĞER KOYABİLİRSEM YEŞİL, KOYAMAZSAM KIRMIZI BİR KUTU ÇİZ (ÇERÇEVE)
                if (canPlace) {
                    g2.setColor(Color.GREEN);
                } else {
                    g2.setColor(Color.RED);
                }
                
                // Kalın bir çerçeve olsun dye 3   
                g2.setStroke(new BasicStroke(3)); 
                g2.drawRect(x, y, tileWidth, tileHeight);
                g2.setStroke(new BasicStroke(1)); // Kalemi normale döndür
            }

            // 5) READY / SET / GO overlay (drawn above everything)
            if (showCountdown) {
                String text = (countdownValue > 0) ? String.valueOf(countdownValue) : "GO!";
                Font font = new Font("Arial", Font.BOLD, 72);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();

                int bw = fm.stringWidth(text) + 80;
                int bh = 110;

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                int bx = cx - bw / 2;
                int by = cy - bh / 2;

                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(bx, by, bw, bh, 30, 30);

                g2.setColor(Color.WHITE);
                g2.drawRoundRect(bx, by, bw, bh, 30, 30);

                int tx = cx - fm.stringWidth(text) / 2;
                int ty = cy + fm.getAscent() / 2 - 8;
                g2.drawString(text, tx, ty);
            }
            // 6) WAVE banner’ı
            else if (showWaveBanner) {
                String text = waveBannerText;
                Font font = new Font("Arial", Font.BOLD, 32);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();

                int bw = fm.stringWidth(text) + 60;
                int bh = 70;

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                int bx = cx - bw / 2;
                int by = cy - bh / 2;

                g2.setColor(new Color(0, 150, 255));
                g2.fillRoundRect(bx, by, bw, bh, 20, 20);

                g2.setColor(Color.WHITE);
                g2.drawRoundRect(bx, by, bw, bh, 20, 20);

                int tx = cx - fm.stringWidth(text) / 2;
                int ty = cy + fm.getAscent() / 2 - 4;
                g2.drawString(text, tx, ty);
            }
        }
    }

    // =================================================
    // INNER CLASS: DigitLabel (sprite rakamlı label)
    // =================================================
    private static class DigitLabel extends JComponent {

        private int digits; // kaç haneli
        private int value; // gösterilen sayı

        public DigitLabel(int digits) {
            this.digits = digits;
            setPreferredSize(new Dimension(digits * 18, 24));
            setOpaque(false);
        }

        public void setValue(int value) {
            if (value < 0)
                value = 0;
            this.value = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            if (Assets.digitImages == null) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Consolas", Font.BOLD, 16));
                String txt = String.format("%0" + digits + "d", value);
                g2.drawString(txt, 0, getHeight() - 6);
                return;
            }

            String txt = String.format("%0" + digits + "d", value);
            int w = getWidth();
            int h = getHeight();
            int digitWidth = Math.min(18, w / digits);
            int digitHeight = h;

            for (int i = 0; i < txt.length(); i++) {
                char ch = txt.charAt(i);
                int d = ch - '0';
                if (d < 0 || d > 9)
                    continue;

                Image img = Assets.digitImages[d];
                if (img != null) {
                    int x = i * digitWidth;
                    int y = 0;
                    g2.drawImage(img, x, y, digitWidth, digitHeight, null);
                }
            }
        }
    }

    // =================================================
    // INNER CLASS: FloatingText (+20 / -20 efektleri)
    // =================================================
    private static class FloatingText {
        private static final int MAX_LIFE = 40; // 40 kare yaşar (0.6 saniye)
        private String text;
        private double x;
        private double y;
        private Color color;
        private int life;
        private double vy;

        public FloatingText(String text, int x, int y, Color color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.life = MAX_LIFE;
            this.vy = -0.7; // Yukarı doğru yavaşça kaysın
        }

        public void update() {
            y += vy; // Y koordinatını azaltarak yukarı taşım
            life--; // Ömrünü tüket
        }

        public boolean isFinished() {
            return life <= 0; // Ömür bitti mi
        }

        public void draw(Graphics2D g2) {
            if (life > 0) {
                g2.setColor(color);
                g2.setFont(new Font("Arial", Font.BOLD, 16));

                // Koordinatları int'e çevirip çiz
                g2.drawString(text, (int) x - 10, (int) y - 10);
            }
        }
    }

}
