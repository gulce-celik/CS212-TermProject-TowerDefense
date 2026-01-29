package tdgame;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.List;

public class TankWhiteEnemy extends Enemy {

    private int towerShotCooldown = 0;
    private int towerShotDelay = 80;
    private double towerShotRangeTiles = 5.0; // Menzil (5 kare)

    public TankWhiteEnemy(GameMap map) {
        super(map, Type.TANK_WHITE);
        this.speedTilesPerTick = 0.02;
        this.maxHp = 280;
        this.hp = maxHp;
    }

    @Override
    public int getRewardMoney() {
        return 50;
    }

    @Override
    public int getLifeDamage() {
        return 30;
    }

    @Override
    public Image getImage() {
        return Assets.enemyTankWhiteImage;
    }

    @Override
    public Color getFallbackColor() {
        return Color.WHITE;
    }

    @Override
    public void update(GameContext ctx) {
        super.update(ctx); // walk like a normal enemy
        if (towerShotCooldown > 0) {
            towerShotCooldown--;
        }

        // Shoot in update
        shootLogic(ctx);
    }

    // Internal helper for shooting
    private void shootLogic(GameContext ctx) {
        if (dead || finished) // finished bitiş çizgisine varmak
            return;
        if (ctx.towers.isEmpty())
            return;

        Point ep = getPixelCenter(ctx.tileW, ctx.tileH, ctx.startX, ctx.startY);
        int ex = ep.x;
        int ey = ep.y;

        double maxRangePixels = towerShotRangeTiles * Math.min(ctx.tileW, ctx.tileH);
        double maxRange2 = maxRangePixels * maxRangePixels;

        Tower closest = null;
        double bestDist2 = Double.MAX_VALUE;

        for (Tower t : ctx.towers) {
            if (t.isDead())
                continue;
            int tx = ctx.startX + t.getCol() * ctx.tileW + ctx.tileW / 2;
            int ty = ctx.startY + t.getRow() * ctx.tileH + ctx.tileH / 2;
            double dx = tx - ex;
            double dy = ty - ey;
            double dist2 = dx * dx + dy * dy;
            if (dist2 < bestDist2 && dist2 <= maxRange2) {
                bestDist2 = dist2;
                closest = t;
            }
        }

        if (closest != null && towerShotCooldown <= 0) {
            ctx.tankShots.add(new TankShot(ex, ey, closest));
            SoundManager.playShoot();
            towerShotCooldown = towerShotDelay;
        }
    }

}
