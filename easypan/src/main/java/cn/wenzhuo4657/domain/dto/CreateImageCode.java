package cn.wenzhuo4657.domain.dto;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.IOException;
import  java.util.Random;


public class CreateImageCode {
    private  int width=160;//图的宽度
    private int height=40;//图的高度
    private  int codeCount=4;//验证码字符个数
    private  int lineCount=20;//验证码干扰线个数


    private String code=null;//验证码

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    private BufferedImage bufferedImage=null;//图片
    Random random=new Random();

    public CreateImageCode() {
    }

    public CreateImageCode(int width, int heighe) {
        this.width = width;
        this.height= heighe;
        creatImage();
    }

    public CreateImageCode(int width, int heighe, int codeCount) {
        this.width = width;
        this.height = heighe;
        this.codeCount = codeCount;
        creatImage();
    }

    public CreateImageCode(int width, int heighe, int codeCount, int lineCount) {
        this.width = width;
        this.height = heighe;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        creatImage();
    }

//    生成图片
    public  void creatImage(){
        int fontWidth=width/codeCount;//字体狂赌
        int fontHeight=height-1;//字体高度
        int codeY=height-8;

        //图像buffer
        bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g=bufferedImage.getGraphics();
        g.setColor(getRandColor(200,250));

        g.fillRect(0,0,width,height);
        Font font=new Font("Fixedsys",Font.BOLD,fontHeight);
        //设置干扰线
        for (int i=0;i<lineCount;i++){
            int xs=random.nextInt(width);
            int ys=random.nextInt(height);
            int xe=xs+random.nextInt(width);
            int ye=ys+random.nextInt(height);
            g.setColor(getRandColor(1,255));
            g.drawLine(xs,ys,xe,ye);
        }
//        添加噪点
//        float yawpRate=0.01f;
//        int area=(int) (yawpRate*width*height);
//        for (int i=0;i<area;i++){
//            int x=random.nextInt(width);
//            int y=random.nextInt(height);
//            bufferedImage.setRGB(x,y,random.nextInt(255));
//        }

        String str1=randomStr(codeCount);
        this.code=str1;
        for(int i=0;i<codeCount;i++){
            String strRand=str1.substring(i,i+1);
            g.setColor(getRandColor(1,255));
            g.drawString(strRand,i*fontWidth+3,codeY);
        }
    }


    private  String randomStr(int n){
        String str1="ABCDEFGHIJKMNOPQRSTUVWXYZabcdefghijlmnopqrstuvwxyz1234657890";
        String str2="";
        int len=str1.length()-1;
        double r;
        for(int i=0;i<n;i++){
            r=(Math.random())*len;
            str2=str2+str1.charAt((int)r);
        }
        return  str2;
    }

    private  Color getRandColor(int fc,int bc){
        if(fc>255)fc=255;
        if (bc>255) bc=255;
        int r=fc+random.nextInt(bc-fc);
        int g=fc+random.nextInt(bc-fc);
        int b=fc+random.nextInt(bc-fc);
        return  new Color(r,g,b);
    }

    public  String getCode(){
        return  code.toLowerCase();
    }


    public void write(ServletOutputStream outputStream) throws IOException {
        ImageIO.write(bufferedImage,"png",outputStream);
        outputStream.close();
    }
}
