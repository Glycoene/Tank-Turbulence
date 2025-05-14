package GamePanel;

import java.awt.*;
import java.awt.geom.Line2D;

public class Bullet {
    private int x, y;
    private final int speed = 5;
    private double rotation; // 角度，单位：度
    public static final int DEFAULT_SIZE = 10; // 静态常量，供外部访问

    private final long createTime; // 添加：记录创建时间

    public Bullet(int x, int y, double rotation) {
        this.x = x;
        this.y = y;
        this.rotation = normalizeAngle(rotation);
        this.createTime = System.currentTimeMillis(); // 初始化创建时间
    }

    // 子弹每次移动
    public void move() {
        x += (int) (speed * Math.cos(Math.toRadians(rotation - 90)));
        y += (int) (speed * Math.sin(Math.toRadians(rotation - 90)));
    }

    // 绘制红色圆形子弹
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = normalizeAngle(rotation);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // 碰撞反弹：水平或垂直反弹
    public void reflect(boolean vertical) {
        if (vertical) {
            // 水平反弹，角度反转（上下）
            rotation = 180 - rotation;
        } else {
            // 垂直反弹，角度反转（左右）
            rotation = -rotation;
        }
        rotation = normalizeAngle(rotation); // 确保角度在 0 - 360 之间

        // 反弹后，重新计算移动
        move();
    }

    private double normalizeAngle(double angle) {
        angle = angle % 360;
        return (angle < 0) ? angle + 360 : angle;
    }

    public int getSize() {
        return DEFAULT_SIZE;
    }

    public int speed() {
        return speed;
    }

    // ✅ 新增：判断是否超过5秒
    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > 5000; // 5秒后过期
    }

    // 用于回退子弹位置
    public void revertMovement(int prevX, int prevY) {
        this.x = prevX;
        this.y = prevY;
    }
}
