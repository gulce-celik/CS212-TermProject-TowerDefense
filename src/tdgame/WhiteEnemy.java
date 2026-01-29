package tdgame;

import java.awt.Color;
import java.awt.Image;

public class WhiteEnemy extends Enemy {
    public WhiteEnemy(GameMap map) {
        super(map, Type.WHITE);
        this.speedTilesPerTick = 0.045;
        this.maxHp = 150;
        this.hp = maxHp;
    }

    @Override
    public int getRewardMoney() {
        return 25;
    }

    @Override
    public int getLifeDamage() {
        return 15;
    }

    @Override
    public Image getImage() {
        return Assets.enemyWhiteImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.LIGHT_GRAY;
    }
}
