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
    private double rotationAngle = 0; // 当前角度，单位度，0 是向上
    private double targetAngle = 0;   // 目标角度
    private boolean isDead = false;
    public static final int BARREL_LENGTH = 20; // 示例值，调整为炮管实际长度

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

    public void moveUp() {
        y -= speed;
    }

    public void moveDown(int panelHeight) {
        if (y + speed + DISPLAY_SIZE <= panelHeight) y += speed;
    }

    public void moveLeft() {
        x -= speed;
    }

    public void moveRight(int panelWidth) {
        if (x + speed + DISPLAY_SIZE <= panelWidth) x += speed;
    }

    // 直接设置当前角度（一般初始化或瞬间设定用）
    public void setRotationAngle(double angle) {
        this.rotationAngle = normalizeAngle(angle);
        this.targetAngle = this.rotationAngle;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    // 设置目标角度，坦克会逐渐转到此角度
    public void setTargetAngle(double angle) {
        this.targetAngle = normalizeAngle(angle);
    }

    public double getTargetAngle() {
        return targetAngle;
    }

    // 每帧调用，更新当前角度向目标角度靠近，实现平滑旋转
    public void updateRotation() {
        double rotationSpeed = 5; // 每帧最大旋转角度（度）
        double diff = targetAngle - rotationAngle;

        // 规范化差值到[-180, 180]
        diff = ((diff + 180) % 360) - 180;

        if (Math.abs(diff) <= rotationSpeed) {
            rotationAngle = targetAngle;
        } else {
            rotationAngle = normalizeAngle(rotationAngle + Math.signum(diff) * rotationSpeed);
        }
    }

    // 预测碰撞用的圆形区域
    public Ellipse2D getFutureCollisionCircle(int dx, int dy) {
        int centerX = x + DISPLAY_SIZE / 2 + dx;
        int centerY = y + DISPLAY_SIZE / 2 + dy;
        return new Ellipse2D.Double(centerX - collisionSize / 2.0, centerY - collisionSize / 2.0, collisionSize, collisionSize);
    }

    public void setCollisionSize(int size) {
        this.collisionSize = size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 规范角度到 [0, 360) 区间
    private double normalizeAngle(double angle) {
        angle %= 360;
        if (angle < 0) angle += 360;
        return angle;
    }
}
