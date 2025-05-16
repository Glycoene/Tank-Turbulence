package GamePanel;

import java.awt.*;

public class Bullet {
    private int x, y;
    private double speedX, speedY;
    private final int size = DEFAULT_SIZE;
    private int lifeTime = 300;  // 存活帧数
    public static final int DEFAULT_SIZE = 10;
    private boolean expired = false;
    private int safeFrames = 10;  // 子弹发射后10帧内不检测自己坦克碰撞
    private Tank owner;

    public Bullet(int x, int y, double angleDegree, Tank owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;

        double angleRad = Math.toRadians(angleDegree - 90);
        double speed = 6.0;
        this.speedX = speed * Math.cos(angleRad);
        this.speedY = speed * Math.sin(angleRad);
    }

    public Tank getOwner() {
        return owner;
    }

    public void move() {
        x += speedX;
        y += speedY;

        if (safeFrames > 0) {
            safeFrames--;
        }

        lifeTime--;
        if (lifeTime <= 0) expired = true;
    }

    public void revertMovement(int prevX, int prevY) {
        this.x = prevX;
        this.y = prevY;
    }

    public void reflectHorizontal() {
        speedX = -speedX;
    }

    public void reflectVertical() {
        speedY = -speedY;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public boolean isExpired() {
        return isDestroyed();  // 合并判断
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

    public double getSpeedX() { return speedX; }
    public double getSpeedY() { return speedY; }

    public int speed() {
        // 返回速度的近似值用于判断
        return (int) Math.round(Math.sqrt(speedX * speedX + speedY * speedY));
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, size, size);
    }

    private boolean destroyed = false;

    public void destroy() {
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed || expired;
    }
    public int getSafeFrames() {
        return safeFrames;
    }

}
