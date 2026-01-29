package tdgame;

import java.awt.Image;

public class BulletProjectile extends Projectile {
    public BulletProjectile(double startX, double startY, Enemy target, int damage) {
        super(startX, startY, target, damage, Kind.BULLET);
        this.speed = 10.0;
        this.lifeTime = 90;
    }

    @Override
    public void onHit() {
        finished = true;
    }

    @Override
    public Image getImage() {
        return Assets.fireImage;
    }

    @Override
    public int getWidth() {
        return 14;
    }

    @Override
    public int getHeight() {
        return 24;
    }
}
