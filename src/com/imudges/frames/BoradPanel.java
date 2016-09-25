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
public class BoradPanel extends JPanel implements DiyViews,MouseListener {
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
        if (state_color == BoradFrame.STATE_BLACK) {
            waitFlag = true;
        } else {
            waitFlag = false;
        }
        if (state_start == BoradFrame.STATE_SERVICE) {  //如果是以服务器形式来启动
            System.out.println("以服务器形式来启动");
            server = new Server(port);     //创建SocketService
            server.Put("开始");
        } else {
            System.out.println("以客户端形式来启动");
            client = new Client(host, port);
            String s = client.Get();
            System.out.println(s);
        }
        if (!waitFlag) {
            getPoint();
            }
    }


    private  void getPoint(){
        new Thread() {
            @Override
            public void run() {
                Point point = null;
                if (state_start == BoradFrame.STATE_SERVICE) {
                    String s = server.Get();
                    System.out.println(s);
                    String[] data = s.split(","); //分割内容
                    int nextX = Integer.valueOf(data[0]);
                    int nextY = Integer.valueOf(data[1]);
                    if ((state_color == BoradFrame.STATE_WHITE)) {
                        point = new Point(nextX, nextY, Point.STATE_BLACK);
                    } else {
                        point = new Point(nextX, nextY, Point.STATE_WHITE);
                    }
                    addPoint(point);
                } else {
                    String s = client.Get();
                    System.out.println(s);
                    String[] data = s.split(","); //分割内容
                    int nextX = Integer.valueOf(data[0]);
                    int nextY = Integer.valueOf(data[1]);
                    if ((state_color == BoradFrame.STATE_WHITE)) {
                        point = new Point(nextX, nextY, Point.STATE_BLACK);
                    } else {
                        point = new Point(nextX, nextY, Point.STATE_WHITE);
                    }
                    addPoint(point);
                }
            }
        }.start();
    }

    //接收棋子添加到棋盘 // 需要判断所选颜色
    public void addPoint(Point point) {
        System.out.println("添加棋子" + point.getX() + "----" + point.getY());
        if (flag == 1) {
            this.remove(beginpanel);
            updateUI();
            flag++;
        }
        Calculate calculate = null;
        if (datas[point.getX()][19 - point.getY()] == 0) {
            points.add(point);  // 添加棋子到list中
            if (waitFlag) {
                if (state_color == BoradFrame.STATE_WHITE)
                    datas[point.getX()][19 - point.getY()] = -1;
                else
                    datas[point.getX()][19 - point.getY()] = 1;
            } else {
                if (state_color == BoradFrame.STATE_WHITE)
                    datas[point.getX()][19 - point.getY()] = 1;
                else
                    datas[point.getX()][19 - point.getY()] = -1;
            }
            System.out.println("X:" + point.getX() + "Y:" + (19 - point.getY()));
            int whoWin=-1;
            boolean winFlag = false;
            if (waitFlag) {
                if (state_color == BoradFrame.STATE_WHITE) {
                    calculate = new Calculate(datas, point.getX(), 19 - point.getY(), BoradFrame.STATE_WHITE);
                    winFlag = calculate.checkWin();
                    if(winFlag){
                        whoWin = 1;
                    }
                }
                else {
                    calculate = new Calculate(datas, point.getX(), 19 - point.getY(), BoradFrame.STATE_BLACK);
                    winFlag = calculate.checkWin();
                    if(winFlag){
                        whoWin = 2;
                    }
                }
            } else {
                if (state_color == BoradFrame.STATE_WHITE) {
                    calculate = new Calculate(datas, point.getX(), 19 - point.getY(), BoradFrame.STATE_BLACK);
                    winFlag = calculate.checkWin();
                    if(winFlag){
                        whoWin = 2;
                    }
                }
                else {
                    calculate = new Calculate(datas, point.getX(), 19 - point.getY(), BoradFrame.STATE_WHITE);
                    winFlag = calculate.checkWin();
                    if(winFlag){
                        whoWin = 1;
                    }
                }
            }

            updateUI();
           // System.out.println(winFlag);
            if(waitFlag) {
                if(state_start == BoradFrame.STATE_SERVICE){
                    server.Put(""+point.getX()+","+point.getY());
                }else {
                    client.Put(""+point.getX()+","+point.getY());
                }
                waitFlag = false;
            }else{
                waitFlag = true;
            }
            if (winFlag) {
                if(whoWin==1) {
                    JOptionPane.showMessageDialog(this, "白球赢了");
                }else {
                        JOptionPane.showMessageDialog(this, "黑棋赢了");
                }
            }
        }

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
        addMouseListener(this);
        btnBegin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == btnBegin) {
                    beginpanel.setVisible(false);
                    //updateUI();
                    //  JustAdd();
                }
            }
        });
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
        int i = 1;
        for (Point point : points) {
            ImageIcon icon = null;
            if (i==points.size()) {
                if (point.getState() == com.imudges.tool.Point.STATE_WHITE) {
                    icon = new ImageIcon("drawable/lastwhite.png"); // 指定白旗资源
                } else {
                    icon = new ImageIcon("drawable/lastblack.png"); // 指定黑棋资源
                }
            } else{
                if (point.getState() == com.imudges.tool.Point.STATE_WHITE) {
                    icon = new ImageIcon("drawable/white.png"); // 指定白旗资源
                } else {
                    icon = new ImageIcon("drawable/black.png"); // 指定黑棋资源
                }
                i++;
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
        System.out.println("hahaha");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("hahaha22");
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
                getPoint();

            }else {
                System.out.println("不是你下棋的时候");
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


}
