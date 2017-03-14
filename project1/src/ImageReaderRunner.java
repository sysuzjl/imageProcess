import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D; 

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
        //初始化图片RGB矩阵
        init(bi);

        //缩放图片
        downScale(450, 300, bi);
        downScale(300, 200, bi);
        downScale(500, 200, bi);
        downScale(192, 128, bi);
        downScale(96, 64, bi);
        downScale(48, 32, bi);
        downScale(24, 16, bi);
        downScale(12, 8, bi);

        //量化
        quantization(256, bi);
        quantization(128, bi);
        quantization(32, bi);
        quantization(8, bi);
        quantization(4, bi);
        quantization(2, bi);
    }
    public void init(BufferedImage bi) {
        //初始化矩阵
    	sourceRGB = new double[width][height][4];
    	for (int i = 0; i < width; i++) {  
            for (int j = 0; j < height; j++) {  
                //获得像素值，并将其分别解析成A、R、G、B进行存储
                int pixel = bi.getRGB(i, j); // 下面4行代码将一个数字转换为RGB数字
                sourceRGB[i][j][0] = (pixel & 0xff000000) >> 24;    
                sourceRGB[i][j][1] = (pixel & 0xff0000) >> 16;  
                sourceRGB[i][j][2] = (pixel & 0xff00) >> 8;  
                sourceRGB[i][j][3] = (pixel & 0xff);  
                //System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");  
            }  
        }
    }
    //量化
    public void quantization(int level, BufferedImage img) {
    	BufferedImage buffImage =  new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        
        // 将图像画到buffImage中
        Graphics2D bGr = buffImage.createGraphics();
        bGr.drawImage(img, 0, 0, width, height, null);
        bGr.dispose();

    	//设置每个区间段
    	int gap = (int) (256 / (level - 1));

    	//存储量化后的RGB值
    	int[] rgbArray = new int[width * height];
    	int cnt = 0;

        //获得一维数组的RGB值
    	for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grayARGB = img.getRGB(j, i);
                rgbArray[cnt++] = setRgbByLevel(grayARGB, gap);
            }
        }

        // 把图像设置为新的RGB值
        buffImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        File output= new File("../image/92_level_" + Integer.toString(level) + ".png");
        try {
        	ImageIO.write(buffImage, "png", output);
    	} catch(Exception e) {
    	    return;
    	}

    }

    //进行灰度处理,找出区间gap最接近的值
    public int setRgbByLevel(int gray, int gap) {
    	int alpha = (gray & 0xff000000) >> 24;
    	int temp = (gray & 0xff0000) >> 16;

    	//算出lower_bound
        int rgb = temp / gap * gap;

        //进行四舍五入
        if (temp - rgb >= gap/2)
        	rgb += gap;

        
        if (rgb >= 256)
        	rgb = 255;

        //转化成RGB值
        int grayARGB = ((alpha << 24) & 0xFF000000)
                | ((rgb << 16) & 0x00FF0000)
                | ((rgb << 8) & 0x0000FF00)
                | (rgb & 0x000000FF);

        return grayARGB;
    }

    //缩放图片
    public void downScale(int w, int h, BufferedImage bi) {
    	BufferedImage buffImage =  new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
        
        // 将图像画到buffImage中
        Graphics2D bGr = buffImage.createGraphics();
        bGr.drawImage(bi, 0, 0, w, h, null);
        bGr.dispose();

    	//与原图相比，长宽比例
    	double colRatio = (double)(width) / (double)(w);
    	double rowRatio = (double)(height) / (double)(h);
    	
    	int[][][] downScaleRGB = new int[w][h][4];

    	//双线性插值法
    	for (int col = 0; col < w; col++) {
    		//横坐标映射，并截取小数部分
    		int k = (int)(col * colRatio);
    		double u = (double)(col) * colRatio - k;
    		for (int row = 0; row < h; row++) {
                //纵坐标映射，并截取小数部分
    			int j = (int)(row * rowRatio);
    			double c = (double)(row) * rowRatio - j;

        		for (int i = 0; i < 4; i++) {
        			//这里映射时的坐标，可能刚好超过最大高度及宽度，超过时像素点就设为最高的像素点及最宽的像素点
    				int Q11 = (int)(sourceRGB[k][j][i]*(1.0 - c) + sourceRGB[k][j+1 >= height?height-1:j+1][i] *1.0* c);
    				int Q22 = (int)(sourceRGB[k+1 >=width?width-1:k+1][j][i] * (1.0 - c) + sourceRGB[k+1 >=width?width-1:k+1][j+1 >= height?height-1:j+1][i] *1.0* c);
    	
    				// Dxy 为通过两次插值得到的目的像素坐标上的灰度值。
    				downScaleRGB[col][row][i] = (int)(Q11 * (1.0 - u) + Q22 *1.0 * u);
				}
    		}
    	}

        //将RGB值重组成一个BYTE字节,并转化成一维数组
    	int[] rgbArray = downScaleRGBToInt(w, h, downScaleRGB);
    	// 把图像设置为新的RGB值
        buffImage.setRGB(0, 0, w, h, rgbArray, 0, w);
        // 将图片输出为png格式
        File output= new File("../image/92_" + Integer.toString(w) + "_" + Integer.toString(h) + ".png");
        try {
        	ImageIO.write(buffImage, "png", output);
    	} catch(Exception e) {
    	    return;
    	}

    }
    //将RGB值重组成一个BYTE字节
    public int[] downScaleRGBToInt(int w, int h, int[][][] data) {
    	int[] oneDPix = new int[w * h];

        for (int row = 0, cnt = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                oneDPix[cnt] = 
                          ((data[col][row][0] << 24) & 0xFF000000)
                        | ((data[col][row][1] << 16) & 0x00FF0000)
                        | ((data[col][row][2] << 8) & 0x0000FF00)
                        | ((data[col][row][3]) & 0x000000FF);

                cnt++;
            }
        }
        return oneDPix;

    }
    //原图宽、高
    private int width;
	private int height;
	//原图rgb矩阵
	private double[][][] sourceRGB;
}