package GamePanel;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    public static final int ROWS = 12;
    public static final int COLS = 16;
    private int[][] map = new int[ROWS][COLS]; // 0空地 1墙
    private List<Wall> walls = new ArrayList<>();

    public GameMap() {
        generateRandomMap();
    }

    private void generateRandomMap() {
        Random rand = new Random();
        walls.clear();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (rand.nextDouble() < 0.3) {
                    map[y][x] = 1;
                    walls.add(new Wall(x * Wall.SIZE, y * Wall.SIZE));
                } else {
                    map[y][x] = 0;
                }
            }
        }
    }

    public void draw(Graphics g) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
    }

    public boolean isWallAt(int x, int y) {
        int col = x / Wall.SIZE;
        int row = y / Wall.SIZE;
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return true;
        return map[row][col] == 1;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    // ✅ 新增支持 Ellipse2D 的碰撞检测
    public boolean isColliding(Ellipse2D futureCircle) {
        for (Wall wall : walls) {
            if (futureCircle.intersects(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }
}
