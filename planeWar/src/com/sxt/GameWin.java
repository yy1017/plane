package com.sxt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameWin extends JFrame {

    /** 定义双缓存图片 */
    Image offScreenImage = null;
    //游戏重绘次数
    int count = 1;
    //记录敌方飞机数量
    int ennemyCount = 0;
    //游戏状态 0未开始 1运行中 2暂停 3失败 4成功
    int state = 0;
    int MY_WIDTH = 600;
    int MY_HEIGHT = 600;
    Image bg = Toolkit.getDefaultToolkit().getImage("imgs/bj.jpg");
    //爆炸效果图
    Image explode = Toolkit.getDefaultToolkit().getImage("imgs/explode/e6.gif");
    //爆炸效果图坐标
    int explode_x = -300;
    int explode_y = 300;
    //定义大集合
    List<GameObject> objectList = new ArrayList<>();
    //被删除物体的集合
    List<GameObject> removeList = new ArrayList<>();
    //我方子弹集合
    List<ShellObj> shellObjList = new ArrayList<>();
    //敌方子弹集合
    List<BulletObj> bulletObjList = new ArrayList<>();
    //敌方战斗机集合
    List<EnemyObj> enemyObjList = new ArrayList<>();
    //爆炸对象集合
    List<ExplodeObj> explodeObjList = new ArrayList<>();
    //背景的实体类
    BgObj bgObj = new BgObj("imgs/bj.jpg",-200,-1700,1,this);
    //我方战斗机
    PlaneObj planeObj = new PlaneObj("imgs/qiu.png",290,550,0,this);
    //敌方boss
    BossObj bossObj = null;
    //窗口的启动方法
    public void launch(){
        //窗口是否可见
        setVisible(true);
        //窗口大小
        setSize(MY_WIDTH,MY_HEIGHT);
        //窗口的位置
        setLocationRelativeTo(null);
        //窗口的标题
        setTitle("飞机大战");

        //将游戏物体添加到大集合
        objectList.add(bgObj);
        objectList.add(planeObj);

        //为窗口添加开始鼠标事件
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //鼠标左键代号是1
                if (e.getButton() == 1){
                    state = 1;
                    repaint();
                }
            }
        });
        //为暂停添加一个键盘事件
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                //空格键被按下
               if (e.getKeyCode() == 32){
                    switch (state){
                        //运行改为暂停
                        case 1:
                            state = 2;
                            break;
                            //暂停改为运行
                        case 2:
                            state = 1;
                            break;
                    }
               }
            }
        });

        while (true){
            if (state == 1){
                createObj();
                repaint();
            }

            try {
                //线程休眠  1秒 = 1000毫秒
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        /** 创建和容器一样大小的Image图片 */
        if(offScreenImage ==null){
            offScreenImage=this.createImage(MY_WIDTH, MY_HEIGHT);
        }
        /** 获得该图片的画布*/
        Graphics gImage= offScreenImage.getGraphics();
        /** 填充整个画布*/
        gImage.fillRect(0, 0, MY_WIDTH, MY_HEIGHT);
        //游戏未开始
        if (state == 0){
            gImage.drawImage(bg,0,0,null);
            //改变画笔的颜色
            gImage.setColor(Color.white);
            //改变文字大小和样式
            gImage.setFont(new Font("仿宋",Font.BOLD,50));
            //添加文字
            gImage.drawString("点击开始游戏",150,300);
            gImage.setColor(Color.red);
            gImage.setFont(new Font("仿宋",Font.BOLD,25));
            gImage.drawString("拖动鼠标移动飞机",250,500);
        }
        if (state == 1){
            //爆炸对象添加到大集合
            objectList.addAll(explodeObjList);

            //绘制所有游戏物体
            for (GameObject object : objectList){
                object.paintSelf(gImage);
            }
            //绘制完后将要删除的元素进行处理
            objectList.removeAll(removeList);
        }
        //失败显示内容
        if (state == 3){
            //改变画笔的颜色
            gImage.setColor(Color.red);
            //改变文字大小和样式
            gImage.setFont(new Font("仿宋",Font.BOLD,50));
            //添加文字
            gImage.drawString("GAME OVER",180,300);
        }
        //通关显示内容
        if (state == 4){
            //改变画笔的颜色
            gImage.setColor(Color.yellow);
            //改变文字大小和样式
            gImage.setFont(new Font("黑体",Font.BOLD,45));
            //添加文字
            gImage.drawString("游戏通关",150,300);
        }
        gImage.drawImage(explode,explode_x,explode_y,null);
        /** 将缓冲区绘制好哦的图形整个绘制到容器的画布中 */
        g.drawImage(offScreenImage, 0, 0, null);
        //count自增
        count++;
        System.out.println("大集合的长度" + objectList.size());
    }

    //添加子弹或敌机
    public void createObj(){
        //控制我方子弹生成速度
        if (count % 20 == 0){
            shellObjList.add(new ShellObj("imgs/kk.png",planeObj.x + 3,planeObj.y - 10,10,this));
            objectList.add(shellObjList.get(shellObjList.size() - 1));
        }
        //敌方子弹生成速度
        if (count % 10 == 0 && bossObj != null){
            bulletObjList.add(new BulletObj("imgs/bulletYellow.png",bossObj.x + 45,bossObj.y + 76,10,this));
            objectList.add(bulletObjList.get(bulletObjList.size() - 1));
        }
        //生成敌方战斗机
        if (count % 15 == 0) {
            enemyObjList.add(new EnemyObj("imgs/enemy.png",this));
            objectList.add(enemyObjList.get(enemyObjList.size() - 1));
            ennemyCount++;
        }
        if (ennemyCount > 100){
            if (bossObj == null){
                bossObj = new BossObj("imgs/boss.png",250,30,5,this);
                objectList.add(bossObj);
            }
        }
    }
    public static void main(String[] args) {
        GameWin gameWin = new GameWin();
        gameWin.launch();
    }
}
