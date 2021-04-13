package components;

import java.util.ArrayList;
import java.util.List;

public class Layer implements Cloneable {
	public ArrayList<Pixel> layer_ = new ArrayList<>();
	private int width_, height_;
	private boolean active_;
	private boolean visible_;
	private short opacity_ = 100; // potencijalno byte, ali mozda i 0

	public Layer() {
		super();
	}

	public Layer(int width_, int height_) {
		super();
		this.width_ = width_;
		this.height_ = height_;
		this.active_ = true;
		this.visible_ = true;
		// opacity_=0;

	}

	public void generateTransparentPixels() {
		for (int i = 0; i < this.width_ * this.height_; i++)
			layer_.add(new Pixel((short) 0, (short) 0, (short) 0, (short) 255));
	}

	public final int getWidth() {
		return width_;
	}

	public final int getHeight() {
		return height_;
	}

	public final short getOpacity() {
		return opacity_;
	}

	public Pixel getPixelAt(int row, int col) {
		return layer_.get(row * this.width_ + col);
	}

	public final boolean isActive() {
		return active_;
	}

	public final boolean isVisible() {
		return visible_;
	}

	public void setLayer(ArrayList<Pixel> arp) {
		layer_ = arp;
	};

	public ArrayList<Pixel> getLayer() {
		return layer_;
	};
	
	public final void setWidth(int w) {
		width_ = w;
	}

	public final void setHeight(int h) {
		height_ = h;
	}

	public final synchronized void setOpacity(short o) {
		opacity_ = o;
	}

	public final synchronized void setActive(boolean val) {
		active_ = val;
	}

	public final synchronized void setVisible(boolean val) {
		visible_ = val;
	}

	public Pixel setPixelAt(int row, int col, Pixel p) {
		return layer_.set(row * this.width_ + col, p);
	}

	public void resizeW(int new_width) {
		// definisati
		for (int i = 0; i < height_; i++) {
			int start_row_position = i * new_width + width_;
			for (int j = width_; j < new_width; j++) {
				layer_.add(start_row_position, new Pixel());// pixel sve 0 za vrednosti
			}
		}
		width_ = new_width;
	}

	public void resizeH(int new_height) {
		int start_position = width_ * height_;
		for (int i = 0; i < (new_height - height_); i++) {
			for (int j = 0; j < width_; j++) {
				layer_.add(new Pixel());// pixel sve 0 za vrednosti
			}
		}
		height_ = new_height;
	}

	@Override
	public Layer clone() {
		// TODO Auto-generated method stub
		Layer novi=null;
		try {
			novi = (Layer) super.clone();
			novi.layer_ = new ArrayList<>();
			for (Pixel p : this.layer_) {
				novi.layer_.add(p.clone());
			}
			return novi;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
