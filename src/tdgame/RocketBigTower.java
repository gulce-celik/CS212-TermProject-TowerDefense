package tdgame;

import java.awt.Color;
import java.awt.Image;

public class RocketBigTower extends Tower {
    public RocketBigTower(int row, int col) {
        super(row, col, Type.ROCKET_BIG);
        this.fireDelay = 60;
        this.rangeTiles = 6.0;
        this.damage = 80;
        this.projectileKind = Projectile.Kind.BIG_ROCKET;
        this.maxHp = 200;
        this.hp = maxHp;
    }

    @Override
    public Image getImage() {
        return Assets.rocketTowerBigImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.RED;
    }
}
