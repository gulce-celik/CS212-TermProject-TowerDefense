package tdgame;

import java.util.List;

//Holds the current state of the game frame/tick for Entities to use during update.
//This allows all Entities to share a common update(GameContext) signature.
public class GameContext {
    public List<Enemy> enemies;
    public List<Tower> towers;
    public List<Projectile> projectiles;
    public List<TankShot> tankShots;

    public int tileW;
    public int tileH;
    public int startX; // Usually 0
    public int startY; // Usually 0


    public GameContext(List<Enemy> enemies, List<Tower> towers, List<Projectile> projectiles, List<TankShot> tankShots,
            int tileW, int tileH, int startX, int startY) {
        this.enemies = enemies;
        this.towers = towers;
        this.projectiles = projectiles;
        this.tankShots = tankShots;
        this.tileW = tileW;
        this.tileH = tileH;
        this.startX = startX;
        this.startY = startY;
    }
}
