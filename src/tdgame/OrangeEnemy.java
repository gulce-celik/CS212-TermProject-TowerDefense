package tdgame;

import java.awt.Color;
import java.awt.Image;

public class OrangeEnemy extends Enemy {
    public OrangeEnemy(GameMap map) {
        super(map, Type.ORANGE);
        this.speedTilesPerTick = 0.06;
        this.maxHp = 220;
        this.hp = maxHp;
    }

    @Override
    public int getRewardMoney() {
        return 35;
    }

    @Override
    public int getLifeDamage() {
        return 20;
    }

    @Override
    public Image getImage() {
        return Assets.enemyOrangeImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.ORANGE.darker();
    }
}
