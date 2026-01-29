package tdgame;

import java.awt.*;

public class PlaneSmoke {

    private static final int MAX_LIFE = 30;

    private double x;
    private double y;
    private double vy = -0.3; // hafif yukarı kay
    private int life = MAX_LIFE;

    public PlaneSmoke(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += vy;
        life--;
    }

    public boolean isFinished() {
        return life <= 0;
    }

    public void draw(Graphics2D g2) {
        // Duman görseli yoksa çizme
        if (Assets.planeBombSmoke == null)
            return;

        //Dumanın ömrü bitmediyse çiz
        if (life > 0) {
            int size = 40;
            // Resmi (x,y) noktasına ortalayarak çiz
            g2.drawImage(Assets.planeBombSmoke,
                    (int) x - size / 2,
                    (int) y - size / 2,
                    size, size,
                    null);
        }
    }
}
