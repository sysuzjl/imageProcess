import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color; 

public class ImageReaderRunner {
    ImageReaderRunner() {
    	BufferedImage bi = null;
        //读取图片
    	try {  
            bi = ImageIO.read(new File("../image/92.png"));;
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //获取图片宽度，高度
        width = bi.getWidth();  
        height = bi.getHeight();

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        //记录每个灰度值在该图上分布的个数
        histogram = new double[256];
        
        //初始化图片RGB矩阵
        init(bi);

        //计算概率分布值
        calcuProbability();

        //获得概率分布直方图
        getHistogram(0);

        //直方图均衡化
        BufferedImage histogramImage = equalize_hist(bi, 1);

        //再次直方图均衡化，同样初始化数组histogram数组，及新图的rgb矩阵
        init(histogramImage);
        //获得概率分布直方图
        calcuProbability();
        //计算概率分布值
        getHistogram(1);
        //直方图均衡化
        BufferedImage histogramImage2 = equalize_hist(histogramImage, 2);

        calcuProbability();
        getHistogram(2);

        init(bi);

        //3*3均值滤波
        double[][] filter = new double[3][3];
        filter = sewFilterAsOne(filter);
        filter2d(bi, filter, 1);

        //7*7均值滤波
        double[][] filter1 = new double[7][7];
        filter1 = sewFilterAsOne(filter1);
        filter2d(bi, filter1, 2);

        //11*11均值滤波
        double[][] filter2 = new double[11][11];
        filter2 = sewFilterAsOne(filter2);
        filter2d(bi, filter2, 3);

        //锐化空间滤波
        double[][] filter3 = {{1,1,1},{1,-8,1},{1,1,1}};
        filter2d(bi, filter3, 4);

        //high-boost 高提升滤波
        double[][] origin = {{0,0,0},{0,1,0},{0,0,0}};
        double[][] filter4 = new double[3][3];
        filter4 = sewFilterAsOne(filter4);

        //获得high-boost filter, g = origin + (origin - filter) * k;
        filter4 = GFilter(origin, filter4, 2);

        filter2d(bi, filter4, 5);

    }
    //获得 high-boost filter， 即 g = origin + (origin - filter) * k;
    public double[][] GFilter(double[][] a, double[][] b, double k) {
        int len = a.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                b[i][j] = a[i][j] + (a[i][j]-b[i][j])*k;
            }
        }
        return b;
    }

    public void init(BufferedImage bi) {
        //初始化矩阵
    	for (int i = 0; i < height; i++) {  
            for (int j = 0; j < width; j++) {  
                //获得像素值，并将其分别解析成A、R、G、B进行存储
                int pixel = bi.getRGB(j, i); // 下面4行代码将一个数字转换为RGB数字
                sourceRGB[i][j][0] = (pixel & 0xff000000) >> 24;    
                sourceRGB[i][j][1] = (pixel & 0xff0000) >> 16;  
                sourceRGB[i][j][2] = (pixel & 0xff00) >> 8;  
                sourceRGB[i][j][3] = (pixel & 0xff);  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }
    }

    //计算像素矩阵概率分布
    public void calcuProbability() {
        //先将数组histogram 置为0
        for (int i = 0; i < 256; i++) {
            histogram[i] = 0;
        }
        //计算灰度值分布
        for (int i = 0; i < height; i++) {
            for (int j = 0; j <  width; j++) {
                histogram[(int)(sourceRGB[i][j][3])]++;
            }
        }
    }
    //画直方图
    public void getHistogram(int cnt) {  
        int size = 280;
        // 画X、Y轴
        BufferedImage histogramImage = new BufferedImage(size,size, BufferedImage.TYPE_4BYTE_ABGR);  
        Graphics2D g2d = histogramImage.createGraphics();  
        g2d.setPaint(Color.BLACK);  
        g2d.fillRect(0, 0, size, size);  
        g2d.setPaint(Color.WHITE);  
        g2d.drawLine(5, 250, 265, 250);  
        g2d.drawLine(5, 250, 5, 5);  
          
        // 将最大高度标定成200  
        g2d.setPaint(Color.GREEN);  
        double max = findMaxValue();

        //即每个单元高度有rate值
        float rate = 200.0f/((float)max);  
        int offset = 2;
        //画出histogram数组在图上相应高度值
        for(int i=0; i<histogram.length; i++) {  
            int frequency = (int)(histogram[i] * rate);  
            g2d.drawLine(5 + offset + i, 250, 5 + offset + i, 250-frequency);  
        }  
          
        // X Axis Gray intensity  
        g2d.setPaint(Color.RED);  
        g2d.drawString("Gray histogram", 100, 270);

        // 将图片输出为png格式
        File output= new File("../image/92_histogram_" + cnt + ".png");
        try {
            ImageIO.write(histogramImage, "png", output);
        } catch(Exception e) {
            return;
        }  
    }
    //找出histogram数组中最大值
    private double findMaxValue() {  
        double max = -1;

        for(int i=0; i<histogram.length; i++) {  
            if(max <  histogram[i]) {  
                max = histogram[i];  
            }  
        }  
        return max;  
    }
    public BufferedImage equalize_hist(BufferedImage bi, int count) {
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //存储直方图均衡化后的RGB值
        int[] rgbArray = new int[width * height];
        //存储原先rgb值与概率分布产生的rgb值对应变化
        int[] tRGB = new int[256];
        int sum = height*width;
        double pSum = 0;

        //经过概率分布变化后，原先的rgb值i对应的变化
        for (int i = 0; i < 256; i++) {
            pSum += histogram[i]/sum;
            tRGB[i] = (int)(pSum*(256-1) + 0.5);
        }

        //进行新的rgb矩阵赋值
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int gray = tRGB[(int)(sourceRGB[i][j][3])];
                rgbArray[cnt++] = (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | ((gray << 16) & 0x00FF0000)
                | ((gray << 8) & 0x0000FF00)
                | (gray & 0x000000FF);
            }
        }

        // 把图像设置为新的RGB值
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        File output= new File("../image/92_hist_equal_" + count + ".png");
        try {
            ImageIO.write(buffImage, "png", output);
            return buffImage;
        } catch(Exception e) {
            return null;
        }

    }
    double[][] sewFilterAsOne(double[][] filter) {
        int len = filter.length;
        //设置平均滤波器
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                filter[i][j] = 1.0/(len*len);
            }
        }
        return filter;
    }
    //均值滤波器
    void filter2d(BufferedImage bi, double[][] filter, int count) {
        int len = filter.length;
        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        double g;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g = 0;
                //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                for (int s = -len/2; s <= len/2; s++) {
                    for (int t = -len/2; t <= len/2; t++) {
                        if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width) {
                            g += filter[s+len/2][t+len/2]*sourceRGB[i+s][j+t][3];
                        }
                    }
                }
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(g) << 16) & 0x00FF0000)
                | (((int)(g) << 8) & 0x0000FF00)
                | ((int)(g) & 0x000000FF);
            }
        }

        //绘制图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        File output= new File("../image/92_filter2d_" + len + "*" + len + "_" + count + ".png");
        try {
            ImageIO.write(buffImage, "png", output);
        } catch(Exception e) {
            return;
        }

    }
    //原图宽、高
    private int width;
	private int height;
	//原图rgb矩阵
	private double[][][] sourceRGB;
    private double[] histogram;
    private BufferedImage img;
}