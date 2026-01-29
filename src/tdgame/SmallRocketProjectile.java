package tdgame;

import java.awt.Image;

public class SmallRocketProjectile extends Projectile {
    public SmallRocketProjectile(double startX, double startY, Enemy target, int damage) {
        super(startX, startY, target, damage, Kind.SMALL_ROCKET);
        this.speed = 7.0;
        this.lifeTime = 120;
    }

    @Override
    public void onHit() {
        hasHit = true;
        explosionTimer = 12;
    }

    @Override
    public Image getImage() {
        return Assets.rocketSmallImage;
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 32;
    }
}
