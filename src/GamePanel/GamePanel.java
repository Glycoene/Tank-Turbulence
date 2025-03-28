package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class GamePanel extends JPanel implements KeyListener {
    private GameMap gameMap;
    private Tank tank;

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.LIGHT_GRAY);

        gameMap = new GameMap();
        tank = new Tank(100, 100, "Tank.png");

        // 设置更合理的圆形碰撞体尺寸
        tank.setCollisionSize(40);

        this.setFocusable(true);
        this.addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.draw(g);  // 先画地图
        tank.draw(g, this); // 再画坦克
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Ellipse2D futureCircle = null;

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
        }

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
