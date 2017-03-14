import java.lang.Math;
//存储复数的数据结构，方便进行傅立叶变换
public class Complex {
	private double r; //存储实数域
	private double i; //存储虚部域

	public Complex (double r, double i) {
	    this.r = r;
	    this.i = i;
	}

	public double abs() {  // return sqrt(r^2 +i^2)  
	    return Math.hypot(r,i);
	}

	public double phase() { //相位
		return Math.atan2(i, r);  
	}

	public Complex plus(Complex c) { // 两个复数相加
		return new Complex(this.r + c.r, this.i + c.i);
	}

	public Complex minus(Complex c) { // 两个复数相减
		return new Complex(this.r - c.r, this.i - c.i);
	}

	public Complex times(Complex c) {
		return new Complex(this.r * c.r - this.i * c.i, this.r * c.i + this.i * c.r);
	}

	public Complex times(double d) {
		return new Complex (this.r * d, this.i * d);
	}

    public Complex conjugate() {
    	return new Complex(r, -i);
    }

    public double getR() {
    	return r;
    }

    public double getI() {
    	return i;
    }

    public Complex exp() {
    	return new Complex(Math.exp(r)*Math.cos(i),Math.exp(r) * Math.sin(i));
    }
}
