#pragma once
#ifndef FORMATTER_HPP_
#define FORMATTER_HPP_

#include"Layer.h"
#include <string>

class Image;
class Formatter
{
public:
	//Formatter() {};
	//virtual Layer ReadFile(std::string filename)=0;
	//virtual void SaveFile(std::string path, const Layer& l)const=0;
	virtual ~Formatter() {};

	virtual void saveImage(Image& image, const std::string& path, std::string proj_name)=0;
	virtual void loadImage(Image &i,const std::string& path)=0;
};
#endif
