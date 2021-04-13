#pragma once
#ifndef BMPFORMATTER_HPP_
#define BMPFORMATTER_HPP_
#include "Formatter.h"
#include"ReadingError.h"
#include<cstdint>

class Image;
class BMPFormatter :
	public Formatter
{
public:
	BMPFormatter()  {}
	Layer ReadFile(std::string filename);
	void SaveFile(std::string path,const Layer &l)const;

	void saveImage(Image& image, const std::string& path, std::string file_name);
	void loadImage(Image& i, const std::string& path);
};
#endif

