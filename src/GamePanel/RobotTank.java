package GamePanel;

import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Random;

public class RobotTank extends Tank {
    private final GameMap gameMap;
    private final List<Bullet> bullets;
    private final List<RobotTank> otherRobots;
    private final Random random = new Random();

    private int moveCooldown = 0;
    private int fireCooldown = 0;

    public RobotTank(int x, int y, String imagePath, GameMap gameMap, List<Bullet> bullets, List<RobotTank> otherRobots) {
        super(x, y, imagePath);
        this.setCollisionSize(40);
        this.gameMap = gameMap;
        this.bullets = bullets;
        this.otherRobots = otherRobots;
    }

    public void update(int panelWidth, int panelHeight, Tank player) {
        if (isDead()) return;

        moveCooldown--;
        fireCooldown--;

        // 先更新旋转角度（平滑旋转）
        updateRotation();

        int dx = 0, dy = 0;
        int vectorX = player.getX() - getX();
        int vectorY = player.getY() - getY();

        if (Math.abs(vectorX) > 300 || Math.abs(vectorY) > 300) {
            // 巡逻状态：偶尔随机移动
            if (random.nextDouble() < 0.02) {
                int dir = random.nextInt(4);
                switch (dir) {
                    case 0 -> { dx = 3; setTargetAngle(90); }
                    case 1 -> { dx = -3; setTargetAngle(270); }
                    case 2 -> { dy = 3; setTargetAngle(180); }
                    case 3 -> { dy = -3; setTargetAngle(0); }
                }
                tryMove(dx, dy);
            }
        } else if (moveCooldown <= 0) {
            boolean moved = false;

            // 优先按 X 或 Y 接近玩家
            int[] dxOptions = {Integer.compare(vectorX, 0) * 3, 0};
            int[] dyOptions = {0, Integer.compare(vectorY, 0) * 3};

            // 尝试 X 方向
            if (dxOptions[0] != 0) {
                int angle = dxOptions[0] > 0 ? 90 : 270;
                setTargetAngle(angle);
                if (trySmartMove(dxOptions[0], 0)) {
                    moved = true;
                }
            }

            // 尝试 Y 方向
            if (!moved && dyOptions[1] != 0) {
                int angle = dyOptions[1] > 0 ? 180 : 0;
                setTargetAngle(angle);
                if (trySmartMove(0, dyOptions[1])) {
                    moved = true;
                }
            }

            // 如果都不通，则尝试随机方向
            if (!moved) {
                int[][] directions = {{3, 0, 90}, {-3, 0, 270}, {0, 3, 180}, {0, -3, 0}};
                shuffleArray(directions);
                for (int[] dir : directions) {
                    setTargetAngle(dir[2]);
                    if (trySmartMove(dir[0], dir[1])) {
                        break;
                    }
                }
            }

            moveCooldown = 8;
        }

        // 自动开火逻辑
        if (fireCooldown <= 0 && canSeePlayer(player)) {
            fire();
            fireCooldown = 80 + random.nextInt(60);
        }
    }

    private boolean trySmartMove(int dx, int dy) {
        Ellipse2D future = getFutureCollisionCircle(dx, dy);
        if (!gameMap.isColliding(future)) {
            setPosition(getX() + dx, getY() + dy);
            return true;
        }
        return false;
    }

    private void tryMove(int dx, int dy) {
        Ellipse2D future = getFutureCollisionCircle(dx, dy);
        if (!gameMap.isColliding(future)) {
            setPosition(getX() + dx, getY() + dy);
        }
    }

    private void fire() {
        int centerX = getX() + Tank.DISPLAY_SIZE / 2;
        int centerY = getY() + Tank.DISPLAY_SIZE / 2;
        double angleRad = Math.toRadians(getRotationAngle() - 90);
        int offset = Tank.DISPLAY_SIZE / 2;

        int bulletX = (int) (centerX + offset * Math.cos(angleRad)) - Bullet.DEFAULT_SIZE / 2;
        int bulletY = (int) (centerY + offset * Math.sin(angleRad)) - Bullet.DEFAULT_SIZE / 2;

        bullets.add(new Bullet(bulletX, bulletY, getRotationAngle(), this));
    }

    private boolean canSeePlayer(Tank player) {
        int thisX = getX() + Tank.DISPLAY_SIZE / 2;
        int thisY = getY() + Tank.DISPLAY_SIZE / 2;
        int playerX = player.getX() + Tank.DISPLAY_SIZE / 2;
        int playerY = player.getY() + Tank.DISPLAY_SIZE / 2;
        int angle = (int) getRotationAngle();

        if (angle == 0 && Math.abs(thisX - playerX) < 10 && thisY > playerY) {
            return !isWallBetween(thisX, thisY, playerX, playerY);
        }
        if (angle == 180 && Math.abs(thisX - playerX) < 10 && thisY < playerY) {
            return !isWallBetween(thisX, thisY, playerX, playerY);
        }
        if (angle == 90 && Math.abs(thisY - playerY) < 10 && thisX < playerX) {
            return !isWallBetween(thisX, thisY, playerX, playerY);
        }
        if (angle == 270 && Math.abs(thisY - playerY) < 10 && thisX > playerX) {
            return !isWallBetween(thisX, thisY, playerX, playerY);
        }

        return false;
    }

    private boolean isWallBetween(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        double stepX = dx / (double) steps;
        double stepY = dy / (double) steps;

        for (int i = 1; i < steps; i++) {
            int checkX = (int) (x1 + stepX * i);
            int checkY = (int) (y1 + stepY * i);
            if (gameMap.isWallPixel(checkX, checkY)) {
                return true;
            }
        }

        return false;
    }

    private void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
