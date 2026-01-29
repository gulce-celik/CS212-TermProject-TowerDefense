package tdgame;

import java.awt.Image;

public class BigRocketProjectile extends Projectile {
    public BigRocketProjectile(double startX, double startY, Enemy target, int damage) {
        super(startX, startY, target, damage, Kind.BIG_ROCKET);
        this.speed = 6.0;
        this.lifeTime = 140;
    }

    @Override
    public void onHit() {
        hasHit = true;
        explosionTimer = 12;
    }

    @Override
    public Image getImage() {
        return Assets.rocketBigImage;
    }

    @Override
    public int getWidth() {
        return 22;
    }

    @Override
    public int getHeight() {
        return 38;
    }
}
