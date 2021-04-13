package components;

public class Pixel implements Cloneable {

	private short R_, G_, B_;
	private short alpha_;
	public static final short max_val_ = 255;

	public Pixel(short r_, short g_, short b_, short alpha_) {
		super();
		R_ = r_;
		G_ = g_;
		B_ = b_;
		this.alpha_ = alpha_;
	}

	public Pixel() {
		super();
		R_ = 0;
		G_ = 0;
		B_ = 0;
		this.alpha_ = 0;
	}

	public short getRed() {
		return R_;
	}

	public short getGreen() {
		return G_;
	}

	public short getBlue() {
		return B_;
	}

	public short getAlpha() {
		return alpha_;
	}

	public void setRed(short r_) {
		R_ = r_;
	}

	public void setGreen(short g_) {
		G_ = g_;
	}

	public void setBlue(short b_) {
		B_ = b_;
	}

	public void setAlpha(short alpha_) {
		this.alpha_ = alpha_;
	}

	@Override
	public Pixel clone() {
		// TODO Auto-generated method stub
		try {
			return (Pixel) super.clone();
		} catch (CloneNotSupportedException e) {

			return null;
		}
	}

}
