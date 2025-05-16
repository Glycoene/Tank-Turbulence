package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import windows.Screen;

public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private Tank playerTank;
    private List<RobotTank> robots;
    private List<Bullet> bullets;

    private final int robotLimit = 5;
    private Timer robotSpawnTimer;
    private Timer gameTimer;  // 游戏主循环timer

    private boolean gameOver = false;  // 游戏是否结束（死亡）

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.LIGHT_GRAY);

        gameMap = new GameMap();
        playerTank = new Tank(Wall.SIZE, Wall.SIZE, "Tank.png");
        playerTank.setCollisionSize(40);

        robots = new ArrayList<>();
        bullets = new ArrayList<>();

        this.setFocusable(true);
        this.addKeyListener(this);

        spawnRobot(); // 初始生成一个机器人
        startRobotSpawner(); // 定时生成机器人

        startGameLoop(); // 启动游戏主循环
    }

    private void spawnRobot() {
        if (robots.size() >= robotLimit) return;

        Random random = new Random();
        int attempts = 0;
        while (attempts < 50) {
            int x = random.nextInt(700) + 50;
            int y = random.nextInt(500) + 50;
            RobotTank robot = new RobotTank(x, y, "RTank.png", gameMap, bullets, robots);
            Ellipse2D area = robot.getFutureCollisionCircle(0, 0);
            if (!gameMap.isColliding(area)) {
                robots.add(robot);
                break;
            }
            attempts++;
        }
    }

    private void startRobotSpawner() {
        robotSpawnTimer = new Timer(10000, e -> spawnRobot());
        robotSpawnTimer.start();
    }

    private void startGameLoop() {
        gameTimer = new Timer(16, e -> {
            if (!gameOver) {
                updateGameState();
                repaint();
            }
        });
        gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.draw(g);

        if (playerTank != null && !playerTank.isDead()) {
            playerTank.draw(g, this);
        }

        for (RobotTank robot : robots) {
            if (!robot.isDead()) {
                robot.draw(g, this);
            }
        }

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (playerTank == null || playerTank.isDead() || gameOver) return;

        int key = e.getKeyCode();
        Ellipse2D futureCircle;
        switch (key) {
            case KeyEvent.VK_W -> {
                futureCircle = playerTank.getFutureCollisionCircle(0, -5);
                if (!gameMap.isColliding(futureCircle)) {
                    playerTank.moveUp();
                    playerTank.setRotationAngle(0);
                }
            }
            case KeyEvent.VK_S -> {
                futureCircle = playerTank.getFutureCollisionCircle(0, 5);
                if (!gameMap.isColliding(futureCircle)) {
                    playerTank.moveDown(getHeight());
                    playerTank.setRotationAngle(180);
                }
            }
            case KeyEvent.VK_A -> {
                futureCircle = playerTank.getFutureCollisionCircle(-5, 0);
                if (!gameMap.isColliding(futureCircle)) {
                    playerTank.moveLeft();
                    playerTank.setRotationAngle(270);
                }
            }
            case KeyEvent.VK_D -> {
                futureCircle = playerTank.getFutureCollisionCircle(5, 0);
                if (!gameMap.isColliding(futureCircle)) {
                    playerTank.moveRight(getWidth());
                    playerTank.setRotationAngle(90);
                }
            }
            case KeyEvent.VK_SPACE -> fireBulletFrom(playerTank);
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    private void updateGameState() {
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (bullet.isDestroyed()) {
                toRemove.add(bullet);
                continue;
            }
            int prevX = bullet.getX(), prevY = bullet.getY();
            bullet.move();
            checkBulletCollision(bullet, prevX, prevY);
        }
        bullets.removeAll(toRemove);

        for (RobotTank robot : robots) {
            if (!robot.isDead()) {
                robot.update(getWidth(), getHeight(), playerTank);
            }
        }
    }

    private void checkBulletCollision(Bullet bullet, int prevX, int prevY) {
        Rectangle bulletBounds = bullet.getBounds();
        for (Wall wall : gameMap.getWalls()) {
            Rectangle wallBounds = wall.getBounds();
            if (bulletBounds.intersects(wallBounds)) {
                bullet.revertMovement(prevX, prevY);

                // 判断横向或纵向碰撞
                boolean hitHorizontal = false, hitVertical = false;

                if (bullet.getX() + bullet.getSpeedX() < wallBounds.x + wallBounds.width &&
                        bullet.getX() + bullet.getSpeedX() + bullet.getSize() > wallBounds.x) {
                    hitVertical = true;
                }
                if (bullet.getY() + bullet.getSpeedY() < wallBounds.y + wallBounds.height &&
                        bullet.getY() + bullet.getSpeedY() + bullet.getSize() > wallBounds.y) {
                    hitHorizontal = true;
                }

                // 简单反弹处理
                if (hitVertical) bullet.reflectHorizontal();
                if (hitHorizontal) bullet.reflectVertical();

                return;
            }
        }

        // 玩家被击中判定
        if (playerTank != null && !playerTank.isDead() && bulletBounds.intersects(playerTank.getBounds())) {
            playerTank.setDead(true);
            bullet.destroy();  // 销毁子弹
            triggerGameOver();
            return;
        }

// 机器人被击中判定
        for (RobotTank robot : robots) {
            if (!robot.isDead() && bulletBounds.intersects(robot.getBounds())) {
                robot.setDead(true);
                bullet.destroy();  // 销毁子弹
                return;
            }
        }
    }

    private void triggerGameOver() {
        gameOver = true;

        if (robotSpawnTimer != null) robotSpawnTimer.stop();
        if (gameTimer != null) gameTimer.stop();

        // 关闭游戏窗口并打开死亡界面
        SwingUtilities.invokeLater(() -> {
            closeGameWindow(); // 关闭当前窗口
            new DeathScreen(); // 打开新的死亡界面
        });
    }


    private void fireBulletFrom(Tank tank) {
        int centerX = tank.getX() + Tank.DISPLAY_SIZE / 2;
        int centerY = tank.getY() + Tank.DISPLAY_SIZE / 2;
        double angleRad = Math.toRadians(tank.getRotationAngle() - 90);
        int offset = Tank.DISPLAY_SIZE / 2;

        int bulletX = (int) (centerX + offset * Math.cos(angleRad)) - Bullet.DEFAULT_SIZE / 2;
        int bulletY = (int) (centerY + offset * Math.sin(angleRad)) - Bullet.DEFAULT_SIZE / 2;

        bullets.add(new Bullet(bulletX, bulletY, tank.getRotationAngle()));
    }

    private void closeGameWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
}
