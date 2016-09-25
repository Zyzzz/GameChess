package com.imudges.socket;



import com.imudges.frames.BoradFrame;
//import com.imudges.frames.BoradPanel;
import com.imudges.frames.BoradPanel;
import com.imudges.interfaces.Call;
import com.imudges.tool.Point;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
* 服务器程序，用于已服务器的方式接受与发送数据，
 * 接收传入的数据，  端口号， BoradPanel对象
 * 在以客户端形式启动的时候启动，其中的Get方法要以新建线程的方式来启动
*/
public class Server implements Call {
    private BoradPanel panel;  // Get方法中用于添加棋子的BoradPanel对象
    ServerSocket serverSocket; // 新建服务器对象
    private Socket socket = null;
    private int state_color ; // 用于存放已方所选棋子颜色

    public Server(int port) {
      //  this.panel = panel;
        this.state_color = state_color;
        try {
            serverSocket = new ServerSocket(port); //根据端口号创建对象
            socket = serverSocket.accept(); // 在没有客户端连接之前会一直阻塞，此处会在一个加载动画中来进行等待
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向输入流中发送数据
    public void Send(String s) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream()); //获取到输出流
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println(s);
        writer.flush(); // 刷新输出流
//        Writer writer = null;
//        try {
//            writer = new OutputStreamWriter(socket.getOutputStream());
//            writer.write(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    //关闭连接
    public void Close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Put方法，用与发送数据，供BoradPanel来调用
    @Override
    public void Put(String s) {
//        System.out.println("Service Put" + s);
        Send(s);
    }

    //Get方法，此方法会阻塞线程，所以在创建服务器对象之后，要通过线程的方式来使用此方法
    //此方法会一直执行，获取传进的数据，并使用BoradPanel引用来添加点到面板
    @Override
    public String Get() {
            //获取输入流
            BufferedReader reader = null; // 获取读入对象
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 获取输入流
                String str = null;
                while ((str = reader.readLine()) != null) {
                    return str;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return "";
    }
}
