package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private Tank tank;
    private List<Bullet> bullets; // 存储子弹的列表

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.LIGHT_GRAY);

        gameMap = new GameMap();
        tank = new Tank(100, 100, "Tank.png");

        // 设置更合理的圆形碰撞体尺寸
        tank.setCollisionSize(40);
        bullets = new ArrayList<>();

        this.setFocusable(true);
        this.addKeyListener(this);

        // 启动游戏循环
        gameLoop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.draw(g);  // 先画地图

        if (tank != null && !tank.isDead()) {
            tank.draw(g, this);
        }
        // 绘制所有子弹
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Ellipse2D futureCircle = null;
        if (tank == null || tank.isDead()) return;
        switch (key) {
            case KeyEvent.VK_W -> {
                futureCircle = tank.getFutureCollisionCircle(0, -5);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveUp();
                    tank.setRotationAngle(0);
                }
            }
            case KeyEvent.VK_S -> {
                futureCircle = tank.getFutureCollisionCircle(0, 5);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveDown(getHeight());
                    tank.setRotationAngle(180);
                }
            }
            case KeyEvent.VK_A -> {
                futureCircle = tank.getFutureCollisionCircle(-5, 0);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveLeft();
                    tank.setRotationAngle(270);
                }
            }
            case KeyEvent.VK_D -> {
                futureCircle = tank.getFutureCollisionCircle(5, 0);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveRight(getWidth());
                    tank.setRotationAngle(90);
                }
            }
            case KeyEvent.VK_SPACE -> {
                // 获取坦克的中心位置
                int tankCenterX = tank.getX() + Tank.DISPLAY_SIZE / 2;
                int tankCenterY = tank.getY() + Tank.DISPLAY_SIZE / 2;

                // 获取坦克的旋转角度并将其转换为弧度
                double angleRad = Math.toRadians(tank.getRotationAngle() - 90); // 调整角度，使得角度0为向上

                // 计算子弹的发射偏移量，发射点在坦克的炮管方向
                int offset = Tank.DISPLAY_SIZE / 2 ; // 这可以根据需要调整

                // 计算子弹发射的位置
                int bulletX = (int) (tankCenterX + offset * Math.cos(angleRad)) - Bullet.DEFAULT_SIZE / 2;
                int bulletY = (int) (tankCenterY + offset * Math.sin(angleRad)) - Bullet.DEFAULT_SIZE / 2;

                // 创建并添加子弹对象
                Bullet bullet = new Bullet(bulletX, bulletY, tank.getRotationAngle());
                bullets.add(bullet);
            }
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    // 每次绘制都会更新子弹位置
    private void updateBulletPositions() {
        List<Bullet> toRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            if (bullet.isExpired()) {
                toRemove.add(bullet); // 标记超时子弹
                continue;
            }

            bullet.move();
            checkBulletCollision(bullet);
        }

        bullets.removeAll(toRemove); // 移除超时子弹
    }

    private void checkBulletCollision(Bullet bullet) {
        Rectangle bulletBounds = bullet.getBounds();

        for (Wall wall : gameMap.getWalls()) {
            Rectangle wallBounds = wall.getBounds();

            // 检查子弹是否与墙壁碰撞
            if (bulletBounds.intersects(wallBounds)) {
                // 回退子弹位置
                int prevX = bullet.getX();
                int prevY = bullet.getY();
                bullet.revertMovement(prevX, prevY);  // 恢复到碰撞前的位置

                // 重新计算反弹方向
                boolean collidesIfXMoves = false;
                boolean collidesIfYMoves = false;

                // 试探水平移动
                Rectangle testXPos = new Rectangle((int)(bullet.getX() + bullet.speed()), (int)bullet.getY(), bullet.getSize(), bullet.getSize());
                Rectangle testXNeg = new Rectangle((int)(bullet.getX() - bullet.speed()), (int)bullet.getY(), bullet.getSize(), bullet.getSize());
                if (testXPos.intersects(wallBounds) || testXNeg.intersects(wallBounds)) {
                    collidesIfXMoves = true;
                }

                // 试探垂直移动
                Rectangle testYPos = new Rectangle((int)bullet.getX(), (int)(bullet.getY() + bullet.speed()), bullet.getSize(), bullet.getSize());
                Rectangle testYNeg = new Rectangle((int)bullet.getX(), (int)(bullet.getY() - bullet.speed()), bullet.getSize(), bullet.getSize());
                if (testYPos.intersects(wallBounds) || testYNeg.intersects(wallBounds)) {
                    collidesIfYMoves = true;
                }

                // 根据碰撞方向选择反弹
                if (collidesIfXMoves && !collidesIfYMoves) {
                    bullet.reflect(true);  // 水平反弹
                } else if (!collidesIfXMoves && collidesIfYMoves) {
                    bullet.reflect(false); // 垂直反弹
                } else {
                    // 两个方向都重叠（角落等），优先垂直反弹
                    bullet.reflect(false);
                }

                // 反弹后立即继续移动
                bullet.move();
                return;  // 找到碰撞就跳出，避免多次碰撞处理
            }
        }

        // 与坦克碰撞检测
        if (bullet.getBounds().intersects(tank.getBounds())) {
            tank.setDead(true);
            closeGameWindow();
            new DeathScreen();
            tank = null;
        }
    }

    private void closeGameWindow() {
        // 获取父窗口并关闭
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();  // 关闭当前窗口
        }
    }

    public void gameLoop() {
        // 这是一个独立的游戏循环来定期更新游戏状态
        Timer timer = new Timer(16, e -> { // 每隔16毫秒（大约60帧每秒）更新一次
            updateBulletPositions();
            repaint();
        });
        timer.start();
    }
}
