package tdgame;

import java.awt.Color;
import java.awt.Image;
import java.util.List;

public class RocketDoubleTower extends Tower {
    public RocketDoubleTower(int row, int col) {
        super(row, col, Type.ROCKET_DOUBLE);
        this.fireDelay = 45;
        this.rangeTiles = 5.0;
        this.damage = 50;
        this.projectileKind = Projectile.Kind.SMALL_ROCKET;
        this.maxHp = 180;
        this.hp = maxHp;
    }

    @Override
    protected void shoot(Enemy closest, int cx, int cy, int tileW, int tileH, List<Projectile> projectiles) {
        double barrel = Math.min(tileW, tileH) * 0.48;
        double sx = cx + Math.cos(angleRad) * barrel;
        double sy = cy + Math.sin(angleRad) * barrel;

        SoundManager.playShoot();

        // Double shot logic
        projectiles.add(Projectile.create(sx, sy, closest, damage, projectileKind));
        projectiles.add(Projectile.create(sx, sy, closest, damage, projectileKind));
    }

    @Override
    public Image getImage() {
        return Assets.rocketTowerDoubleImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.GREEN; // Default backup
    }
}
