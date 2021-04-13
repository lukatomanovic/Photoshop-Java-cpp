package components;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.security.auth.callback.LanguageCallback;

import components.Layer;
import components.Pixel;

import java.io.IOException;

/**
 * 
 * Formater koji podrzava tipove iste kao i BufferedImage
 * @author LT
 * @see BufferedImage
 *
 */
public class EmbededFormatters extends Formatter {

	public EmbededFormatters() {
		// TODO Auto-generated constructor stub
	}

	public void SaveImage(Layer layer, String path) {
		BufferedImage bufferedImage=new BufferedImage(layer.getWidth(),layer.getHeight(),BufferedImage.TYPE_INT_ARGB);
		int height=layer.getHeight();
		int width=layer.getWidth();
		int color=0;
		Pixel p=null;
		for (int y = 0; y < height; y++) 
			for (int x = 0; x < width; x++) {
				p=layer.layer_.get(y*width+x);
				color=p.getAlpha()<<8;
				color=(color|p.getRed())<<8;
				color=(color|p.getGreen())<<8;
				color=color|p.getBlue();
				bufferedImage.setRGB(x, y, color);
			}
		try {
			ImageIO.write(bufferedImage, "BMP", new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Layer ReadImage(String path) {
		File file = new File(path);
		BufferedImage bi = null;
		Layer l=null;
		try {
			bi = ImageIO.read(file);
		} catch (IOException e) {
			return l;
		}

		int width = bi.getWidth();
		int height = bi.getHeight();
		l = new Layer(width, height);
		int alpha,red,green,blue,color;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				color = bi.getRGB(j, i);
				alpha = (color & 0xff000000) >> 24;
				alpha=(alpha<0)?alpha+256:alpha;
				red =  (color & 0x00ff0000) >> 16;
				green =  (color & 0x0000ff00) >> 8;
				blue =  color & 0x000000ff;
				l.layer_.add(new Pixel((short)red, (short)green, (short)blue, (short)alpha));
			}
		}
		return l;

	}

	@Override
	public void exportImage(Image image, String path) {
		Layer l=image.compressIntoOneLayer();
		SaveImage(l, path);

	}

	@Override
	public void importImage(Image image, String path) {
		Layer layer =ReadImage(path);
		image.addLayer(layer);
	}

}
