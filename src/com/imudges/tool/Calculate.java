package com.imudges.tool;

/**
 * Created by Administrator on 2016/9/20.
 */

import com.imudges.frames.BoradFrame;

import java.util.ArrayList;
import java.util.List;


public class Calculate {
    private int state_color;//1为黑棋，-1为白棋，0为旁观者
    private int[][] datas = new int[20][20];
    int x,y;
    public Calculate(int[][] datas, int x, int y, int state_color)
    {
        this.datas = datas;
        this.x = x;
        this.y = y;
        this.state_color = state_color;
    }

    private int checkCount(int xChange, int yChange, int color)
    {
        int count = 1;
        int tempX = xChange;
        int tempY = yChange;
        while (x + xChange >= 0 && x + xChange <= 19 && y + yChange >= 1&& y + yChange <= 19
                && color == datas[x + xChange][y + yChange])
        {//下一个需要判断的棋子在边界内并且与上一个棋子颜色相同则执行,这是判断x的右边和y的下边
            count++;
            if (xChange != 0)//xChange不为零，就判断x的右上和左下
                xChange++;
            if (yChange != 0) {
                if (yChange > 0)
                    yChange++;
                else {
                    yChange--;
                }
            }
        }
        xChange = tempX;
        yChange = tempY;
        while (x - xChange >= 0 && x - xChange <= 19 && y - yChange >= 0&& y - yChange <= 19
                && color == datas[x - xChange][y - yChange])
        {//继续判断x的左边和y的上边
            count++;
            if (xChange != 0)//xChange不为零，就判断x的左上和右下
                xChange++;
            if (yChange != 0) {
                if (yChange > 0)
                    yChange++;
                else {
                    yChange--;
                }
            }
        }
        return count;//返回当前判断的方向共有多少个同色棋子相连，共四个方向，横竖对角线四个
    }
    public boolean checkWin()//判断输赢
    {
        boolean flag = false;
        int count = 1;
        int color = datas[x][y];
        count = this.checkCount(1, 0, color);//判断横向
        if (count >= 5)
        {
            flag = true;
        }
        else
        {
            // 判断纵向
            count = this.checkCount(0, 1, color);
            if (count >= 5)
            {
                flag = true;
            }
            else
            {
                // 判断右上、左下
                count = this.checkCount(1, -1, color);
                if (count >= 5)
                {
                    flag = true;
                }
                else
                {
                    // 判断右下、左上
                    count = this.checkCount(1, 1, color);
                    if (count >= 5)
                    {
                        flag = true;
                    }
                }
            }
        }

        return flag;
    }
}
