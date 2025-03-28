package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class Tank {
    private int x, y;
    private final int speed = 5;
    private final Image image;
    public static final int DISPLAY_SIZE = 50;
    private int collisionSize = 30;
    private double rotationAngle = 0; // 单位：度，0 是向上

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

    public Ellipse2D getFutureCollisionCircle(int dx, int dy) {
        int centerX = x + DISPLAY_SIZE / 2 + dx;
        int centerY = y + DISPLAY_SIZE / 2 + dy;
        int radius = collisionSize / 2;
        return new Ellipse2D.Double(centerX - radius, centerY - radius, collisionSize, collisionSize);
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
}
