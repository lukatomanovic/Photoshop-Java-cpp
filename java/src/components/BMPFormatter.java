package components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class BMPFormatter extends Formatter {
	static class Parser {
		static int bytesToInt(byte[] b) {
			int b0 = (b[0] >= 0) ? b[0] : ((int) b[0]) + 256;
			int b1 = (b[1] >= 0) ? b[1] : ((int) b[1]) + 256;
			int b2 = (b[2] >= 0) ? b[2] : ((int) b[2]) + 256;
			int b3 = (b[3] >= 0) ? b[3] : ((int) b[3]) + 256;
			return b0 | b1 << 8 | b2 << 16 | b3 << 24;
		}

		static int bytesToShort(byte[] b) {
			int b0 = (b[0] >= 0) ? b[0] : ((int) b[0]) + 256;
			int b1 = (b[1] >= 0) ? b[1] : ((int) b[1]) + 256;
			return b0 + b1 << 8;
		}

		static byte[] shortToBytes(short s) {
			byte[] bytes=new byte[2];
			bytes[0]=(byte)(s&0x00ff);
			bytes[1]=(byte)((s&0xff00)>>8);
			return bytes;
		}

		static byte[] intToBytes(int i) {
			byte[] bytes=new byte[4];
			bytes[0]=(byte)(i&0x000000ff);
			bytes[1]=(byte)((i&0x0000ff00)>>8);
			bytes[2]=(byte)((i&0x00ff0000)>>16);
			bytes[3]=(byte)((i&0xff000000)>>24);
			return bytes;
		}

	}

	public BMPFormatter() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void SaveImage(Layer l, String path) {
		OutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(path);
			int bmp_size = l.getWidth() * l.getHeight() * 4;
			byte b1;
			byte[] b2 = new byte[2];
			byte[] b4 = new byte[4];

			int tmp;
			b2[0] = 0x42;
			b2[1] = 0x4d;
			outputStream.write(b2);// 0h-1h
			tmp = bmp_size + 122;// VELICINA FAJLA!!!!----------------------------
			outputStream.write(Parser.intToBytes(tmp));// 2h-5h
			tmp = 0;
			outputStream.write(Parser.intToBytes(tmp)); // 6h-9h
			tmp = 122;
			outputStream.write(Parser.intToBytes(tmp));// Ah-Dh

			// DIB
			tmp = 108;
			outputStream.write(Parser.intToBytes(tmp));// Eh-11h
			tmp = l.getWidth();// sirina slike-----------------------------------
			outputStream.write(Parser.intToBytes(tmp));// 12h-15h
			tmp = l.getHeight();// visina slike------------------------------------
			outputStream.write(Parser.intToBytes(tmp));// 16h-19h
			tmp = 1;
			outputStream.write(Parser.shortToBytes((short) tmp));// 1Ah-Bh
			tmp = 32;
			outputStream.write(Parser.shortToBytes((short) tmp));// 1Ch-1Dh
			tmp = 3;
			outputStream.write(Parser.intToBytes(tmp));// 1Eh-21h
			tmp = bmp_size;// velicina bitmape u bajtovima--------------------------1000x800
			outputStream.write(Parser.intToBytes(tmp));// 22h-25h
			tmp = 2835;
			outputStream.write(Parser.intToBytes(tmp));// 26h-29h
			outputStream.write(Parser.intToBytes(tmp));// 2Ah-2Dh
			tmp = 0;
			outputStream.write(Parser.intToBytes(tmp));// 2Eh-31h
			outputStream.write(Parser.intToBytes(tmp));// 32h-35h
			tmp = 0x00FF0000;
			outputStream.write(Parser.intToBytes(tmp));// 36h-39h
			tmp = 0x0000FF00;
			outputStream.write(Parser.intToBytes(tmp));// 3Ah-3Dh
			tmp = 0x000000FF;
			outputStream.write(Parser.intToBytes(tmp));// 3Dh-41h
			tmp = 0xFF000000;
			outputStream.write(Parser.intToBytes(tmp));// 42h-45h
			tmp = 0x57696E20;
			outputStream.write(Parser.intToBytes(tmp));// 46h-49h
			tmp = 0;
			int i = 9;
			while (i > 0) {
				outputStream.write(Parser.intToBytes(tmp));// 4Ah-6Dh - 36bajtova
				i--;
			}
			outputStream.write(Parser.intToBytes(tmp));// 6Eh-71h
			outputStream.write(Parser.intToBytes(tmp));// 72h-75h
			outputStream.write(Parser.intToBytes(tmp));// 76h-79h

			int w = l.getWidth();
			int h = l.getHeight();
			int pos;
			for (i = h - 1; i >= 0; i--) {
				for (int j = 0; j < w; j++) {
					pos = i * w + j;
					b4[0] = (byte) l.layer_.get(pos).getBlue();
					b4[1] = (byte) l.layer_.get(pos).getGreen();
					b4[2] = (byte) l.layer_.get(pos).getRed();
					b4[3] = (byte) l.layer_.get(pos).getAlpha();
					outputStream.write(b4);
				}
			}
			System.out.println("end");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
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
			byte[] b2 = new byte[2];
			byte[] b4 = new byte[4];
			inputStream = new FileInputStream(path);
			inputStream.skip(18);
			// read width
			inputStream.read(b4, 0, 4);
			int width = Parser.bytesToInt(b4);
			// read height 22-26 bajta
			inputStream.read(b4, 0, 4);
			int height = Parser.bytesToInt(b4);
			// preskacemo jos 96 bajtova dok ne stignemo do sadrzaja(preskacemo zaglavlje)
			inputStream.skip(95);
			System.out.println(inputStream.read());

			int total_read = 0, curr_read = 0;
			Pixel pixel_read;
			short byte0 = 0, byte1 = 0, byte2 = 0, byte3 = 0;
			Layer layer_read = new Layer(width, height);
			int total=width * height;
			Pixel ex;
			while (total_read != total) {
				inputStream.read(b4, 0, 4);
				byte0 = (short) ((b4[0] >= 0) ? b4[0] : ((short) b4[0]) + 256);
				byte1 = (short) ((b4[1] >= 0) ? b4[1] : ((short) b4[1]) + 256);
				byte2 = (short) ((b4[2] >= 0) ? b4[2] : ((short) b4[2]) + 256);
				byte3 = (short) ((b4[3] >= 0) ? b4[3] : ((short) b4[3]) + 256);
				pixel_read = new Pixel(byte2, byte1, byte0, byte3);
				if (total_read/width > (height / 2)) {
					int nh = height - total_read/width - 1;
					//if (nh < 0)throw ReadingError();
					ex =layer_read.layer_.get(nh * width + total_read % width);
					layer_read.layer_.set(nh * width + total_read % width, pixel_read);
					layer_read.layer_.add(ex);
				}
				else
					layer_read.layer_.add(pixel_read);
				total_read++;				
			}
			return layer_read;

		} catch (IOException e) {
			return null;
		}
		finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	@Override
	public void exportImage(Image image, String path) {
		Layer l=image.compressIntoOneLayer();
		SaveImage(l, path);
	}

	@Override
	public void importImage(Image image, String path) {
		Layer layer=ReadImage(path);
		image.addLayer(layer);
	}

}
