import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color; 
import java.lang.Math;

public class ImageDFTRunner {
    ImageDFTRunner() {
    	BufferedImage bi = null;
        Complex[][] f = null;
        //读取图片
    	try {  
            bi = ImageIO.read(new File("../image/92_96_64.png"));;
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //获取图片宽度，高度
        width = bi.getWidth();  
        height = bi.getHeight();

        //原图rgb矩阵
        sourceRGB = new double[width][height][4];
        
        //初始化图片RGB矩阵
        init(bi);

        //傅立叶变换后的频谱图
        f = dft2d(bi, 0, f);
        BufferedImage imgOut = showFourierImage(f);
        writeImg(imgOut, "dft2d_3");

        //傅立叶逆变换后的图
        Complex[][] g = dft2d(bi, 1, f);
        imgOut = showImage(g);
        writeImg(imgOut, "dft2d_4");

        //快速傅立叶变化
        /*f = fft2d(bi, 0, f);
        imgOut = showFourierImage(f);
        writeImg(imgOut, "fft2d_1");

        //快速傅立叶逆变化
        f = fft2d(bi, 1, f);
        imgOut = showImage(f);
        writeImg(imgOut, "fft2d_2");*/

        /*Complex[][] filter = constructFilter(0);
        Complex[][] H = filter2d_freq(bi, filter);
         imgOut = showImage(H);
        writeImg(imgOut, "filter2d_1");

        filter = constructFilter(1);
        H = filter2d_freq(bi, filter);
        imgOut = showImage(H);
        writeImg(imgOut, "filter2d_2");*/


        

    }

    public void init(BufferedImage bi) {
        //初始化矩阵
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

    //flag 为 0 时构造拉普拉斯滤波
    //flag 为 1 时构造拉普拉斯滤波
    public Complex[][] constructFilter(int flag) {
        int m = get2PowerEdge(width);
        int n = get2PowerEdge(height);
        
        Complex[][] filter = new Complex[m][n];
        if (flag == 0) {
            for (int x = 0; x < m; x++) {  
                for (int y = 0; y < n; y++) {  
                    if (x < 7 && y < 7) {  
                        if ((x+y)%2==0)  
                            filter[x][y] = new Complex(1/121d, 0); // double 后面赋值数字记得加d！！！！！！！  
                        else  
                            filter[x][y] = new Complex(-1/121d, 0);  
                    }  
                    else {  
                        filter[x][y] = new Complex(0, 0);  
                    }  
                }  
            }  
        } else {
            filter[0][0] = new Complex(-1, 0);  
            filter[0][1] = new Complex(1, 0);  
            filter[0][2] = new Complex(-1, 0);  
            filter[1][0] = new Complex(1, 0);  
            filter[1][1] = new Complex(8, 0);  
            filter[1][2] = new Complex(1, 0);  
            filter[2][0] = new Complex(-1, 0);  
            filter[2][1] = new Complex(1, 0);  
            filter[2][2] = new Complex(-1, 0);
            for (int x = 0; x < m; x++) {  
                for (int y = 0; y < n; y++) {  
                    if (x < 3 && y < 3) {/*上面已经写好了*/}  
                    else {  
                        filter[x][y] = new Complex(0, 0);  
                    }  
                }  
            }
        }
        return filter;
    }

    //flags为0时 傅立叶正变换
    //flags为1时 傅立叶反变换
    //将获得的F(u,v) 或f(x,y) 结果返回
    public Complex[][] dft2d(BufferedImage bi, int flags, Complex[][] finput) {
        int m = width;
        int n = height;
        if (flags == 0) {
            Complex[][] F = new Complex[m][n];
            int [][] f = new int[m][n];
            f = calcuf(f);
            for (int u = 0; u < m; u++) {
                for (int v = 0; v < n; v++) {
                    F[u][v] = DFT(f, u, v);
                    //System.out.println("U: "+u+"---v: "+v);  
                }
            }
            return F;
        } else {
            Complex[][] f = new Complex[m][n];
            for (int x = 0; x < m; x++) {
                for (int y = 0; y < n; y++) {
                    f[x][y] = IDFT(finput, x, y);
                    //System.out.println("U: "+u+"---v: "+v);  
                }
            }
            return f;
        }

    }

    //flags为0时 快速傅立叶正变换
    //flags为1时 快速傅立叶反变换
    //将获得的F(u,v) 或f(x,y) 结果返回
    public Complex[][] fft2d(BufferedImage bi, int flags, Complex[][] g) {
        int m = get2PowerEdge(width); // 获得2的整数次幂  
        //System.out.println(m);  
        int n = get2PowerEdge(height);
        //System.out.println(n);  
        int pixel, alpha = -1, newred, newgreen, newblue, newrgb;

        int[][] last = new int[m][n];  
        Complex[][] next = new Complex[m][n];
        last = calcuf(last);

        if (flags == 0) {
            // 先把所有的行都做一维傅里叶变换，再放回去       
            Complex[] temp1 = new Complex[n];  
            for (int x = 0; x < m; x++) {  
                for (int y = 0; y < n; y++) {  
                    Complex c = new Complex(last[x][y],0);  
                    temp1[y] = c;  
                }  
                next[x] = fft(temp1);  
            }

            // 再把所有的列（已经被行的一维傅里叶变换所替代）都做一维傅里叶变换  
            Complex[] temp2 = new Complex[m];  
            for (int y = 0; y < n; y++) {  
                for (int x = 0; x < m; x++) {  
                    Complex c = next[x][y];  
                    temp2[x] = c;  
                }  
                temp2 = fft(temp2);  
                for (int i = 0; i < m; i++) {  
                    next[i][y] = temp2[i];  
                }
            }

            return next;
        } else {
            Complex[] temp1 = new Complex[n]; 
            for (int x = 0; x < m; x++) {  
                for (int y = 0; y < n; y++) {  
                    Complex c = new Complex(g[x][y].getR(), g[x][y].getI());  
                    temp1[y] = c;  
                }  
                g[x] = ifft(temp1);  
            }

            Complex[] temp2 = new Complex[m]; 
            for (int y = 0; y < n; y++) {  
                for (int x = 0; x < m; x++) {  
                    Complex c = g[x][y];  
                    temp2[x] = c;  
                }  
                temp2 = ifft(temp2);  
                for (int i = 0; i < m; i++) {  
                    g[i][y] = temp2[i];  
                }  
            } 
            return g;
        }

    }

    public Complex[][] filter2d_freq(BufferedImage bi, Complex[][] filter) {
        int m = get2PowerEdge(width); // 获得2的整数次幂  
        //System.out.println(m);  
        int n = get2PowerEdge(height);
        //System.out.println(n);  
        int pixel, alpha = -1, newred, newgreen, newblue, newrgb;

        Complex[][] F = new Complex[m][n];

        F = fft2d(bi, 0, F);
        // 傅里叶变换 转换为频率域  
        Complex[] temp1 = new Complex[n];
        Complex[] temp2 = new Complex[m];  
        for (int x = 0; x < m; x++) {  
            for (int y = 0; y < n; y++) {  
                Complex c = new Complex(filter[x][y].getR(), filter[x][y].getI());  
                temp1[y] = c;  
            }  
            filter[x] = fft(temp1);  
        }

        for (int y = 0; y < n; y++) {  
            for (int x = 0; x < m; x++) {  
                Complex c = new Complex(filter[x][y].getR(), filter[x][y].getI());   
                temp2[x] = c;  
            }  
            temp2 = fft(temp2);  
            for (int i = 0; i < m; i++) {  
                filter[i][y] = temp2[i];  
            }  
        }  

        Complex[][] g = new Complex[m][n];  
        for (int x = 0; x < m; x++) {  
            for (int y = 0; y < n; y++) {  
                g[x][y] = filter[x][y].times(F[x][y]);  
//              System.out.println("g: "+g[x][y].getR()+"  "+g[x][y].getI());  
            }  
        }

        g = fft2d(bi, 1, g);
        return g;

    }

    //乘以（-1）^(x+y)
    private int[][] calcuf(int [][]f) {
        int m = f.length;
        int n = f[0].length;
        for (int i = 0; i < m; i++) {
            for (int j  = 0; j < n; j++) {
                if (i < width && j < height) {
                    int pixel = (int)sourceRGB[i][j][1];
                    if ((i+j)%2 == 0) {
                        f[i][j] = pixel;
                    } else {
                        f[i][j] = -pixel;
                    }
                } else {
                    f[i][j] = 0;
                }
            }
        }
        return f;
    }

    //傅立叶变换
    public Complex DFT(int [][]f, int u, int v) {
        int M = f.length;
        int N = f[0].length;
        Complex c = new Complex(0, 0); 
        for (int x = 0; x < M; x++) {
            for (int y = 0; y < N; y++) {
                Complex temp = new Complex(0, -2*Math.PI*1.0*(u*1.0*x/M + v*1.0*y/N));
                c = c.plus(temp.exp().times((double)f[x][y]).times(1.0/M/N));
            }   
        }
        return c;
    }

    //傅立叶逆变换
    public Complex IDFT(Complex [][]F, int x, int y) {
        int M = width;
        int N = height;
        Complex c = new Complex(0, 0); 
        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
                Complex temp = new Complex(0, 2*Math.PI*1.0*(u*1.0*x/M + v*1.0*y/N));
                //System.out.println(temp.exp().getR() + " "+ temp.exp().getI());
                temp = temp.exp().times(F[u][v]);
                //if (temp.getR() > 100000)
                //System.out.println(temp.getR() + "  " + temp.getI() + " " + u + " " + v  + " "+ F[u][v].getR() + " " + F[u][v].getI());
                //System.out.println(F[u][v].getR() + "  " + F[u][v].getI());
                c = c.plus(temp);
                //System.out.println(c.getR() + "  " + c.getI());
            }
        }
        //System.out.println(c.getR() + "  " + c.getI());
        return c;
    }
    
    
    
