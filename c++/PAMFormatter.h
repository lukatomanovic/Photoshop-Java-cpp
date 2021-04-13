#pragma once
#ifndef PAMFORMATTER_HPP_
#define PAMFORMATTER_HPP_
#include "Formatter.h"
#include"ReadingError.h"
#include<cstdint>

class PAMFormatter :
	public Formatter
{

public:
	PAMFormatter() {}
	Layer ReadFile(std::string filename);
	void SaveFile(std::string path, const Layer& l)const;

	void saveImage(Image& image, const std::string& path, std::string file_name);
	void loadImage(Image& img, const std::string& path);
};
#endif

