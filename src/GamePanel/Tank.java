package GamePanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class Tank {
    private int x, y;
    private final int speed = 5;
    private final Image image;
    public static final int DISPLAY_SIZE = 50;
    private int collisionSize = 30;
    private double rotationAngle = 0; // 单位：度，0 是向上
    private boolean isDead = false;

    public Tank(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.image = new ImageIcon(imagePath).getImage();
    }

    public void draw(Graphics g, JPanel panel) {
        Graphics2D g2d = (Graphics2D) g;
        int centerX = x + DISPLAY_SIZE / 2;
        int centerY = y + DISPLAY_SIZE / 2;

        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(rotationAngle), centerX, centerY);
        g2d.drawImage(image, x, y, DISPLAY_SIZE, DISPLAY_SIZE, panel);
        g2d.setTransform(old);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, DISPLAY_SIZE, DISPLAY_SIZE);
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    public boolean isDead() {
        return isDead;
    }

    public void moveUp() { y -= speed; }
    public void moveDown(int panelHeight) {
        if (y + speed + DISPLAY_SIZE <= panelHeight) y += speed;
    }
    public void moveLeft() { x -= speed; }
    public void moveRight(int panelWidth) {
        if (x + speed + DISPLAY_SIZE <= panelWidth) x += speed;
    }

    public void setRotationAngle(double angle) { this.rotationAngle = angle; }
    public double getRotationAngle() { return rotationAngle; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setCollisionSize(int size) { this.collisionSize = size; }

    public int getX() { return x; }
    public int getY() { return y; }

    // ✅ 添加用于预测碰撞的圆形区域
    public Ellipse2D getFutureCollisionCircle(int dx, int dy) {
        int centerX = x + DISPLAY_SIZE / 2 + dx;
        int centerY = y + DISPLAY_SIZE / 2 + dy;
        return new Ellipse2D.Double(centerX - collisionSize / 2.0, centerY - collisionSize / 2.0, collisionSize, collisionSize);
    }
}
