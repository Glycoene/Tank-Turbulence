package GamePanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("单人模式");
        GamePanel panel = new GamePanel(); // 创建 GamePanel 实例

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // 居中
        frame.setVisible(true);
    }
}
