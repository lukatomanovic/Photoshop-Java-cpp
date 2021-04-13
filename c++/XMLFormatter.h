#pragma once

#ifndef XMLFormatter_HPP_
#define XMLFormatter_HPP_

#include "Formatter.h"
#include"tinyxml/tinystr.h"
#include"tinyxml/tinyxml.h"
class BMPFormatter;
class CompositeOperation;
class Image;
class XMLFormatter:public Formatter
{
public:
	XMLFormatter();
	void saveFun(const std::string &path,const CompositeOperation *co);
	std::string loadFun(const std::string& path);
	void saveImage(Image& image, const std::string& path, std::string proj_name);
	Image * loadImage(const std::string& path);
	void loadImage(Image& i, const std::string& path);
private:
	Layer ReadFile(std::string filename);
	void SaveFile(std::string path, const Layer& l)const;
};
#endif
