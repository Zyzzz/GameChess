package com.imudges.frames;




import com.imudges.frames.BoradFrame;
import com.imudges.interfaces.DiyViews;
import com.imudges.socket.Client;
import com.imudges.socket.Server;
import com.imudges.tool.Calculate;
import com.imudges.view_override.BlankPanel;
import com.imudges.view_override.ImageButton;
import com.imudges.tool.Point;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Created by null on 15-11-6
 * 根据Service或者Client的Get方法来添加棋子，故在创建Service或者Client对象的时候要传入BoradPanel对象
 * 发送棋子调用的是Service或者Client的Put方法
 */
public class BoradPanel extends JPanel implements DiyViews, ActionListener,MouseListener {
    private java.util.List<Point> points;  //用于存放棋盘中存在的点
    private Server server = null; // Service对象
    private Client client = null; // Client对象
    Calculate calculate;
    private int state_start; //保存启动方式
    private int state_color; // 保存所用的棋子颜色
    private BlankPanel beginpanel; // 开始的提示面板
    private JLabel label_messagge; // 开始的提示信息
    private JButton btnBegin;
    private int flag = 1;
    private boolean waitFlag;

    private int[][] datas = new int[20][20];
    //传入参数， ip（供客户端使用）  端口号（均使用） 启动方式（启动客户端获取服务器端）  棋子颜色（白子或黑子）
    public BoradPanel(String host, int port, int state_start, int state_color) {
        this.state_start = state_start;
        this.state_color = state_color;
        points = new ArrayList<>();
        initViews();
        setViews();
        addViews();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // if()

        if (state_start == BoradFrame.STATE_SERVICE) {  //如果是以服务器形式来启动
            System.out.println("以服务器形式来启动");
            //calculate = new Calculate(state_color);
            server = new Server(port, BoradPanel.this, state_color);    //创建SocketService
            new Thread() {
                @Override
                public void run() {
                    String s = server.Get();// 在客户端连接之前会阻塞，且要持续运行来接受数据
                    while (true) {
                        if (s != "1") {
                            s = server.Get();
                        } else {
                            server.Put("1");
                            break;
                        }
                    }
                }
            }.start();
        } else {
            System.out.println("以客户端形式来启动");
            //calculate = new Calculate(state_color);
            client = new Client(host, port, this, state_color);
            new Thread() {
                @Override
                public void run() {
                    client.Put("1");
                    String s = client.Get();
                    //String s = server.Get();// 在客户端连接之前会阻塞，且要持续运行来接受数据
                    while (true) {
                        if (s != "1") {
                            s = server.Get();
                        } else {
                            break;
                        }
                    }
                }
            }.start();
        }

    }


    //接收棋子添加到棋盘 // 需要判断所选颜色
    public void addPoint(Point point) {
        System.out.println("添加棋子" + point.getX() + "----" + point.getY());
        if (flag == 1) {
            this.remove(beginpanel);
            updateUI();
            flag++;
        }
        if (datas[point.getX()][19 - point.getY()] == 0) {
            points.add(point);  // 添加棋子到list中
            if (waitFlag) {
                if (state_color == BoradFrame.STATE_WHITE)
                    datas[point.getX()][19 - point.getY()] = 1;
                else
                    datas[point.getX()][19 - point.getY()] = -1;
            } else {
                if (state_color == BoradFrame.STATE_WHITE)
                    datas[point.getX()][19 - point.getY()] = -1;
                else
                    datas[point.getX()][19 - point.getY()] = 1;
            }
            System.out.println("X:" + point.getX() + "Y:" + (19 - point.getY()));
            Calculate calculate = new Calculate(datas, point.getX(), 19 - point.getY(), state_color);
            boolean winFlag = calculate.checkWin();
            updateUI();
            System.out.println(winFlag);
            if (winFlag) {
                if (state_color == BoradFrame.STATE_BLACK)
                    JOptionPane.showMessageDialog(this, "白棋赢了");
                else
                    JOptionPane.showMessageDialog(this, "黑棋赢了");
            }
        }
//        if (calculate.JudegeWin(point)) {
//            calculate.addPoint(point);
//            points.add(point);  // 添加棋子到list中
//            updateUI();
//
//            Point result = calculate.getNext();
//
//            result.setState(state_color);
//            calculate.addPoint(result);
//            points.add(result);  // 添加棋子到list中
//            updateUI();
//            System.out.println("发送" + result.getX() + "---" + result.getY());
//            if (server != null) {
//                server.Put(result.getX() + "," + result.getY());
//            } else {
//                client.Put(result.getX() + "," + result.getY());
//            }


//        } else {
//            System.out.println("游戏结束");
//            System.exit(0);
//        }


//        if (result != null) {

//            if (state_color == BoradFrame.STATE_BLACK) {
//                result.setState(Point.STATE_BLACK);
//            } else {
//                result.setState(Point.STATE_WHITE);
//            }

//        } else {
//            System.out.println("游戏结束");
//            System.exit(0);

    }



