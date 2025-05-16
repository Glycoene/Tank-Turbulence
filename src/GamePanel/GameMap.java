package GamePanel;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    public static final int ROWS = 12;  // 可以根据需要调整
    public static final int COLS = 16;  // 可以根据需要调整
    private int[][] map = new int[ROWS][COLS]; // 0空地 1墙
    private List<Wall> walls = new ArrayList<>();

    // 构造函数，生成地图
    public GameMap() {
        generateRandomMap(0.3);  // 增加一个参数来控制墙壁的概率
    }

    // 随机生成地图并放置墙壁
    private void generateRandomMap(double wallProbability) {
        Random rand = new Random();
        walls.clear();

        // 定义安全出生区（左上角 3x3 区域）
        int safeZoneSize = 3;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                // 跳过安全区，不放墙
                if (x < safeZoneSize && y < safeZoneSize) {
                    map[y][x] = 0;
                    continue;
                }

                // 随机决定是否放置墙
                if (rand.nextDouble() < wallProbability) {
                    map[y][x] = 1;
                    walls.add(new Wall(x * Wall.SIZE, y * Wall.SIZE));
                } else {
                    map[y][x] = 0;
                }
            }
        }

        // 确保安全区至少一条通路通向外部
        map[safeZoneSize - 1][safeZoneSize] = 0;  // 向右一格
        map[safeZoneSize][safeZoneSize - 1] = 0;  // 向下
    }


    // 绘制地图，包括墙壁
    public void draw(Graphics g) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
    }

    // 检查某个位置是否是墙壁
    public boolean isWallAt(int x, int y) {
        int col = x / Wall.SIZE;
        int row = y / Wall.SIZE;
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return true;  // 防止越界
        return map[row][col] == 1;  // 1 表示墙壁
    }

    // 获取所有墙壁
    public List<Wall> getWalls() {
        return walls;
    }

    // 增加一个支持 Ellipse2D 类型的碰撞检测方法
    public boolean isColliding(Ellipse2D futureCircle) {
        for (Wall wall : walls) {
            if (futureCircle.intersects(wall.getBounds())) {
                return true;  // 如果碰撞，返回 true
            }
        }
        return false;  // 否则返回 false
    }

    // 重新设置墙壁的生成概率，方便调节地图难度
    public void setWallGenerationProbability(double probability) {
        generateRandomMap(probability);  // 重新生成墙壁
    }

    // 可以提供获取地图大小的方法
    public int getRows() {
        return ROWS;
    }

    public int getCols() {
        return COLS;
    }
    public boolean isWallPixel(int pixelX, int pixelY) {
        if (pixelX < 0 || pixelY < 0 || pixelX >= COLS * Wall.SIZE || pixelY >= ROWS * Wall.SIZE) {
            return true; // 越界视为墙
        }
        int col = pixelX / Wall.SIZE;
        int row = pixelY / Wall.SIZE;
        return map[row][col] == 1;
    }
}
