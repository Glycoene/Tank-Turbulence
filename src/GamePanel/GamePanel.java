package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private Tank playerTank;
    private Move moveLogic;
    private List<RobotTank> robots;
    private List<Bullet> bullets;

    private final int robotLimit = 5;
    private Timer robotSpawnTimer;
    private Timer gameTimer;
    private boolean gameOver = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.LIGHT_GRAY);

        gameMap = new GameMap();
        playerTank = new Tank(Wall.SIZE, Wall.SIZE, "Tank.png");
        playerTank.setCollisionSize(40);
        moveLogic = new Move(playerTank, gameMap);

        robots = new ArrayList<>();
        bullets = new ArrayList<>();

        this.setFocusable(true);
        this.addKeyListener(this);

        spawnRobot();
        startRobotSpawner();
        startGameLoop();
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

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_A ||
                key == KeyEvent.VK_S || key == KeyEvent.VK_D) {
            moveLogic.handleKeyPress(key, getWidth(), getHeight());
        } else if (key == KeyEvent.VK_SPACE) {
            fireBulletFrom(playerTank);
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

                boolean hitHorizontal = false, hitVertical = false;

                if (bullet.getX() + bullet.getSpeedX() < wallBounds.x + wallBounds.width &&
                        bullet.getX() + bullet.getSpeedX() + bullet.getSize() > wallBounds.x) {
                    hitVertical = true;
                }
                if (bullet.getY() + bullet.getSpeedY() < wallBounds.y + wallBounds.height &&
                        bullet.getY() + bullet.getSpeedY() + bullet.getSize() > wallBounds.y) {
                    hitHorizontal = true;
                }

                if (hitVertical) bullet.reflectHorizontal();
                if (hitHorizontal) bullet.reflectVertical();

                return;
            }
        }

        if (playerTank != null && !playerTank.isDead() && bulletBounds.intersects(playerTank.getBounds())) {
            playerTank.setDead(true);
            bullet.destroy();
            triggerGameOver();
            return;
        }

        for (RobotTank robot : robots) {
            if (!robot.isDead() && bulletBounds.intersects(robot.getBounds())) {
                robot.setDead(true);
                bullet.destroy();
                return;
            }
        }
    }

    private void triggerGameOver() {
        gameOver = true;
        if (robotSpawnTimer != null) robotSpawnTimer.stop();
        if (gameTimer != null) gameTimer.stop();

        SwingUtilities.invokeLater(() -> {
            closeGameWindow();
            new DeathScreen();
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
