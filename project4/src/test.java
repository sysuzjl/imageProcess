import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color; 
import java.lang.Math;
import java.util.Random;
import java.util.*;

public class test {
    test() {
    	//获取图片宽度，高度
        width = 64;  
        height = 64;
        

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        
        //初始化图片RGB矩阵
        init();

        converToHSIModel();

        double[][] filter = new double[16][16];
        filter = setMeanFilter(filter);
        double[][][] tmp = filter2d(filter, 2);
        BufferedImage imgOut = converToRGBformat(tmp);
        writeImg(imgOut, "test_1");

        tmp = filter2d(filter, 1);
        imgOut = converToRGBformat(tmp);
        writeImg(imgOut, "test_2");
            

    }

    public void init() {
        //初始化矩阵
    	for (int i = 0; i < 32; i++) {  
            for (int j = 0; j < 32; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 255;  
                sourceRGB[i][j][2] = 255;  
                sourceRGB[i][j][3] = 0;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }

        for (int i = 32; i < 64; i++) {  
            for (int j = 0; j < 32; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 0;  
                sourceRGB[i][j][2] = 255;  
                sourceRGB[i][j][3] = 255;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }

        for (int i = 0; i < 32; i++) {  
            for (int j = 32; j < 64; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 255;  
                sourceRGB[i][j][2] = 0;  
                sourceRGB[i][j][3] = 255;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }
        for (int i = 32; i < 64; i++) {  
            for (int j = 32; j < 64; j++) {  
                
                sourceRGB[i][j][0] = 0;    
                sourceRGB[i][j][1] = 0;  
                sourceRGB[i][j][2] = 255;  
                sourceRGB[i][j][3] = 0;  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }
    }
    
    //构造均值滤波器
    double[][] setMeanFilter(double[][] filter) {
        int len = filter.length;
        //设置平均滤波器
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                filter[i][j] = 1.0/(len*len);
            }
        }
        return filter;
    }
    

    //算术均值滤波器
    double[][][] filter2d(double[][] filter, int flag) {
        int len = filter.length;
        double[][][] newHSImodel = new double[height][width][3];
        double g;
        if (flag == 1) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    g = 0;
                    //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                    for (int s = -len/2; s < len/2; s++) {
                        for (int t = -len/2; t < len/2; t++) {
                            if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width) {
                                g += filter[s+len/2][t+len/2]*hsimodel[i+s][j+t][0];
                            }
                        }
                    }
                    newHSImodel[i][j][0] = g;
                    
                    newHSImodel[i][j][1] = hsimodel[i][j][1];
                    newHSImodel[i][j][2] = hsimodel[i][j][2];
                }
            }
            
        } else {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    g = 0;
                    //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                    for (int s = -len/2; s < len/2; s++) {
                        for (int t = -len/2; t < len/2; t++) {
                            if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width) {
                                g += filter[s+len/2][t+len/2]*hsimodel[i+s][j+t][1];
                            }
                        }
                    }
                
                    newHSImodel[i][j][1] = g;
                    newHSImodel[i][j][2] = hsimodel[i][j][2];
                    newHSImodel[i][j][0] = hsimodel[i][j][0];
                }
            }
            
        }

        return newHSImodel;
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

    void converToHSIModel() {
        hsimodel = new double [height][width][3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double minPix = sourceRGB[i][j][1];
                if (minPix > sourceRGB[i][j][2])
                    minPix = sourceRGB[i][j][2];
                if (minPix > sourceRGB[i][j][3])
                    minPix = sourceRGB[i][j][3];
                double R = sourceRGB[i][j][1];
                double G = sourceRGB[i][j][2];
                double B = sourceRGB[i][j][3];

                double denominator = Math.pow((R-G)*(R-G) + (R-B)*(G-B), 0.5);
                if (denominator < 1e-9)
                    denominator = 1e-9;
                double thieta = Math.acos(0.5*(R*2-G-B)/denominator);
                if (B > G)
                    hsimodel[i][j][0] = 2*Math.PI-thieta;
                else
                    hsimodel[i][j][0] = thieta;

                hsimodel[i][j][1] = 1 - 3/(sourceRGB[i][j][1] + sourceRGB[i][j][2] + sourceRGB[i][j][3])*minPix;
                hsimodel[i][j][2] = 1.0/3*(sourceRGB[i][j][1] + sourceRGB[i][j][2] + sourceRGB[i][j][3]);

            }
        }
    }

    BufferedImage converToRGBformat(double[][][] hsiValue) {
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //存储直方图均衡化后的RGB值
        int[] rgbArray = new int[width * height];
        int cnt = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double H = hsiValue[i][j][0], B, G, R;
                if (H >= Math.PI*2/3.0 && H < Math.PI*4/3.0) {
                    H -= Math.PI*2/3.0;
                    R = hsiValue[i][j][2] * (1 - hsiValue[i][j][1]);
                    G = hsiValue[i][j][2] * (1 + hsiValue[i][j][1]*Math.cos(H) / Math.cos(Math.PI/3.0-H));
                    B = 3*hsiValue[i][j][2] - R - G;
                }
                else if (H >= Math.PI*4/3.0 && H <= Math.PI*2) {
                    H -= Math.PI*4/3.0;
                    G = hsiValue[i][j][2] * (1 - hsiValue[i][j][1]);
                    B = hsiValue[i][j][2] * (1 + hsiValue[i][j][1]*Math.cos(H) / Math.cos(Math.PI/3.0-H));
                    R = 3*hsiValue[i][j][2] - G - B;
                } else {
                    B = hsiValue[i][j][2] * (1 - hsiValue[i][j][1]);
                    R = hsiValue[i][j][2] * (1 + hsiValue[i][j][1]*Math.cos(H) / Math.cos(Math.PI/3.0-H));
                    G = 3*hsiValue[i][j][2] - R - B;
                }

                
                rgbArray[cnt++] = (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)R << 16) & 0x00FF0000)
                | (((int)G << 8) & 0x0000FF00)
                | ((int)B & 0x000000FF);
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
        test a = new test();
    }
}