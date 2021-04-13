#include "BMPFormatter.h"
#include <fstream>  
#include"Image.h"

Layer BMPFormatter::ReadFile(std::string filename)
{
	std::vector<Pixel> layer;
	FILE* fio = fopen(filename.c_str(), "rb");
	if (fio == NULL)
		throw ReadingError();
	uint8_t header[122];
	fread(header, sizeof(uint8_t), 122, fio);
	unsigned width = *(unsigned*)&header[18];
	unsigned height = *(unsigned*)&header[22];

	Layer picture_;
	picture_.setWidth(width);
	picture_.setHeight(height);
	uint8_t data[4];
	Pixel p,ex;
	int i = 0;
	int tmp = 0;
	try {
		while (fread(data, 4 * sizeof(uint8_t), 1, fio)) {
			p.setRed(data[2]);
			p.setGreen(data[1]);
			p.setBlue(data[0]);
			p.setAlpha(data[3]);
			if (i / width > (height / 2)) {
				int nh = height - i / width - 1;
				if (nh < 0)throw ReadingError();
				ex = layer[nh * width + i % width];
				layer[nh * width + i % width] = p;
				layer.push_back(ex);
			}
			else
				layer.push_back(p);
			i++;
		}
	}
	catch (...) {
		fclose(fio);
		throw ReadingError("Los format fajla Treba biti 32-bit bitmap!");
	}
	if (layer.size() != width * height) {
		fclose(fio);
		throw ReadingError("Fajl je ostecen!");
	}
	fclose(fio);
	picture_.setLayer(layer);
	return picture_;
}

void BMPFormatter::SaveFile(std::string path, const Layer& l)const
{
	std::ofstream fout(path, std::ios::out |std:: ios::binary| std::ios::trunc);
	unsigned bmp_size = l.getWidth() * l.getHeight() * 4;
	uint8_t tmp1;
	uint16_t tmp2;
	unsigned int tmp4;

	tmp2 = 0x4d42;
	fout.write((char*)&tmp2, 2);//0h-1h
	tmp4 = bmp_size+122;//VELICINA FAJLA!!!!----------------------------
	fout.write((char*)&tmp4, 4);//2h-5h
	tmp4 = 0;
	fout.write((char*)&tmp4, 4); //6h-9h
	tmp4 = 122;
	fout.write((char*)&tmp4, 4);//Ah-Dh

	//DIB
	tmp4 = 108;
	fout.write((char*)&tmp4, 4);//Eh-11h
	tmp4 = l.getWidth();//sirina slike-----------------------------------
	fout.write((char*)&tmp4, 4);//12h-15h
	tmp4 = l.getHeight();//visina slike------------------------------------
	fout.write((char*)&tmp4, 4);//16h-19h
	tmp2 = 1;
	fout.write((char*)&tmp2, 2);//1Ah-Bh
	tmp2 = 32;
	fout.write((char*)&tmp2, 2);//1Ch-1Dh
	tmp4 = 3;
	fout.write((char*)&tmp4, 4);//1Eh-21h
	tmp4 = bmp_size;//velicina bitmape u bajtovima--------------------------1000x800
	fout.write((char*)&tmp4, 4);//22h-25h
	tmp4 = 2835;
	fout.write((char*)&tmp4, 4);//26h-29h
	fout.write((char*)&tmp4, 4);//2Ah-2Dh
	tmp4 = 0;
	fout.write((char*)&tmp4, 4);//2Eh-31h
	fout.write((char*)&tmp4, 4);//32h-35h
	tmp4 = 0x00FF0000;
	fout.write((char*)&tmp4, 4);//36h-39h
	tmp4 = 0x0000FF00;
	fout.write((char*)&tmp4, 4);//3Ah-3Dh
	tmp4 = 0x000000FF;
	fout.write((char*)&tmp4, 4);//3Dh-41h
	tmp4 = 0xFF000000;
	fout.write((char*)&tmp4, 4);//42h-45h
	tmp4 = 0x57696E20;
	fout.write((char*)&tmp4, 4);//46h-49h
	tmp4 = 0;
	int i = 9;
	while (i) {
		fout.write((char*)&tmp4, 4);//4Ah-6Dh - 36bajtova
		i--;
	}
	fout.write((char*)&tmp4, 4);//6Eh-71h
	fout.write((char*)&tmp4, 4);//72h-75h
	fout.write((char*)&tmp4, 4);//76h-79h
	uint8_t data[4];
	unsigned w = l.getWidth();
	unsigned h = l.getHeight();
	unsigned pos;
	for (int i = h - 1; i >= 0; i--) {
		for (int j = 0; j < w; j++) {
			pos = i * w + j;
			data[0] = l[pos].getBlue();
			data[1] = l[pos].getGreen();
			data[2] = l[pos].getRed();
			data[3] = l[pos].getAlpha();
			fout.write((char*)data, 4);
		}
	}
	fout.close();

}

void BMPFormatter::saveImage(Image& image, const std::string& path, std::string file_name)
{
	Layer&& l = image.compressIntoOneLayer();
	SaveFile(path + file_name + ".bmp", l);	
}

void BMPFormatter::loadImage(Image& image, const std::string& path)
{
	std::string filename = path;
	Layer &&layer=ReadFile(path);
	image.addLayer(layer);
}
