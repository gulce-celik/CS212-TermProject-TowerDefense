package tdgame;

import java.awt.Color;
import java.awt.Image;

public class FastTower extends Tower {
    public FastTower(int row, int col) {
        super(row, col, Type.FAST);
        this.fireDelay = 20;
        this.rangeTiles = 3.0;
        this.damage = 15;
        this.projectileKind = Projectile.Kind.BULLET;
        this.maxHp = 110;
        this.hp = maxHp;
    }

    @Override
    public Image getImage() {
        return Assets.greenTowerImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.GREEN;
    }
}
