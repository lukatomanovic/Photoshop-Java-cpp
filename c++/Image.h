#pragma once
#ifndef IMAGE_HPP_
#define IMAGE_HPP_

#include "Layer.h"
#include "Formatter.h"
#include "Selection.h"
#include"Operation.h"
#include<map>

class Image
{
public:
	Image();
	~Image();
	unsigned getWidth()const { return img_width_; }
	unsigned getHeight()const { return img_height_; }
	unsigned getNumOfLayers()const { return layers_.size(); }
	bool isSaved()const { return saved_; }

	//void setWidth(int w) { img_width_ = w; }
	//void setHeight(int h) { img_height_ = h; }

	void setFormatter(Formatter* f) { format_ = f; }

	void addLayer(Layer& l);//ne koristi korisnik
	void addLayer(int w=-1, int h=-1);//poziva prvu
	void addLayer(std::string path);//poziva prvu
	void setLayerStatus(unsigned pos, bool status);//active, inactive
	void setLayerVisible(unsigned pos, bool visible);
	void setLeyerOpacity(unsigned pos, unsigned opacity);
	void deleteLayer(unsigned pos);


	void addSelection(std::string name, std::vector<Pravougaonik> & rectangles);
	void setSelectionStatus(std::string name, bool status);
	void deleteSelection(std::string name);



	void createCompositeOperation(std::string name,std::string operation);
	void exportCompositeOperation(std::string name,std::string path);
	void importCompositeOperation(std::string name, std::string path);

	bool ExecuteOperation(std::string cmd);
	Layer& getLayer() { return layers_[0]; }//izbaciti, za potrebe testiranja dodato
	Layer compressIntoOneLayer();


	std::map<std::pair<unsigned, unsigned>, unsigned>& getSelected() { return slected_pixels_; }

	void loadImage(const std::string& path);
	void SaveImage(const std::string& path, std::string fname);

	unsigned numofparams(std::string name);
private:
	friend class XMLFormatter;
	std::vector<Layer> layers_;
	std::map<std::string, Selection> selections_;
	std::map<std::pair<unsigned, unsigned>, unsigned> slected_pixels_;
	std::map<std::string, Operation*> operations_;
	std::map<std::string, Formatter*> formatters_;
	Formatter *format_=nullptr;
	unsigned num_of_layers_=0;
	unsigned img_width_=0 ,img_height_=0;
	bool saved_;
	void deleteImage();

	void activateSelection(std::string name);//ne koristi korisnik
	void deactivateSelection(std::string name);//ne koristi korisnik
};
int checkVal(int);

#endif