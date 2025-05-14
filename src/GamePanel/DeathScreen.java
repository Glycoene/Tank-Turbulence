package GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import windows.Screen;

public class DeathScreen extends JFrame {
    public DeathScreen() {
        setTitle("游戏结束");
        setSize(400, 200);
        setLocationRelativeTo(null);  // 居中显示

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("您已死亡", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.RED);

        panel.add(label, BorderLayout.CENTER);
        add(panel);

        // 设置关闭时处理的方法
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosed();  // 调用关闭后的处理方法
            }
        });

        setVisible(true);  // 显示死亡界面
    }

    private void onWindowClosed() {
        // 关闭死亡界面
        dispose();

        // 启动新界面或重新开始游戏
        new Screen().START();  // 假设 Screen 是你的主界面类
    }
}
