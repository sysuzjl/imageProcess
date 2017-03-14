import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color; 
import java.lang.Math;
import java.util.Random;
import java.util.*;

public class writeimage {
    writeimage() {
    	//获取图片宽度，高度
        width = 64;  
        height = 64;
        

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        
        //初始化图片RGB矩阵
        init();
        BufferedImage imgOut = converToRGBformat();
        writeImg(imgOut, "test_5");

    }

    public void init() {
        //初始化矩阵
    	for (int i = 0; i < 32; i++) {  
            for (int j = 0; j < 32; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 255;  
                sourceRGB[i][j][2] = 255;  
                sourceRGB[i][j][3] = 255;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }

        for (int i = 32; i < 64; i++) {  
            for (int j = 0; j < 32; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 255;  
                sourceRGB[i][j][2] = 255; 
                sourceRGB[i][j][3] = 255;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }

        for (int i = 0; i < 32; i++) {  
            for (int j = 32; j < 64; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 255;  
                sourceRGB[i][j][2] = 255;  
                sourceRGB[i][j][3] = 255;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }
        for (int i = 32; i < 64; i++) {  
            for (int j = 32; j < 64; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 255;  
                sourceRGB[i][j][2] = 255;  
                sourceRGB[i][j][3] = 255;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }
    }
    
    

    

    //绘制图像
    void writeImg(BufferedImage bi, String name) {
        File output= new File("../image/" + name + ".png");
        try {
            ImageIO.write(bi, "png", output);
        } catch(Exception e) {
            return;
        }

    }

    BufferedImage converToRGBformat() {
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //存储直方图均衡化后的RGB值
        int[] rgbArray = new int[width * height];
        int cnt = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {                
                rgbArray[cnt++] = (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)sourceRGB[i][j][1] << 16) & 0x00FF0000)
                | (((int)sourceRGB[i][j][2] << 8) & 0x0000FF00)
                | ((int)sourceRGB[i][j][3] & 0x000000FF);
            }
        }

        // 把图像设置为新的RGB值
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }







    //原图宽、高
    private int width;
	private int height;
	//原图rgb矩阵
	private double[][][] sourceRGB;
    private double[][][] hsimodel;
    private BufferedImage img;
    private double[][] histogram;

    public static void main(String[] args) {
        writeimage a = new writeimage();
    }
}