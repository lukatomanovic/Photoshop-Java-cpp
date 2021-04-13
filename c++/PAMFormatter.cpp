#include "PAMFormatter.h"
#include"Image.h"
#include <fstream>  
Layer PAMFormatter::ReadFile(std::string filename)
{
	std::vector<Pixel> layer;

	FILE* fio = fopen(filename.c_str(), "rb");
	if (fio == NULL)
		throw ReadingError();
	uint8_t byte;
	try {
		do {
			fread(&byte, sizeof(byte), 1, fio);
		} while (byte != ' ');

		unsigned width = 0, height = 0;
		//citanje sirine
		fread(&byte, sizeof(byte), 1, fio);//pretpostavka da postoji sirina od jednog bajta da se procita
		while (byte != '\n') {
			width = width * 10 + (byte - '0');//uint8 kao unsigned char 0-255
			fread(&byte, sizeof(byte), 1, fio);
		}

		//citanje slova i medjuprostora izmedju width i height podatka
		do {
			fread(&byte, sizeof(byte), 1, fio);
		} while (byte != ' ');

		//citanje visine
		fread(&byte, sizeof(byte), 1, fio);//pretpostavka da postoji sirina od jednog bajta da se procita
		while (byte != '\n') {
			height = height * 10 + (byte - '0');//uint8 kao unsigned char 0-255
			fread(&byte, sizeof(byte), 1, fio);
		}

		Layer picture_(width, height);

		//preskok cetiri tacke max depth itd.
		unsigned preskok = 4;
		while (preskok) {
			do {
				fread(&byte, sizeof(byte), 1, fio);
			} while (byte != '\n');
			preskok--;
		}

		uint8_t data[4];
		Pixel p;
		while (fread(data, sizeof(uint8_t), 4, fio)) {
			p.setRed(data[0]);
			p.setGreen(data[1]);
			p.setBlue(data[2]);
			p.setAlpha(data[3]);
			layer.push_back(p);
		}
		if (layer.size() != width * height) {
			fclose(fio);
			throw ReadingError("Fajl je ostecen!");
		}
		picture_.setLayer(layer);
		return picture_;
	}
	catch (...) {
		fclose(fio);
		throw ReadingError("Los format fajla Treba biti 32-bit bitmap!");
	}
}

void PAMFormatter::SaveFile(std::string path, const Layer& l) const
{
	std::ofstream fout(path, std::ios::out | std::ios::binary | std::ios::trunc);
	fout << "P7\n";
	fout << "WIDTH " << l.getWidth()<<std::endl;
	fout << "HEIGHT " << l.getHeight() << std::endl;
	fout << "DEPTH " << 4 << std::endl;
	fout << "MAXVAL " << 255 << std::endl;
	fout << "TUPLTYPE RGB_ALPHA" << std::endl;
	fout << "ENDHDR" << std::endl;
	uint8_t data[4];
	for (const Pixel& p : l) {
		data[0]=p.getRed();
		data[1] = p.getGreen();
		data[2] = p.getBlue();
		data[3] = p.getAlpha();
		fout.write((char*)data, 4);//36h-39h
	}
	fout.close();
}

void PAMFormatter::saveImage(Image& image, const std::string& path, std::string file_name)
{
	Layer&& l = image.compressIntoOneLayer();
	SaveFile(path + file_name + ".pam", l);
}

void PAMFormatter::loadImage(Image& img, const std::string& path)
{
	Layer&& l = ReadFile(path);
	img.addLayer(l);
}
