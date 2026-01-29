package tdgame;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public abstract class Enemy implements Entity {

    public enum Type {
        GREEN,
        WHITE,
        ORANGE,
        TANK_WHITE
    }

    protected GameMap map;
    protected List<Point> path;
    protected double pathPos = 0.0;
    protected double speedTilesPerTick;

    protected boolean finished = false;
    protected boolean dead = false;

    protected int maxHp;
    protected int hp;

    protected Type type;

    // Factory method
    public static Enemy create(GameMap map, Type type) {
        return switch (type) {
            case GREEN -> new GreenEnemy(map);
            case WHITE -> new WhiteEnemy(map);
            case ORANGE -> new OrangeEnemy(map);
            case TANK_WHITE -> new TankWhiteEnemy(map);
        };
    }

    protected Enemy(GameMap map, Type type) {
        this.map = map;
        this.type = type;
        this.path = buildPath(map);
        this.pathPos = 0.0;

        // Default values, can be overridden by subclasses
        this.speedTilesPerTick = 0.03;
        this.maxHp = 100;
        this.hp = maxHp;
    }

    // Abstract definitions for things that differ
    public abstract int getRewardMoney();

    public abstract int getLifeDamage();

    public abstract java.awt.Image getImage();

    public abstract Color getFallbackColor();

    // ---------------- PATH ----------------
    private List<Point> buildPath(GameMap map) {
        int rows = map.getRows();
        int cols = map.getCols();
        Point start = null;
        Point goal = null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int t = map.getTile(r, c);
                if (t == GameMap.TILE_SPAWN)
                    start = new Point(c, r);
                if (t == GameMap.TILE_GOAL)
                    goal = new Point(c, r);
            }
        }
        if (start == null || goal == null) {
            List<Point> simple = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                for (int r = 0; r < rows; r++) {
                    if (map.getTile(r, c) == GameMap.TILE_PATH)
                        simple.add(new Point(c, r));
                }
            }
            if (simple.isEmpty())
                simple.add(new Point(0, rows / 2));
            return simple;
        }
        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];
        Queue<Point> q = new ArrayDeque<>();
        q.add(start);
        visited[start.y][start.x] = true;
        while (!q.isEmpty()) {
            Point p = q.remove();
            if (p.equals(goal))
                break;
            for (int[] d : dirs) {
                int nc = p.x + d[0];
                int nr = p.y + d[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols)
                    continue;
                if (visited[nr][nc])
                    continue;
                int tile = map.getTile(nr, nc);
                if (tile == GameMap.TILE_PATH || tile == GameMap.TILE_SPAWN || tile == GameMap.TILE_GOAL) {
                    visited[nr][nc] = true;
                    parent[nr][nc] = p;
                    q.add(new Point(nc, nr));
                }
            }
        }
        List<Point> result = new ArrayList<>();
        if (!visited[goal.y][goal.x]) {
            result.add(start);
            result.add(goal);
            return result;
        }
        Point cur = goal;
        while (cur != null) {
            result.add(cur);
            cur = parent[cur.y][cur.x];
        }
        Collections.reverse(result);
        return result;
    }

    @Override
    public void update(GameContext ctx) {
        if (finished || dead)
            return;
        if (path.size() < 2) {
            finished = true;
            return;
        }
        pathPos += speedTilesPerTick;
        if (pathPos >= path.size() - 1) {
            finished = true;
        }
    }


    public void updateAndShootTowers(List<Tower> towers, List<TankShot> shots, int tileW, int tileH, int startX,
            int startY) {
        // Default
    }

    public void draw(Graphics2D g2, int tileW, int tileH, int startX, int startY, int tileSizeForEnemy) {
        if (dead)
            return;
        Point p = getPixelCenter(tileW, tileH, startX, startY);
        int size = tileSizeForEnemy;
        double angle = getDirectionAngle();
        Graphics2D gRot = (Graphics2D) g2.create();
        gRot.translate(p.x, p.y);
        gRot.rotate(angle);

        java.awt.Image img = getImage();
        if (img != null) {
            gRot.drawImage(img, -size / 2, -size / 2, size, size, null);
        } else {
            gRot.setColor(getFallbackColor());
            gRot.fillOval(-size / 2, -size / 2, size, size);
        }
        gRot.dispose();
    }

    public Point getPixelCenter(int tileW, int tileH, int startX, int startY) {
        if (path == null || path.isEmpty())
            return new Point(startX, startY);
        int index = (int) Math.floor(pathPos);
        if (index >= path.size() - 1)
            index = path.size() - 2;
        double frac = pathPos - index;
        Point a = path.get(index);
        Point b = path.get(index + 1);
        double col = a.x + (b.x - a.x) * frac;
        double row = a.y + (b.y - a.y) * frac;
        int cx = startX + (int) (col * tileW) + tileW / 2;
        int cy = startY + (int) (row * tileH) + tileH / 2;
        return new Point(cx, cy);
    }

    private double getDirectionAngle() {
        if (path == null || path.size() < 2)
            return 0.0;
        int index = (int) Math.floor(pathPos);
        if (index >= path.size() - 1)
            index = path.size() - 2;
        Point a = path.get(index);
        Point b = path.get(index + 1);
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.atan2(dy, dx);
    }

    public boolean hit(int damage) {
        if (dead || finished)
            return false;
        hp -= damage;
        if (hp <= 0) {
            dead = true;
            return true;
        }
        return false;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isDead() {
        return dead;
    }

    public Type getType() {
        return type;
    }
}
