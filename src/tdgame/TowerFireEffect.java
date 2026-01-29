package tdgame;

import java.awt.Graphics2D;

public class TowerFireEffect {

    private static final int MAX_LIFE = 40; 

    private int x;
    private int y;
    private int life;

    public TowerFireEffect(int x, int y) {
        this.x = x;
        this.y = y;
        this.life = MAX_LIFE;
    }

    public void update() {
        life--;
    }

    public boolean isFinished() {
        return life <= 0;
    }

    public void draw(Graphics2D g2) {
        if (Assets.fireImage == null) return;

        // Sadece ömrü bitmediyse çiz
        if (life > 0) {
            int size = 52; 
            g2.drawImage(
                Assets.fireImage,
                x - size / 2,
                y - size / 2,
                size, size,
                null
            );
        }
    }
}







/*
package tdgame;

import java.awt.*;

public class TowerFireEffect {

    private static final int MAX_LIFE = 40;

    private double x;
    private double y;
    private int life;
    private double scale;

    public TowerFireEffect(int x, int y) {
        this.x = x;
        this.y = y;
        this.life = MAX_LIFE;
        this.scale = 1.0;
    }

    public void update() {
        life--;
    }

    public boolean isFinished() {
        return life <= 0;
    }

    public void draw(Graphics2D g2) {
        if (Assets.fireImage == null) return;

        float alpha = (float) life / (float) MAX_LIFE;
        if (alpha < 0f) alpha = 0f;

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int baseSize = 52;   // kule boyutuna yakın
        int size = (int) (baseSize * scale);

        g2.drawImage(
                Assets.fireImage,
                (int) x - size / 2,
                (int) y - size / 2,
                size, size,
                null
        );

        g2.setComposite(old);
    }
}*/
