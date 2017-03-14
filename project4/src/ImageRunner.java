import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color; 
import java.lang.Math;
import java.util.Random;
import java.util.*;

public class ImageRunner {
    ImageRunner() {
    	BufferedImage bi = null;
        //读取图片
    	try {  
            bi = ImageIO.read(new File("../hw4_input/task_1.png"));;
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //获取图片宽度，高度
        width = bi.getWidth();  
        height = bi.getHeight();

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        
        //初始化图片RGB矩阵
        init(bi);

        double[][] filter = new double[3][3];
        filter = setMeanFilter(filter);
        BufferedImage imgOut = filter2d(bi, filter);
        writeImg(imgOut, "filter2d_1");


        double[][] filter1 = new double[9][9];
        filter1 = setMeanFilter(filter1);
        imgOut = filter2d(bi,filter1);
        writeImg(imgOut, "filter2d_2");

        imgOut = harmonicMeanFilter(bi, 3);
        writeImg(imgOut, "harmonic_mean_1");

        imgOut = harmonicMeanFilter(bi, 9);
        writeImg(imgOut, "harmonic_mean_2");

        imgOut = contraHarmonicMeanFilter(bi, 3, 1.5);
        writeImg(imgOut, "contra_harmonic_mean_1");

        imgOut = contraHarmonicMeanFilter(bi, 9, 1.5);
        writeImg(imgOut, "contra_harmonic_mean_2");

        //读取图片
        try {  
            bi = ImageIO.read(new File("../hw4_input/task_2.png"));;
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //获取图片宽度，高度
        width = bi.getWidth();  
        height = bi.getHeight();

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        
        //初始化图片RGB矩阵
        init(bi);

        imgOut = addGaussianNoise(bi, 0, 40);
        writeImg(imgOut, "gaussian_noise_1");

        bi = imgOut;
        init(imgOut);

        imgOut = filter2d(bi, filter);
        writeImg(imgOut, "arithmetic_mean_1");

        imgOut = geometricMeanFilter(bi, 3);
        writeImg(imgOut, "geometric_mean_1");

        imgOut = medianFilter(bi, 3);
        writeImg(imgOut, "median_mean_1");

        //初始化图片RGB矩阵

        imgOut = addSaltAndPepperNoise(bi, 0.2, 0);
        writeImg(imgOut, "salt_noise_1");

        bi = imgOut;
        init(imgOut);

        imgOut = minFilter(bi, 3);
        writeImg(imgOut, "min_filter_1");

        imgOut = harmonicMeanFilter(bi, 3);
        writeImg(imgOut, "harmonic_mean_3");

        imgOut = contraHarmonicMeanFilter(bi, 3, 1.5);
        writeImg(imgOut, "contra_harmonic_mean_3");

        imgOut = contraHarmonicMeanFilter(bi, 3, -1.5);
        writeImg(imgOut, "contra_harmonic_mean_4");


        //读取图片
        try {  
            bi = ImageIO.read(new File("../hw4_input/task_2.png"));
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //获取图片宽度，高度
        width = bi.getWidth();  
        height = bi.getHeight();

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        
        //初始化图片RGB矩阵
        init(bi);

        imgOut = addSaltAndPepperNoise(bi, 0.2, 0.2);
        writeImg(imgOut, "salt_pepper_noise_2");

        bi = imgOut;
        init(imgOut);

        imgOut = filter2d(bi, filter);
        writeImg(imgOut, "arithmetic_mean_2");

        imgOut = geometricMeanFilter(bi, 3);
        writeImg(imgOut, "geometric_mean_2");

        imgOut = harmonicMeanFilter(bi, 3);
        writeImg(imgOut, "harmonic_mean_4");

        imgOut = medianFilter(bi, 3);
        writeImg(imgOut, "median_mean_2");

         //读取图片
        try {  
            bi = ImageIO.read(new File("../hw4_input/task_3/92.png"));;
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //获取图片宽度，高度
        width = bi.getWidth();  
        height = bi.getHeight();

        //原图rgb矩阵
        sourceRGB = new double[height][width][4];
        histogram = new double[3][256];
        
        //初始化图片RGB矩阵
        init(bi);

        //计算概率分布值
        calcuProbability();

        //平方图均衡化
        equalize_hist(bi, 1);

        //先获得单一概率分布直方图再直方图均衡化
        equalize_hist_2(bi, 2);

        //转换到hsi模型
        converToHSIModel(bi);

        //计算hsi模型中强度概率分布值
        calcuIntensityProbability();

        //hsi模型上的强度直方图均衡化
        double[][][] newHSImodel = equalize_hist_3(bi, 3);

        //hsi转为rgb格式
        imgOut = converToRGBformat(newHSImodel);

        writeImg(imgOut, "hsi_equal_his_1");

        //hsi转为rgb格式
        imgOut = converToRGBformat(hsimodel);

        writeImg(imgOut, "hsi_equal_his_2");

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
    BufferedImage filter2d(BufferedImage bi, double[][] filter) {
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

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //几何平均滤波器
    BufferedImage geometricMeanFilter(BufferedImage bi, int len) {
        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        double g;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g = 1;
                //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                for (int s = -len/2; s <= len/2; s++) {
                    for (int t = -len/2; t <= len/2; t++) {
                        if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width) {
                            if (sourceRGB[i+s][j+t][3] != 0)
                                g *= sourceRGB[i+s][j+t][3];
                        }
                    }
                }
                g = Math.pow(g, 1.0/len/len);
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(g) << 16) & 0x00FF0000)
                | (((int)(g) << 8) & 0x0000FF00)
                | ((int)(g) & 0x000000FF);
            }
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //中值平均滤波器
    BufferedImage medianFilter(BufferedImage bi, int len) {
        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[] g = new double[len*len];
                int cnt1 = 0;
                //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                for (int s = -len/2; s <= len/2; s++) {
                    for (int t = -len/2; t <= len/2; t++) {
                        if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width) {
                            g[cnt1++] = sourceRGB[i+s][j+t][3];
                        }
                    }
                }
                for (int t = cnt1; t < len*len; t++) {
                    g[t] = 0;
                }
                Arrays.sort(g);
                double median = g[len*len/2];
                
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(median) << 16) & 0x00FF0000)
                | (((int)(median) << 8) & 0x0000FF00)
                | ((int)(median) & 0x000000FF);
            }
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //最小滤波器
    BufferedImage minFilter(BufferedImage bi, int len) {
        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double minkey = 255;
                //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                for (int s = -len/2; s <= len/2; s++) {
                    for (int t = -len/2; t <= len/2; t++) {
                        if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width && minkey > sourceRGB[i+s][j+t][3]) {
                            minkey = sourceRGB[i+s][j+t][3];
                        }
                    }
                }
                
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(minkey) << 16) & 0x00FF0000)
                | (((int)(minkey) << 8) & 0x0000FF00)
                | ((int)(minkey) & 0x000000FF);
            }
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //调和滤波器
    BufferedImage harmonicMeanFilter(BufferedImage bi, int len) {
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
                            if (sourceRGB[i+s][j+t][3] > 0.1)
                                g += 1.0/sourceRGB[i+s][j+t][3];
                            else
                                g += 255;
                        }
                    }
                }
                //bug在这,注意g是否为0
                if (g != 0)
                    g = (double)len*len/g;
                if (g > 255)
                    g = 255;
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(g) << 16) & 0x00FF0000)
                | (((int)(g) << 8) & 0x0000FF00)
                | ((int)(g) & 0x000000FF);
            }
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //反调和滤波器
    BufferedImage contraHarmonicMeanFilter(BufferedImage bi, int len, double Q) {
        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        double g, h;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                g = 0;
                h = 0;
                //对sourceRGB[i][j]进行滤波，获得滤波后的值g
                for (int s = -len/2; s <= len/2; s++) {
                    for (int t = -len/2; t <= len/2; t++) {
                        if (i+s >= 0 && i+s < height && j+t >= 0 && j+t < width) {
                                if (sourceRGB[i+s][j+t][3] != 0) {
                                    g += Math.pow(sourceRGB[i+s][j+t][3], Q);
                                    h += Math.pow(sourceRGB[i+s][j+t][3], Q+1);
                                }
                        }
                    }
                }

                if (g != 0)
                    g = h/g;
                if (g > 255)
                    g = 255;
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(g) << 16) & 0x00FF0000)
                | (((int)(g) << 8) & 0x0000FF00)
                | ((int)(g) & 0x000000FF);
            }
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //添加高斯噪声
    BufferedImage addGaussianNoise(BufferedImage bi, double mean, double standardVariance) {

        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        double g;
        Random random = new Random();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                

                g = sourceRGB[i][j][3] + random.nextGaussian() * standardVariance + mean;

                if (g < 0)
                    g = 0;
                if (g > 255)
                    g = 255;

                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(g) << 16) & 0x00FF0000)
                | (((int)(g) << 8) & 0x0000FF00)
                | ((int)(g) & 0x000000FF);
            }
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
    }

    //椒盐噪声
    BufferedImage addSaltAndPepperNoise(BufferedImage bi, double saltRate, double pepperRate) {

        //目标rgb数组
        int[] rgbArray =  new int[height*width];
        int cnt = 0;
        double g;
        Random random = new Random();

        int totalSize = height * width;
        int saltSize = (int)(totalSize * saltRate);
        int pepperSize = (int)(totalSize * pepperRate);
    
        //初始化矩阵
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //将g值赋值在新的rgb矩阵中
                rgbArray[cnt++] =  (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | (((int)(sourceRGB[i][j][3]) << 16) & 0x00FF0000)
                | (((int)(sourceRGB[i][j][3]) << 8) & 0x0000FF00)
                | ((int)(sourceRGB[i][j][3]) & 0x000000FF);
            }
        }

        //加盐噪声
        for (int i = 0; i < saltSize; i++) {
            int randomPlace = (int)(Math.random() * totalSize);
            int row = randomPlace / width;
            int col = randomPlace % width;
            rgbArray[randomPlace] =  (((int)(sourceRGB[row][col][0]) << 24) & 0xFF000000)
                | ((255 << 16) & 0x00FF0000)
                | ((255 << 8) & 0x0000FF00)
                | (255 & 0x000000FF);
        }

        //加椒噪声    
        for (int i = 0; i < pepperSize; i++) {
            int randomPlace = (int)(Math.random() * totalSize);
            int row = randomPlace / width;
            int col = randomPlace % width;
            rgbArray[randomPlace] =  (((int)(sourceRGB[row][col][0]) << 24) & 0xFF000000)
                | ((0 << 16) & 0x00FF0000)
                | ((0 << 8) & 0x0000FF00)
                | (0 & 0x000000FF);
        }

        //返回图像
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        return buffImage;
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

    // 根据图像的长获得2的整数次幂  
    public static int get2PowerEdge(int e) {  
        if (e == 1)  
            return 1;  
        int cur = 1;  
        while(true) {  
            if (e > cur && e <= 2 * cur)  
                return 2*cur;  
            else  
                cur *= 2;  
        }  
    } 

    //计算像素矩阵概率分布
    public void calcuProbability() {
        //先将数组histogram 置为0
        for (int i = 0; i < 256; i++) {
            histogram[0][i] = 0;
            histogram[1][i] = 0;
            histogram[2][i] = 0;
        }
        //计算灰度值分布
        for (int i = 0; i < height; i++) {
            for (int j = 0; j <  width; j++) {
                histogram[2][(int)(sourceRGB[i][j][3])]++;
                histogram[1][(int)(sourceRGB[i][j][2])]++;
                histogram[0][(int)(sourceRGB[i][j][1])]++;
            }
        }
    }
    //分别对R、G、B进行直方图均衡化
    public BufferedImage equalize_hist(BufferedImage bi, int count) {
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //存储直方图均衡化后的RGB值
        int[] rgbArray = new int[width * height];
        //存储原先rgb值与概率分布产生的rgb值对应变化
        int[][] tRGB = new int[3][256];
        int sum = height*width;
        double[] pSum = new double[]{0, 0, 0};

        //经过概率分布变化后，原先的rgb值i对应的变化
        for (int i = 0; i < 256; i++) {
            pSum[0] += histogram[0][i]/sum;
            tRGB[0][i] = (int)(pSum[0]*(256-1) + 0.5);
            pSum[1] += histogram[1][i]/sum;
            tRGB[1][i] = (int)(pSum[1]*(256-1) + 0.5);
            pSum[2] += histogram[2][i]/sum;
            tRGB[2][i] = (int)(pSum[2]*(256-1) + 0.5);
        }

        //进行新的rgb矩阵赋值
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int gray = tRGB[2][(int)(sourceRGB[i][j][3])];
                int gray1 = tRGB[1][(int)(sourceRGB[i][j][2])];
                 int gray2 = tRGB[0][(int)(sourceRGB[i][j][1])];
                rgbArray[cnt++] = (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | ((gray2 << 16) & 0x00FF0000)
                | ((gray1 << 8) & 0x0000FF00)
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

    //对R、G、B进行直方图分布后获得平均直方图，再对平均直方图均衡化
    public BufferedImage equalize_hist_2(BufferedImage bi, int count) {
        BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //存储直方图均衡化后的RGB值
        int[] rgbArray = new int[width * height];
        //存储原先rgb值与概率分布产生的rgb值对应变化
        int[] tRGB = new int[256];
        int sum = height*width;
        double pSum = 0;

        //综合3个直方图绘制总的直方图
        for (int i = 0; i < 256; i++) {
            histogram[0][i] += histogram[1][i] + histogram[2][i];
        }

        //经过概率分布变化后，原先的rgb值i对应的变化
        for (int i = 0; i < 256; i++) {
            pSum += histogram[0][i]/(3*sum);
            tRGB[i] = (int)(pSum*(256-1) + 0.5);
        }

        //进行新的rgb矩阵赋值
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int gray = tRGB[(int)(sourceRGB[i][j][3])];
                int gray1 = tRGB[(int)(sourceRGB[i][j][2])];
                 int gray2 = tRGB[(int)(sourceRGB[i][j][1])];
                rgbArray[cnt++] = (((int)(sourceRGB[i][j][0]) << 24) & 0xFF000000)
                | ((gray2 << 16) & 0x00FF0000)
                | ((gray1 << 8) & 0x0000FF00)
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

    //根据公式转变为HSI模型
    void converToHSIModel(BufferedImage bi) {
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

    //计算像素矩阵概率分布
    public void calcuIntensityProbability() {
        //先将数组histogram 置为0
        for (int i = 0; i < 256; i++) {
            histogram[0][i] = 0;
            histogram[1][i] = 0;
            histogram[2][i] = 0;
        }
        //计算灰度值分布
        for (int i = 0; i < height; i++) {
            for (int j = 0; j <  width; j++) {
                histogram[0][(int)(hsimodel[i][j][2])]++;
            }
        }
    }

    public double[][][] equalize_hist_3(BufferedImage bi, int count) {
        
        int[] tRGB = new int[256];
        int sum = height*width;
        double pSum = 0;
        double[][][] newHSImodel = new double[height][width][3];

        //经过概率分布变化后，原先的rgb值i对应的变化
        for (int i = 0; i < 256; i++) {
            pSum += histogram[0][i]*1.0/sum;
            tRGB[i] = (int)(pSum*(256-1) + 0.5);
            System.out.print(tRGB[i] + "  ");

        }
        System.out.print('\n');

        //进行新的rgb矩阵赋值
        int cnt = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //System.out.print(hsimodel[i][j][2] + "  ");
                newHSImodel[i][j][2] = tRGB[(int)(hsimodel[i][j][2])];
                newHSImodel[i][j][1] = hsimodel[i][j][1];
                newHSImodel[i][j][0] = hsimodel[i][j][0];
            }
        }
        //System.out.print('\n');
        return newHSImodel;

    }

    //从HSI模型再转变为RGB模型

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
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
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
}