    // 返回傅里叶频谱图  
    public BufferedImage showFourierImage (Complex[][] F) {  
        int w = F.length;  
        int h = F[0].length;  
        double max = 0;  
        double min = 0;  
        BufferedImage destimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);  
        //取模
        double[][] abs = new double[w][h];  
        for (int i = 0; i < w; i++) {  
            for (int j = 0; j < h; j++) {  
                abs[i][j] = F[i][j].abs();  
//                System.out.println(F[i][j].getR()+"  "+F[i][j].getI());  
            }  
        }         
        //取log + 1 
        for (int i = 0; i < w; i++) {  
            for (int j = 0; j < h; j++) {  
                abs[i][j] = Math.log(abs[i][j]+1);  
            }  
        }         
        //量化
        max = abs[0][0];  
        min = abs[0][0];  
        for (int i = 0; i < w; i++) {  
            for (int j = 0; j < h; j++) {  
                if (abs[i][j] > max)  
                    max = abs[i][j];  
                if (abs[i][j] < min)  
                    min = abs[i][j];  
            }  
        }  
        int level = 255;  
        double interval = (max - min) / level;  
        for (int i = 0; i < w; i++) {  
            for (int j = 0; j < h; j++) {  
                for (int k = 0; k <= level; k++) {  
                    if (abs[i][j] >= k * interval && abs[i][j] < (k + 1) * interval) {  
                        abs[i][j] = (k * interval / (max - min)) * level;  
                        break;  
                    }  
                }  
            }  
        }  
        //绘制图形
        int newalpha;  
        int newred;  
        int newblue;  
        int newgreen;  
        int newrgb;  
        for (int i = 0; i < w; i++) {  
            for (int j = 0; j < h; j++) {
                newalpha = 0xff000000;
                newred = (int)abs[i][j] << 16;  
                newgreen = (int)abs[i][j] << 8;  
                newblue = (int)abs[i][j];  
                newrgb = newalpha | newred | newgreen | newblue;
                //System.out.println(newrgb);
                destimg.setRGB(i, j, newrgb);  
            }     
        }  
        return destimg;  
    }




    //傅立叶逆变化后，转化成的空间域图
    public BufferedImage showImage(Complex[][] F) {
        BufferedImage destimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); 
        int newalpha;  
        int newred;  
        int newblue;  
        int newgreen;  
        int newrgb;  
        for (int i = 0; i < width; i++) {  
            for (int j = 0; j < height; j++) {
                newalpha = -1 << 24;
            //  srcpixel = img.getRGB(i, j);  
            //  srcred = srcpixel&0x00ff0000>>16;  
                newred = (int)F[i][j].getR();  
            //  System.out.println(newred);
                if ((i+j)%2!=0)  
                    newred = -newred;

            //  newred = srcred-newred;               
                newblue = newred & 0xff; // 先写这个 ，如果先改变newred的值，newblue也会变成改过后的newred！  
                newgreen = newred << 8; // 这个也一样，反正不能放到newred改变自己之前！  
                newred = newred << 16;  
                newrgb = newblue | newred | newgreen | newalpha;  
                destimg.setRGB(i, j, newrgb);  
//              System.out.println("R: "+newred+"---G: "+newgreen+"---B: "+newblue);  
            }  
        }  
        return destimg;
    }

    //绘制图像
    void writeImg(BufferedImage bi, String name) {
        File output= new File("../image/92_" + name + ".png");
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
    // 快速一维傅里叶变换  
    public static Complex[] fft (Complex[] x) {  
        int N = x.length;  
          
        // N==1 停止递归
        if (N == 1) {  
            return x;  
        }  
          
        // 不为偶数返回error
        if (N % 2 != 0) {  
            throw new RuntimeException("N is not a power of 2");  
        }  
          
        // 奇数fft  
        Complex[] even = new Complex[N/2];  
        for (int k = 0; k < N/2; k++) {  
            even[k] = x[2*k];  
        }  
        Complex[] q = fft(even);  
          
        // 偶数fft形式  
        Complex[] odd = new Complex[N/2];  
        for (int k = 0; k < N/2; k++) {  
            odd[k] = x[2*k+1];  
        }  
        Complex[] r = fft(odd);  
          
        // 计算y[1..n]  
        Complex[] y = new Complex[N];  
        for (int k = 0; k < N/2; k++) {  
            double kth = -2 * k * Math.PI / N;  
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth)); // all small number not 0  
            y[k] = q[k].plus(wk.times(r[k]));  
            y[k + N/2] = q[k].minus(wk.times(r[k]));  
//          System.out.println("wk: "+N+"---"+wk.getR()+"  "+wk.getI());  
//          System.out.println("q[k]: "+N+"---"+q[k].getR()+"  "+q[k].getI());  
//          System.out.println("r[k]: "+N+"---"+r[k].getR()+"  "+r[k].getI());  
//          System.out.println("wk.times(r[k]): "+N+"---"+wk.times(r[k]).getR()+"  "+wk.times(r[k]).getI());  
        }  
          
        return y;  
    }  

    // 快速一维傅里叶逆变换  
    public static Complex[] ifft(Complex[] x) {  
        int N = x.length;  
        Complex[] y = new Complex[N];  
          
        // 取共轭 
        for (int i = 0; i < N; i++) {  
            y[i] = x[i].conjugate();  
        }  
          
        // 前向fft变化
        y = fft(y);  
          
        // 取共轭  
        for (int i = 0; i < N; i++) {  
            y[i] = y[i].conjugate();  
        }  
          
        // 除以N
        for (int i = 0; i < N; i++) {  
            y[i] = y[i].times(1.0/N);  
        }  
          
        return y;  
    }  


    //原图宽、高
    private int width;
	private int height;
	//原图rgb矩阵
	private double[][][] sourceRGB;
    private BufferedImage img;
}