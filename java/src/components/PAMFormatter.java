package components;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class PAMFormatter extends Formatter {

	public PAMFormatter() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void SaveImage(Layer l, String path) {
		OutputStream outputStream = null;

		try {
			byte b1;

			byte[] b2 = new byte[2];
			byte[] b4 = new byte[4];
			outputStream = new FileOutputStream(path);
			outputStream.write("P7\n".getBytes(StandardCharsets.UTF_8));
			outputStream.write(("WIDTH " + l.getWidth() + "\n").getBytes(StandardCharsets.UTF_8));
			outputStream.write(("HEIGHT " + l.getHeight() + "\n").getBytes(StandardCharsets.UTF_8));
			outputStream.write(("DEPTH 4\n").getBytes(StandardCharsets.UTF_8));
			outputStream.write(("MAXVAL 255\n").getBytes(StandardCharsets.UTF_8));
			outputStream.write(("TUPLTYPE RGB_ALPHA\n").getBytes(StandardCharsets.UTF_8));
			outputStream.write(("ENDHDR\n").getBytes(StandardCharsets.UTF_8));

			for (Pixel p : l.layer_) {
				b4[0] = (byte) p.getRed();
				b4[1] = (byte) p.getGreen();
				b4[2] = (byte) p.getBlue();
				b4[3] = (byte) p.getAlpha();
				outputStream.write(b4);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	@Override
	public Layer ReadImage(String path) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
			byte b;
			byte[] b4 = new byte[4];
			do {
				b = (byte) inputStream.read();
			} while (b != ' ');

			int width = 0, height = 0;
			// citanje sirine
			b = (byte) inputStream.read();
			while (b != '\n') {
				width = width * 10 + (b - '0');// uint8 kao unsigned char 0-255
				b = (byte) inputStream.read();
			}

			// citanje slova i medjuprostora izmedju width i height podatka
			do {
				b = (byte) inputStream.read();
			} while (b != ' ');

			// citanje visine
			b = (byte) inputStream.read();
			while (b != '\n') {
				height = height * 10 + (b - '0');// uint8 kao unsigned char 0-255
				b = (byte) inputStream.read();
			}

			Layer layer = new Layer(width, height);

			// preskok cetiri tacke max depth itd.
			int preskok = 4;
			while (preskok > 0) {
				do {
					b = (byte) inputStream.read();
				} while (b != '\n');
				preskok--;
			}
			Pixel pixel_read;
			int total = width * height;
			int total_read = 0;
			short byte0 = 0, byte1 = 0, byte2 = 0, byte3 = 0;
			while (total_read != total) {
				inputStream.read(b4, 0, 4);
				byte0 = (short) ((b4[0] >= 0) ? b4[0] : ((short) b4[0]) + 256);
				byte1 = (short) ((b4[1] >= 0) ? b4[1] : ((short) b4[1]) + 256);
				byte2 = (short) ((b4[2] >= 0) ? b4[2] : ((short) b4[2]) + 256);
				byte3 = (short) ((b4[3] >= 0) ? b4[3] : ((short) b4[3]) + 256);

				pixel_read = new Pixel(byte0, byte1, byte2, byte3);
				layer.layer_.add(pixel_read);
				total_read++;
			}

			return layer;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void exportImage(Image image, String path) {
		Layer l = image.compressIntoOneLayer();
		SaveImage(l, path);

	}

	@Override
	public void importImage(Image image, String path) {
		Layer l = ReadImage(path);
		image.addLayer(l);

	}

}
