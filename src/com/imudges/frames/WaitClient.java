package com.imudges.frames;

import com.imudges.interfaces.DiyViews;
import com.imudges.view_override.BlankPanel;
import com.imudges.view_override.ImageButton;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/9/19.
 */
public class WaitClient extends JFrame implements DiyViews, ActionListener
{
    private int Frame_Width = 550;
    private int Frame_Height = 550;
    private JLabel label_back, label_host, label_port, label_showMessahe;
    private int port;
    ImageIcon icon = new ImageIcon("drawable/loading.png");
    private JButton btnStartLocal, btnCancle;
    boolean isConnectionted = false;
    private int state_color; // 用于存放服务器端要选用的棋子颜色
    private BlankPanel blankPanel;
    int mx = 0, my = 0, jfx = 0, jfy = 0;

    public WaitClient(int port, int state_color) {
        this.port = port;
        this.state_color = state_color;
        new Thread() {
            @Override
            public void run() {
               BoradPanel panel = new BoradPanel(null, port, BoradFrame.STATE_SERVICE, state_color);
               BoradFrame frame = new BoradFrame(panel,BoradFrame.STATE_SERVICE, state_color);
                frame.setTitle("服务器端");
                System.out.println("此处在客户端链接之后关闭");
                isConnectionted = true; //用于中断label的不断刷新
                WaitClient.this.dispose(); // 有客户端连接之后关闭当前窗口
            }
        }.start();
        initViews();
        setViews();
        addViews();this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mx + jfx, e.getYOnScreen() - my + jfy);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                jfx = getX();
                jfy = getY();
            }
        });
    }

    @Override
    public void initViews() {
        Font f = new Font("幼圆", Font.PLAIN, 15);

        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("Button.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("List.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("Table.font", f);
        //绘制背景
        label_back = new JLabel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                icon.paintIcon(this, g, 0, 0);
            }
        };
        //JLable

        InetAddress ia=null;
        String localip = "";
        try {
            ia=ia.getLocalHost();

            String localname=ia.getHostName();
            localip=ia.getHostAddress();
            System.out.println("本机名称是："+ localname);
            System.out.println("本机的ip是 ："+localip);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int  port = MainFrames.port;
        label_host = new JLabel("    本机Ip为： " + localip);
        label_showMessahe = new JLabel("    等待客户端连接 .");
        label_port = new JLabel("    端口号为： " + port);
        //JButton
        btnStartLocal = new ImageButton("  开始本地双人游戏");
        btnCancle = new ImageButton("返回");
        //JPanel
        blankPanel = new BlankPanel(130);


    }

    @Override
    public void setViews() {

        //Jlabel
        label_showMessahe.setBounds(120, 250, 200, 50);
        label_host.setBounds(120, 130, 235, 45);
        label_port.setBounds(120, 180, 235, 45);
        //JButton
        btnStartLocal.setBounds(135, 310, 192, 35);
        btnStartLocal.addActionListener(this);

        btnCancle.setBounds(190, 360, 80, 30);
        btnCancle.addActionListener(this);

        //JPanel
        blankPanel.setBounds(110, 110, 200, 200);

        //JFrame
        setUndecorated(true); // 不装饰
        setSize(Frame_Width, Frame_Height); // 设置窗口大小
        AWTUtilities.setWindowOpaque(this, false);
        setLocationRelativeTo(null);  //设置窗口居中
        setVisible(true);
    }

    @Override
    public void addViews() {

        this.add(label_host);
        this.add(label_port);
        this.add(btnCancle);
        this.add(btnStartLocal);
        this.add(label_showMessahe);
//        this.add(blankPanel);
        this.add(label_back); // 最后添加在最后面
        String[] mesages = new String[]{
                "    等待客户端连接 .",
                "    等待客户端连接 ...",
                "    等待客户端连接 ....",
                "    等待客户端连接 ....",
                "    等待客户端连接 .....",
                "    等待客户端连接 .......",
        };
        new Thread() {
            @Override
            public void run() {
                int h = 0;
                while (isConnectionted == false) {
                    label_showMessahe.setText(mesages[h]);
                    repaint();
                    h++;
                    h = h % mesages.length;
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnStartLocal ) {
            PlayerPanel playerPanel = null;
            if (state_color == BoradFrame.STATE_BLACK) { // 如果服务器端选择黑子
                playerPanel = new PlayerPanel(MainFrames.host, port, BoradFrame.STATE_WHITE);
            } else {
                playerPanel = new PlayerPanel(MainFrames.host, port, BoradFrame.STATE_BLACK);
            }
            BoradFrame frame = new BoradFrame(playerPanel, BoradFrame.STATE_CLIENT, state_color);
            frame.setTitle("单机");
            this.dispose();
        }
        else{
            this.dispose();
            MainFrames frame = new MainFrames();
        }
    }

    public static void main(String[] args) {
        WaitClient client = new WaitClient(0, 0);

    }
}
