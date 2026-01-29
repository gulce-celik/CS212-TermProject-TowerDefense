package tdgame;

import java.awt.Color;
import java.awt.Image;

public class GreenEnemy extends Enemy {
    public GreenEnemy(GameMap map) {
        super(map, Type.GREEN);
        this.speedTilesPerTick = 0.03;
        this.maxHp = 100;
        this.hp = maxHp;
    }

    @Override
    public int getRewardMoney() {
        return 20;
    }

    @Override
    public int getLifeDamage() {
        return 10;
    }

    @Override
    public Image getImage() {
        return Assets.enemyGreenImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.GREEN.darker();
    }
}
