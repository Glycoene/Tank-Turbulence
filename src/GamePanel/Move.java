package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class Move extends JPanel implements KeyListener {
    private Tank tank;
    private GameMap gameMap;

    public Move() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.GRAY);

        tank = new Tank(100, 100, "Tank.png");
        tank.setCollisionSize(40); // 更接近真实大小

        gameMap = new GameMap();

        this.setFocusable(true);
        this.addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameMap.draw(g);
        tank.draw(g, this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Ellipse2D future;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                future = tank.getFutureCollisionCircle(0, -5);
                if (!gameMap.isColliding(future)) {
                    tank.moveUp();
                    tank.setRotationAngle(0);
                }
                break;
            case KeyEvent.VK_S:
                future = tank.getFutureCollisionCircle(0, 5);
                if (!gameMap.isColliding(future)) {
                    tank.moveDown(getHeight());
                    tank.setRotationAngle(180);
                }
                break;
            case KeyEvent.VK_A:
                future = tank.getFutureCollisionCircle(-5, 0);
                if (!gameMap.isColliding(future)) {
                    tank.moveLeft();
                    tank.setRotationAngle(270);
                }
                break;
            case KeyEvent.VK_D:
                future = tank.getFutureCollisionCircle(5, 0);
                if (!gameMap.isColliding(future)) {
                    tank.moveRight(getWidth());
                    tank.setRotationAngle(90);
                }
                break;
        }

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
