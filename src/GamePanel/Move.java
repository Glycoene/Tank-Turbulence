package GamePanel;

import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;

public class Move {
    private final Tank tank;
    private final GameMap gameMap;

    public Move(Tank tank, GameMap gameMap) {
        this.tank = tank;
        this.gameMap = gameMap;
    }

    public void handleKeyPress(int keyCode, int panelWidth, int panelHeight) {
        Ellipse2D futureCircle;

        switch (keyCode) {
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
                    tank.moveDown(panelHeight);
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
                    tank.moveRight(panelWidth);
                    tank.setRotationAngle(90);
                }
            }
        }
    }
}
