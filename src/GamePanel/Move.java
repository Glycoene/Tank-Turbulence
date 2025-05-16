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

    // 处理玩家键盘移动
    public void handleKeyPress(int keyCode, int panelWidth, int panelHeight) {
        Ellipse2D futureCircle;

        switch (keyCode) {
            case KeyEvent.VK_W -> {
                futureCircle = tank.getFutureCollisionCircle(0, -5);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveUp();
                    tank.setTargetAngle(0);
                }
            }
            case KeyEvent.VK_S -> {
                futureCircle = tank.getFutureCollisionCircle(0, 5);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveDown(panelHeight);
                    tank.setTargetAngle(180);
                }
            }
            case KeyEvent.VK_A -> {
                futureCircle = tank.getFutureCollisionCircle(-5, 0);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveLeft();
                    tank.setTargetAngle(270);
                }
            }
            case KeyEvent.VK_D -> {
                futureCircle = tank.getFutureCollisionCircle(5, 0);
                if (!gameMap.isColliding(futureCircle)) {
                    tank.moveRight(panelWidth);
                    tank.setTargetAngle(90);
                }
            }
        }
    }

    // 通用移动方法，返回是否成功移动
    public boolean tryMove(int dx, int dy) {
        Ellipse2D futureCircle = tank.getFutureCollisionCircle(dx, dy);
        if (!gameMap.isColliding(futureCircle)) {
            tank.setPosition(tank.getX() + dx, tank.getY() + dy);
            return true;
        }
        return false;
    }
}
