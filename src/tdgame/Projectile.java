package tdgame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

public abstract class Projectile implements Entity {

    public enum Kind {
        BULLET,
        SMALL_ROCKET,
        BIG_ROCKET
    }

    protected double x;
    protected double y;

    protected Enemy target;
    protected int damage;

    protected double speed;
    protected int lifeTime;
    protected boolean finished = false;

    protected Kind kind;

    protected double angleRad = 0.0;

    // Explosion logic
    protected boolean hasHit = false;
    protected int explosionTimer = 0;

    // Factory method
    public static Projectile create(double startX, double startY,
            Enemy target, int damage, Kind kind) {
        return switch (kind) {
            case BULLET -> new BulletProjectile(startX, startY, target, damage);
            case SMALL_ROCKET -> new SmallRocketProjectile(startX, startY, target, damage);
            case BIG_ROCKET -> new BigRocketProjectile(startX, startY, target, damage);
        };
    }

    protected Projectile(double startX, double startY,
            Enemy target, int damage, Kind kind) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.damage = damage;
        this.kind = kind;

        // Defaults, override in subclasses
        this.speed = 10.0;
        this.lifeTime = 90;
    }

    // Abstract visuals
    public abstract Image getImage();

    public abstract int getWidth();

    public abstract int getHeight();

    // Abstract hook for hit logic customization
    public abstract void onHit();

    @Override
    public void update(GameContext ctx) {
        if (finished)
            return;

        // patlama aşamasındaysa sadece say
        if (hasHit) {
            explosionTimer--;
            if (explosionTimer <= 0) {
                finished = true;
            }
            return;
        }

        // hedef kaybolduysa mermiyi bitir
        if (target == null || target.isDead() || target.isFinished()) {
            finished = true;
            return;
        }

        Point tp = target.getPixelCenter(ctx.tileW, ctx.tileH, ctx.startX, ctx.startY);
        double dx = tp.x - x;
        double dy = tp.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.0001)
            dist = 0.0001;

        angleRad = Math.atan2(dy, dx);

        // çarpışma yarıçapı
        double hitRadius = Math.min(ctx.tileW, ctx.tileH) * 0.35;
        if (kind != Kind.BULLET) {
            hitRadius *= 1.2; // roketler biraz daha geniş vursun
        }

        if (dist <= hitRadius) {
            // hedefe hasar ver
            target.hit(damage);
            onHit();
            return;
        }

        // hedefe doğru ilerle
        double ux = dx / dist;
        double uy = dy / dist;

        x += ux * speed;
        y += uy * speed;

        lifeTime--;
        if (lifeTime <= 0) {
            finished = true;
        }
    }

    public void draw(Graphics2D g2) {
        if (finished)
            return;

        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        // roket patlama aşamasındaysa patlamayı çiz
        if (hasHit) {
            Image ex = Assets.explosionImage; // mini alev
            if (ex != null) {
                int size = 36;
                g2.drawImage(ex, px - size / 2, py - size / 2, size, size, null);
            } else {
                g2.setColor(Color.ORANGE);
                g2.fillOval(px - 14, py - 14, 28, 28);
            }
            return;
        }

        // normal mermi / roket sprite’ı
        Graphics2D gRot = (Graphics2D) g2.create();
        gRot.translate(px, py);
        gRot.rotate(angleRad + Math.PI / 2.0);

        Image sprite = getImage();
        int w = getWidth();
        int h = getHeight();

        if (sprite != null) {
            gRot.drawImage(sprite, -w / 2, -h / 2, w, h, null);
        } else {
            gRot.setColor(kind == Kind.BIG_ROCKET ? Color.RED : Color.YELLOW);
            gRot.fillOval(-w / 2, -h / 2, w, h);
        }

        gRot.dispose();
    }

    public boolean isFinished() {
        return finished;
    }
}