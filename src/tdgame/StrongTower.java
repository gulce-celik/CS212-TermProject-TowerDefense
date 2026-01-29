package tdgame;

import java.awt.Color;
import java.awt.Image;

public class StrongTower extends Tower {
    public StrongTower(int row, int col) {
        super(row, col, Type.STRONG);
        this.fireDelay = 30;
        this.rangeTiles = 5.0;
        this.damage = 40;
        this.projectileKind = Projectile.Kind.BULLET;
        this.maxHp = 150;
        this.hp = maxHp;
    }

    @Override
    public Image getImage() {
        return Assets.redTowerImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.RED;
    }
}
