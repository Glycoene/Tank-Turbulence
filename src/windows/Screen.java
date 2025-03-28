package windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import GamePanel.Main;  // 导入 GamePanel 包中的 Main 类

//窗口设置
public class Screen extends JFrame {
    //窗口规模
    int width = 800;
    int height  = 610;
    //图片
    Image select;
    int picture_y = 230;
    int zzWidth = 50;
    int zzHeight = 50;

    public Screen(){
        this.setContentPane(new GamePanel());
        select = new ImageIcon(getClass().getResource("images/selecttank.png")).getImage();
        if (select == null){
            System.out.println("图片加载失败，请检查");
        }
    }

    // 启动方法
    public void START(){
        setTitle("坦克大战");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        // 添加键盘监听
        this.addKeyListener(new ClickKey());
        Timer timer = new Timer(25, e -> repaint());
        timer.start();
    }

    // 游戏面板
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.darkGray);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.cyan);
            g.setFont(new Font("仿宋", Font.BOLD, 50));
            g.drawString("选择游戏模式", 220, 200);
            g.drawString("单人模式", 220, 280);
            g.drawString("双人模式", 220, 360);
            g.drawString("1v1模式", 220, 440);
            if (select != null) {
                g.drawImage(select, 160, picture_y, zzWidth, zzHeight, this);
            }
        }
    }

    // 键盘监听
    class ClickKey extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                // 如果已经在最上面，就跳转到最下面
                if (picture_y == 230) {
                    picture_y = 390;
                } else {
                    picture_y -= 80;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                // 如果已经在最下面，就跳转到最上面
                if (picture_y == 390) {
                    picture_y = 230;
                } else {
                    picture_y += 80;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // 处理ENTER键
                switch (picture_y) {
                    case 230 -> { // 单人模式
                        dispose(); // 关闭当前窗口
                        Main.main(null);  // 调用 GamePanel.Main 的 main 方法
                    }
                    case 310 -> {
                        // 双人模式 TODO：实现时调用相应启动方法
                        System.out.println("双人模式 - 暂未实现");
                    }
                    case 390 -> {
                        // 1v1模式 TODO：实现时调用相应启动方法
                        System.out.println("1v1模式 - 暂未实现");
                    }
                }
            }
            repaint();
        }
    }

    // 运行
    public static void main(String[] args) {
        Screen go = new Screen();
        go.START();
    }
}
