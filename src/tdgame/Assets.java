package tdgame;

import javax.swing.*;
import java.awt.*;

public class Assets {

    // Map tiles
    public static Image grassTile;
    public static Image pathTile;
    public static Image towerSpotTile;   // zemindeki kahverengi kare
    public static Image spawnTile;
    public static Image goalTile;

    // Dekorasyon: çalılar ve taş
    public static Image bushSmallImage;   // 132
    public static Image bushRoundImage;   // 133
    public static Image bushBigImage;     // 134
    public static Image stoneImage;       // 136

    // Towers
    public static Image greenTowerImage;
    public static Image redTowerImage;

    // Kurşun / alev efekti
    public static Image fireImage;

    // --- kule plakası + roket kuleleri / mermiler / patlama ---
    public static Image towerBasePlate;          // 181
    public static Image rocketTowerDoubleImage;  // 205 (2 küçük roket)
    public static Image rocketTowerBigImage;     // 206 (1 büyük roket)

    public static Image rocketSmallImage;        // 251
    public static Image rocketBigImage;          // 252
    public static Image explosionImage;          // 297

    // rakam sprite’ları ve dolar ikonu ---
    public static Image[] digitImages;           // 0..9  -> 276..285
    public static Image dollarIcon;              // 287

    // Enemy çeşitleri
    public static Image enemyGreenImage;         // 245
    public static Image enemyWhiteImage;         // 246
    public static Image enemyOrangeImage;        // 247
    public static Image enemyTankWhiteImage;     // 269

    // Tank mermisi (pixel 296)
    public static Image tankShotImage;

    // Uçaklar + gölgeleri + duman efekti ---
    public static Image planeGreenImage;         // 270
    public static Image planeGrayImage;          // 271
    public static Image planeGreenShadow;        // 293
    public static Image planeGrayShadow;         // 294
    public static Image planeBombSmoke;          // 021 (duman)

    public static void load() {
        // ground tiles
        grassTile     = loadImage("images/towerDefense_tile024.png"); // green grass
        pathTile      = loadImage("images/towerDefense_tile034.png"); // grey path
        towerSpotTile = loadImage("images/towerDefense_tile158.png"); // brown dirt (tower platform)

        // spawn/goal
        spawnTile = pathTile;
        goalTile  = pathTile;

        // Dekorasyon sprite’ları
        bushSmallImage  = loadImage("images/towerDefense_tile132.png");
        bushRoundImage  = loadImage("images/towerDefense_tile133.png");
        bushBigImage    = loadImage("images/towerDefense_tile134.png");
        stoneImage      = loadImage("images/towerDefense_tile136.png");

        // towers / enemy / bullet
        greenTowerImage  = loadImage("images/towerDefense_tile249.png");
        redTowerImage    = loadImage("images/towerDefense_tile250.png");

        enemyGreenImage      = loadImage("images/towerDefense_tile245.png");
        enemyWhiteImage      = loadImage("images/towerDefense_tile246.png");
        enemyOrangeImage     = loadImage("images/towerDefense_tile247.png");
        enemyTankWhiteImage  = loadImage("images/towerDefense_tile269.png");

        fireImage        = loadImage("images/towerDefense_tile298.png");
        tankShotImage    = loadImage("images/towerDefense_tile296.png");

        // kule tabanı + roket kule sprite’ları
        towerBasePlate         = loadImage("images/towerDefense_tile181.png");
        rocketTowerDoubleImage = loadImage("images/towerDefense_tile205.png");
        rocketTowerBigImage    = loadImage("images/towerDefense_tile206.png");

        // roket mermileri + patlama
        rocketSmallImage = loadImage("images/towerDefense_tile251.png");
        rocketBigImage   = loadImage("images/towerDefense_tile252.png");
        explosionImage   = loadImage("images/towerDefense_tile297.png");

        //  rakamları 276..285
        digitImages = new Image[10];
        for (int i = 0; i < 10; i++) {
            int tileIndex = 276 + i; // 276..285
            digitImages[i] = loadImage("images/towerDefense_tile" + tileIndex + ".png");
        }

        // SELL için $ ikonu 287
        dollarIcon = loadImage("images/towerDefense_tile287.png");

        // Uçaklar ve gölgeleri
        planeGreenImage   = loadImage("images/towerDefense_tile270.png");
        planeGrayImage    = loadImage("images/towerDefense_tile271.png");
        planeGreenShadow  = loadImage("images/towerDefense_tile293.png");
        planeGrayShadow   = loadImage("images/towerDefense_tile294.png");

        // Uçağın kuleyi vururken çıkardığı duman (021)
        planeBombSmoke = loadImage("images/towerDefense_tile021.png");
    }

    private static Image loadImage(String path) {
        return new ImageIcon(path).getImage();
    }
}

