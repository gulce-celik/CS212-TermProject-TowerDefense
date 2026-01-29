package tdgame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.List;

public abstract class Tower implements Entity {

    public enum Type {
        FAST,
        STRONG,
        ROCKET_DOUBLE,
        ROCKET_BIG
    }

    protected int row;
    protected int col;
    protected Type type;

    protected int maxHp;
    protected int hp;

    protected double angleRad = -Math.PI / 2.0;

    protected int fireCooldown = 0;
    protected int fireDelay;
    protected double rangeTiles;
    protected int damage;

    protected Projectile.Kind projectileKind;

    // Factory method
    public static Tower create(int row, int col, Type type) {
        return switch (type) {
            case FAST -> new FastTower(row, col);
            case STRONG -> new StrongTower(row, col);
            case ROCKET_DOUBLE -> new RocketDoubleTower(row, col);
            case ROCKET_BIG -> new RocketBigTower(row, col);
        };
    }

    // Static cost lookup 
    public static int getCost(Type type) {
        return switch (type) {
            case FAST -> 100;
            case STRONG -> 400;
            case ROCKET_DOUBLE -> 800;
            case ROCKET_BIG -> 1200;
        };
    }

    protected Tower(int row, int col, Type type) {
        this.row = row;
        this.col = col;
        this.type = type;

        // Default init, overridden by subclasses or set in their constructor
        this.projectileKind = Projectile.Kind.BULLET;
    }

    // Abstract logic
    public abstract Image getImage();

    public abstract Color getFallbackColor();

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Type getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void hit(int dmg) {
        hp -= dmg;
        if (hp < 0)
            hp = 0;
    }

    @Override
    public void update(GameContext ctx) {
        if (fireCooldown > 0)
            fireCooldown--;

        int cx = ctx.startX + col * ctx.tileW + ctx.tileW / 2;
        int cy = ctx.startY + row * ctx.tileH + ctx.tileH / 2;

        Enemy closest = null;
        double closestDist2 = Double.MAX_VALUE;

        double maxRangePixels = rangeTiles * Math.min(ctx.tileW, ctx.tileH);
        double maxRange2 = maxRangePixels * maxRangePixels;

        for (Enemy e : ctx.enemies) {
            if (e.isDead() || e.isFinished())
                continue;

            Point p = e.getPixelCenter(ctx.tileW, ctx.tileH, ctx.startX, ctx.startY);
            double dx = p.x - cx;
            double dy = p.y - cy;
            double dist2 = dx * dx + dy * dy;

            if (dist2 < closestDist2 && dist2 <= maxRange2) {
                closestDist2 = dist2;
                closest = e;
            }
        }

        if (closest != null) {
            Point p = closest.getPixelCenter(ctx.tileW, ctx.tileH, ctx.startX, ctx.startY);
            double dx = p.x - cx;
            double dy = p.y - cy;

            angleRad = Math.atan2(dy, dx);

            if (fireCooldown <= 0) {
                shoot(closest, cx, cy, ctx.tileW, ctx.tileH, ctx.projectiles);
                fireCooldown = fireDelay;
            }
        } else {
            angleRad = -Math.PI / 2.0;
        }
    }

    protected void shoot(Enemy closest, int cx, int cy, int tileW, int tileH, List<Projectile> projectiles) {
        double barrel = Math.min(tileW, tileH) * 0.48;
        double sx = cx + Math.cos(angleRad) * barrel;
        double sy = cy + Math.sin(angleRad) * barrel;

        SoundManager.playShoot();

        projectiles.add(Projectile.create(sx, sy, closest, damage, projectileKind));
    }

    public void draw(Graphics2D g2, int tileW, int tileH, int startX, int startY) {
        int x = startX + col * tileW;
        int y = startY + row * tileH;
        int size = Math.min(tileW, tileH);

        if (Assets.towerBasePlate != null) {
            g2.drawImage(Assets.towerBasePlate, x, y, size, size, null);
        }

        Image img = getImage();

        int cx = x + size / 2;
        int cy = y + size / 2;

        Graphics2D rot = (Graphics2D) g2.create();
        rot.translate(cx, cy);
        rot.rotate(angleRad + Math.PI / 2.0);

        if (img != null) {
            rot.drawImage(img, -size / 2, -size / 2, size, size, null);
        } else {
            rot.setColor(getFallbackColor());
            rot.fillOval(-size / 2, -size / 2, size, size);
        }
        rot.dispose();

        if (hp < maxHp) {
            int barW = (int) (size * 0.8);
            int barH = 6;
            int bx = cx - barW / 2;
            int by = y - barH - 4;

            double r = (double) hp / maxHp;
            int gw = (int) (barW * r);

            g2.setColor(Color.RED.darker());
            g2.fillRect(bx, by, barW, barH);

            g2.setColor(Color.GREEN);
            g2.fillRect(bx, by, gw, barH);

            g2.setColor(Color.BLACK);
            g2.drawRect(bx, by, barW, barH);
        }
    }
}