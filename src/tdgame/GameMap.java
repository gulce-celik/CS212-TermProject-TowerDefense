package tdgame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {

    public static final int TILE_GRASS = 0;
    public static final int TILE_PATH  = 1;
    public static final int TILE_TOWER = 2;
    public static final int TILE_SPAWN = 3;
    public static final int TILE_GOAL  = 4;

    // Dekorasyon / engel tile’ları
    public static final int TILE_STONE       = 5; // taşa tower konulamaz
    public static final int TILE_BUSH_SMALL  = 6;
    public static final int TILE_BUSH_ROUND  = 7;
    public static final int TILE_BUSH_BIG    = 8;

    public static final int ROWS = 15;
    public static final int COLS = 20;

    private int[][] grid;
    private int level;

    // path: her nokta (col, row) = (x, y)
    private List<Point> path = new ArrayList<>();

    public GameMap(int level) {
        this.level = level;
        grid = new int[ROWS][COLS];
        generateLayoutForLevel(level);
    }

    // default: level 1
    public GameMap() {
        this(1);
    }

    private void clearToGrass() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = TILE_GRASS;
            }
        }
        path.clear();
    }

    private void generateLayoutForLevel(int level) {
        switch (level) {
            case 1:
                generateLevel1();
                break;
            case 2:
                generateLevel2();
                break;
            case 3:
                generateLevel3();
                break;
            default:
                generateLevel1();
                break;
        }
    }

    // LEVEL 1 – kahverengi, kıvrımlı path
    private void generateLevel1() {
        clearToGrass();

        //İlk nokta (8,0)  SPAWN (Doğuş
        int[][] pathCells = new int[][]{
                // soldan başla, sağa
                {8,0},{8,1},{8,2},{8,3},{8,4},
                // yukarı
                {7,4},{6,4},
                // sağa
                {6,5},{6,6},{6,7},{6,8},{6,9},
                // aşağı
                {7,9},{8,9},{9,9},
                // sağa
                {9,10},{9,11},{9,12},{9,13},{9,14},{9,15},
                // yukarı
                {8,15},{7,15},{6,15},
                // sağa, goal’a kadar
                {6,16},{6,17},{6,18},{6,19}
        };//Son nokta (6,19)  GOAL Bitiş

        setPathFromArray(pathCells);
        addDecorations();
    }

    // LEVEL 2 – gri yol, arka plan toprak olacak (GamePanel tarafında)
    private void generateLevel2() {
        clearToGrass();

        int[][] pathCells = new int[][]{
                {5,0},{5,1},{5,2},{5,3},{5,4},{5,5},{5,6},
                {4,6},{3,6},{2,6},
                {2,7},{2,8},{2,9},{2,10},{2,11},{2,12},
                {3,12},{4,12},{5,12},{6,12},{7,12},{8,12},{9,12},{10,12},
                {10,13},{10,14},{10,15},{10,16},{10,17},{10,18},{10,19}
        };

        setPathFromArray(pathCells);
        addDecorations();
    }

    // LEVEL 3 – gri, daha uzun “snake” yolu
    private void generateLevel3() {
        clearToGrass();

        int[][] pathCells = new int[][]{
                {3,0},{3,1},{3,2},{3,3},{3,4},{3,5},{3,6},
                {4,6},{5,6},{6,6},{7,6},{8,6},
                {8,5},{8,4},{8,3},{8,2},{8,1},
                {9,1},{10,1},{11,1},{12,1},
                {12,2},{12,3},{12,4},{12,5},{12,6},{12,7},{12,8},{12,9},
                {12,10},{12,11},{12,12},{12,13},{12,14},{12,15},{12,16},
                {12,17},{12,18},{12,19}
        };

        setPathFromArray(pathCells);
        addDecorations();
    }

    // path dizisini kullanarak grid + path listesini kur
    private void setPathFromArray(int[][] pathCells) {
        path.clear();

        // tüm path hücrelerini yerleştir
        for (int[] p : pathCells) {
            int r = p[0];
            int c = p[1];
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                grid[r][c] = TILE_PATH;
                // path içinde (col, row) olarak 
                path.add(new Point(c, r));
            }
        }

        // spawn = ilk hücre
        int spawnRow = pathCells[0][0];
        int spawnCol = pathCells[0][1];
        grid[spawnRow][spawnCol] = TILE_SPAWN;

        // goal = son hücre
        int lastIndex = pathCells.length - 1;
        int goalRow = pathCells[lastIndex][0];
        int goalCol = pathCells[lastIndex][1];
        grid[goalRow][goalCol] = TILE_GOAL;
    }

    //Path olmayan grass hücrelerine rastgele çalı / taş .Çalılar tower’a engel değil, taş engel.
    private void addDecorations() {
        Random rnd = new Random(level * 1234L); // level’a göre

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] != TILE_GRASS) continue;

                double v = rnd.nextDouble();

                // %2 civarı taş
                if (v < 0.02) {
                    grid[r][c] = TILE_STONE;
                }
                // Sonraki ~%8 civarı çalı
                else if (v < 0.10) {
                    double b = rnd.nextDouble();
                    if (b < 0.33) {
                        grid[r][c] = TILE_BUSH_SMALL;
                    } else if (b < 0.66) {
                        grid[r][c] = TILE_BUSH_ROUND;
                    } else {
                        grid[r][c] = TILE_BUSH_BIG;
                    }
                }
                // geri kalanı düz grass  
            }
        }
    }

    public int getTile(int row, int col) {
        return grid[row][col];
    }

    public int getRows() {
        return ROWS;
    }

    public int getCols() {
        return COLS;
    }

    public int getLevel() {
        return level;
    }

    // Enemy’nin kullanacağı path (tile koordinatı listesi)
    public List<Point> getPath() {
        return path;
    }
}
