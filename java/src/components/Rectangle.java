package components;

public class Rectangle implements Cloneable{
	private int start_x_;// left corner
	private int start_y_;
	private int width_;
	private int height_;

	public Rectangle() {
		// TODO Auto-generated constructor stub
	}

	public Rectangle(int start_x_, int start_y_, int width_, int height_) {
		super();
		this.start_x_ = start_x_;
		this.start_y_ = start_y_;
		this.width_ = width_;
		this.height_ = height_;
	}

	public int getStartX() {
		return start_x_;
	};

	public int getStartY() {
		return start_y_;
	};

	public int getWidth() {
		return width_;
	};

	public int getHeight() {
		return height_;
	};
	
	@Override
	protected Rectangle clone() {
		// TODO Auto-generated method stub
		try {
			return (Rectangle)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