    @Override
    public void initViews() {
        Font f = new Font("幼圆", Font.PLAIN, 15);
        Font f_label = new Font("幼圆", Font.PLAIN, 17);
        UIManager.put("Label.font", f);
        UIManager.put("Label.foreground", Color.black);
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
        //JPanel
        beginpanel = new BlankPanel(200);

        //JLabel
        label_messagge = new JLabel();

        //JButton
        btnBegin = new ImageButton("确定");

    }

    // 组件设置
    @Override
    public void setViews() {
        beginpanel.setBounds(250, 250, 240, 240);
        beginpanel.setLayout(null);

        label_messagge.setBounds(10, 100, 220, 40);
        //Jbutton
        btnBegin.setBounds(80, 180, 80, 40);
        btnBegin.addActionListener(this);
    }

    //添加组件
    @Override
    public void addViews() {
        if (state_color == BoradFrame.STATE_BLACK) {
            label_messagge.setText("轮到您先行!\n点击确定开始游戏");
        } else {
            label_messagge.setText("请等对方先行");
        }

        beginpanel.add(label_messagge);
        if (state_color == BoradFrame.STATE_BLACK) {
            beginpanel.add(btnBegin);
        }
        this.add(beginpanel);
        setLayout(null); // 设置布局为空
    }

    //面板绘制方法
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon board = new ImageIcon("drawable/board.png");
        g.drawImage(board.getImage(), 0, 0, 740, 740, null);
////        用于测试位置
//        ImageIcon icon1 = new ImageIcon("drawable/white.png");
//        g.drawImage(icon1.getImage(),18 ,  18, 36, 36, null);
//        g.drawImage(icon1.getImage(), 19 * 36 -5, 19 *36-5, 36, 36, null);
//        g.drawImage(icon1.getImage(), 19 * 33+26 , 19 * 33+23, 36, 36, null);
        //循环list中的所有点，根据点的颜色属性来指定不同的图片资源
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Point point : points) {
            ImageIcon icon = null;
            if (point.getState() == com.imudges.tool.Point.STATE_WHITE) {
                icon = new ImageIcon("drawable/white.png"); // 指定白旗资源
            } else {
                icon = new ImageIcon("drawable/black.png"); // 指定黑棋资源
            }
            //指定位置绘制指定图片
            //此处绘制已考虑边界，使用point在19*19棋盘中的坐标即可
            if(point.getY()==1 && point.getX()==1) {
                g.drawImage(icon.getImage(),37-14,710-28, 36, 36, null);
            }
            else if(point.getY()==1) {
                g.drawImage(icon.getImage(),point.getX()*37-16,710-28, 36, 36, null);
            }
            else if(point.getX()==1) {
                g.drawImage(icon.getImage(),37-14,740-point.getY()*37-18, 36, 36, null);
            }
            else {
                g.drawImage(icon.getImage(),point.getX()*37-16,740-point.getY()*37-18, 36, 36, null);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(waitFlag) {
            int x = e.getX();
            int y = e.getY();
            System.out.println(x + "," + y);
            Point point = null;
            int X = (x) / 36;
            int Y = (745 - y) / 36;
            System.out.println(X + ":" + Y);
            if (X > 0 && X < 20 && Y > 0 && Y < 20) {
                System.out.println("发送" + X + ":" + Y);
                if ((state_color == BoradFrame.STATE_WHITE)) {
                    point = new Point(X, Y, Point.STATE_WHITE);
                } else {
                    point = new Point(X, Y, Point.STATE_BLACK);
                }
                //points.add(point);
                addPoint(point);
            }else {

            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    //用于刷新 （自定义的不刷新全部的刷新方法，虽然刷新全部不会影响整体性能）
    //后期改进
    public void refresh(Graphics graphics) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBegin) {
            this.remove(beginpanel);
            updateUI();
            if(state_color == BoradFrame.STATE_BLACK){
                waitFlag = true;
            }else {
                waitFlag = false;
            }
          //  JustAdd();
        }
    }
}
