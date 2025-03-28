package GamePanel;

import java.awt.*;

public class Wall {
    private int x, y;
    public static final int SIZE = 50;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, SIZE, SIZE);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }
}

