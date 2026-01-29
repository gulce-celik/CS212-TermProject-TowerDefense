package tdgame;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Plane {

    public enum Type {
        GREEN,   // daha az hasar
        GRAY     // daha çok hasar
    }

    private double x;
    private double y;
    private double vx;
    private double vy;

    // uçağın baktığı yön (radyan)
    // sprite sağa (east) bakıyor, o yüzden ekstra offset gerekmiyor
    private double angleRad;

    private final Type type;
    private final double speed;
    private final int damageToTower;
    private final double hitRadius;  // kule merkezine olan mesafe (px)

    // Aynı kuleyi her frame vurmasın diye
    private final Set<Tower> alreadyHit = new HashSet<>();

    public Plane(Type type,
                 double startX, double startY,
                 double targetX, double targetY) {

        this.type = type;

        if (type == Type.GREEN) {
            speed         = 4.0;   // px/frame
            damageToTower = 20;
            hitRadius     = 26.0;
        } else {
            speed         = 3.5;
            damageToTower = 35;
            hitRadius     = 30.0;
        }

        this.x = startX;
        this.y = startY;

        double dx = targetX - startX; // target
        double dy = targetY - startY;
        double len = Math.sqrt(dx * dx + dy * dy); // total path
        if (len == 0) len = 1;

        vx = dx / len * speed;
        vy = dy / len * speed;

        // hız vektörüne göre açı rotate plane
        angleRad = Math.atan2(vy, vx);
    }

    //Panel boyutuna göre rastgele bir kenardan girip başka bir kenara doğru giden uçak üretir.
    public static Plane createRandomPlane(int panelWidth, int panelHeight, Random rng) {
    	//hangi kenardan girse?
        int edge = rng.nextInt(4); // 0: left, 1: right, 2: top, 3: bottom

        double startX = 0, startY = 0;
        double targetX = 0, targetY = 0;

        int margin = 40;

        switch (edge) {
            case 0 -> { // soldan gir, sağa doğru
                startX = -margin;
                startY = rng.nextInt(panelHeight);
                targetX = panelWidth + margin;
                targetY = rng.nextInt(panelHeight);
            }
            case 1 -> { // sağdan gir, sola doğru
                startX = panelWidth + margin;
                startY = rng.nextInt(panelHeight);
                targetX = -margin;
                targetY = rng.nextInt(panelHeight);
            }
            case 2 -> { // üstten gir, aşağı
                startX = rng.nextInt(panelWidth);
                startY = -margin;
                targetX = rng.nextInt(panelWidth);
                targetY = panelHeight + margin;
            }
            case 3 -> { // alttan gir, yukarı
                startX = rng.nextInt(panelWidth);
                startY = panelHeight + margin;
                targetX = rng.nextInt(panelWidth);
                targetY = -margin;
            }
        }

        Type type = (rng.nextDouble() < 0.6) ? Type.GREEN : Type.GRAY; //%60 green olsun

        return new Plane(type, startX, startY, targetX, targetY);
    }

    //Uçağı hareket ettirir ve geçtiği yerdeki kuleleri vurur.
     


    public void update(List<Tower> towers,
                       List<PlaneSmoke> smokeList,
                       int tileW, int tileH,
                       int startX, int startY) {

        x += vx; //x direction v hız kadar git
        y += vy;

        double r2 = hitRadius * hitRadius;

        for (Tower t : towers) {
            if (t.isDead()) continue;
            if (alreadyHit.contains(t)) continue;

            int tx = startX + t.getCol() * tileW + tileW / 2;
            int ty = startY + t.getRow() * tileH + tileH / 2;

            double dx = tx - x;
            double dy = ty - y;
            double dist2 = dx * dx + dy * dy; // dist between tower and plane

            if (dist2 <= r2) { // bomb tower
                t.hit(damageToTower);
                alreadyHit.add(t); // kuleyi pat yok ediyordu diye ekledin unutma

                // plane bomb sound
                SoundManager.playPlaneBomb();

                // kuleyi vurduğu yerde duman efekti
                smokeList.add(new PlaneSmoke((int) x, (int) y));
            }
        }
    }
  
    //Panel dışına çıktığında true döner, o zaman uçak listeden silinir.
    public boolean isOutOfBounds(int panelWidth, int panelHeight) {
        int margin = 80;
        return (x < -margin || x > panelWidth + margin ||
                y < -margin || y > panelHeight + margin);
    }

    public void draw(Graphics2D g2) {
        Image planeImg;
        Image shadowImg;

        if (type == Type.GREEN) {
            planeImg  = Assets.planeGreenImage;
            shadowImg = Assets.planeGreenShadow;
        } else {
            planeImg  = Assets.planeGrayImage;
            shadowImg = Assets.planeGrayShadow;
        }

        int size = 48; 
        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);

        Graphics2D gRot = (Graphics2D) g2.create();
        // uçağın merkezine taşı
        gRot.translate(ix, iy);
        // hız yönüne döndür
        gRot.rotate(angleRad);

        // Önce gölge, sonra uçak gölge biraz aşağı
        if (shadowImg != null) {
            gRot.drawImage(
                    shadowImg,
                    -size / 2,
                    -size / 2 + 10,
                    size, size,
                    null
            );
        }

        if (planeImg != null) {
            gRot.drawImage(
                    planeImg,
                    -size / 2,
                    -size / 2,
                    size, size,
                    null
            );
        }

        gRot.dispose();
    }
}

