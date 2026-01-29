package tdgame;

import java.awt.*;

public class TankShot implements Entity {

    private double x, y;
    private Tower target;
    private double speed = 5.0;
    private boolean finished = false;
    private double angleRad;

    private int damage = 20; // tankın kuleye verdiği hasar

    public TankShot(double startX, double startY, Tower target) {
        this.x = startX;
        this.y = startY;
        this.target = target;
    }

    @Override
    public void update(GameContext ctx) {
        if (finished)
            return;
        if (target == null || target.isDead()) {
            finished = true;
            return;
        }

        // tower merkez pikseli
        int tx = ctx.startX + target.getCol() * ctx.tileW + ctx.tileW / 2;
        int ty = ctx.startY + target.getRow() * ctx.tileH + ctx.tileH / 2;

        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.0001)
            dist = 0.0001;

        angleRad = Math.atan2(dy, dx);

        double hitRadius = Math.min(ctx.tileW, ctx.tileH) * 0.35;

        if (dist <= hitRadius) {
            // kuleye damage ver
            target.hit(damage);
            finished = true;
            return;
        }

        double ux = dx / dist;
        double uy = dy / dist;

        x += ux * speed;
        y += uy * speed;
    }

    public void draw(Graphics2D g2) {
        if (finished)
            return;

        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        Graphics2D gRot = (Graphics2D) g2.create();
        gRot.translate(px, py);
        gRot.rotate(angleRad + Math.PI / 2.0);

        Image img = Assets.tankShotImage;
        int w = 18, h = 28; // biraz büyük

        if (img != null) {
            gRot.drawImage(img, -w / 2, -h / 2, w, h, null);
        } else {
            gRot.setColor(Color.ORANGE);
            gRot.fillOval(-8, -8, 16, 16);
        }

        gRot.dispose();
    }

    public boolean isFinished() {
        return finished;
    }
}
