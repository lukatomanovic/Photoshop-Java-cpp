#include "XMLFormatter.h"
#include"CompositeOperation.h"
#include<algorithm>
#include"LoadingError.h"
#include"Image.h"
#include"Selection.h"
#include"BMPFormatter.h"
#include"SavingError.h"
#include <direct.h>

XMLFormatter::XMLFormatter()
{
}

Layer XMLFormatter::ReadFile(std::string filename)
{
	BMPFormatter* layer_loader = new BMPFormatter();
	return layer_loader->ReadFile(filename);;
}

void XMLFormatter::SaveFile(std::string path, const Layer& l)const
{
	BMPFormatter* layer_saver = new BMPFormatter();
	layer_saver->SaveFile(path, l);
}

void XMLFormatter::saveFun(const std::string& path,const CompositeOperation* op)
{
	TiXmlDocument doc;
	TiXmlDeclaration* decl = new TiXmlDeclaration("1.0", "utf-8", "");
	doc.LinkEndChild(decl);
	TiXmlElement* root = new TiXmlElement("CompositeOperation");

	root->SetAttribute("name", op->getName().c_str());
	doc.LinkEndChild(root);

	std::vector<std::string> desc = op->getDescription();
	TiXmlElement* oper;

	std::for_each(desc.begin(), desc.end(), [&](std::string boname) {
		oper = new TiXmlElement("Operation");
		oper->LinkEndChild(new TiXmlText(boname.c_str()));
		root->LinkEndChild(oper);
		});
	doc.SaveFile(path.c_str());
}

std::string XMLFormatter::loadFun(const std::string& path)
{
	std::string operationstr = "";
	TiXmlDocument doc(path.c_str());
	if (!doc.LoadFile()) throw new LoadingError("Greska pri ucitavanju kompozitne operacije");
	TiXmlHandle hDoc(&doc);
	TiXmlElement* pElem;
	TiXmlHandle hRoot(0);
	pElem = hDoc.FirstChildElement().Element();
	if (!pElem) throw new LoadingError("Greska pri ucitavanju kompozitne operacije");
	hRoot = TiXmlHandle(pElem);
	pElem = hRoot.FirstChild().Element();
	bool prvi = true;
	for (pElem; pElem; pElem = pElem->NextSiblingElement())
	{
		const char* pText = pElem->GetText();
		if (prvi) {
			operationstr= pElem->GetText();
			prvi = false;
		}
		else {
			operationstr=operationstr+"|"+ pElem->GetText();
		}
	}
	return operationstr;
}

void XMLFormatter::saveImage(Image& image,const std::string& path, std::string proj_name)
{
	std::string dir;
	dir = _getcwd(NULL, 0);
	dir = dir+"\\"+path  + proj_name;
	_mkdir(dir.c_str());
	_mkdir((dir + "\\files").c_str());

	std::string filename =dir+"\\"+ proj_name + ".xml";
	TiXmlDocument doc;
	TiXmlDeclaration* decl = new TiXmlDeclaration("1.0", "utf-8", "");
	doc.LinkEndChild(decl);
	TiXmlElement* img = new TiXmlElement("Image");

	img->SetAttribute("width", image.getWidth());
	img->SetAttribute("height", image.getHeight());
	doc.LinkEndChild(img);
	TiXmlElement* layers=new TiXmlElement("Layers");
	img->LinkEndChild(layers);


	TiXmlElement* layer;
	int i=0;
	std::for_each(image.layers_.begin(),image.layers_.end(), [&](const Layer &l) {
		layer = new TiXmlElement("Layer");
		layer->SetAttribute("opacity", l.getOpacity());
		layer->SetAttribute("active", (l.isActive()) ? 1 : 0);
		layer->SetAttribute("visible", (l.isVisible()) ? 1 : 0);
		_mkdir((dir + "\\files\\layers").c_str());
		std::string laypath = path + proj_name + "/files/layers/layer" + std::to_string(i++) + ".bmp";
		this->SaveFile(laypath, l);
		layer->LinkEndChild(new TiXmlText(laypath.c_str()));
		layers->LinkEndChild(layer);
		});

	TiXmlElement* selections = new TiXmlElement("Selections");
	img->LinkEndChild(selections);
	TiXmlElement* selection;
	TiXmlElement* rect;
	std::for_each(image.selections_.begin(), image.selections_.end(), [&](const std::pair<std::string, Selection> & s) {
		selection = new TiXmlElement("Selection"); 
		selection->SetAttribute("name", s.first.c_str());
		selection->SetAttribute("active", s.second.isActive());
		for (const Pravougaonik& p : s.second) {
			rect=new TiXmlElement("Rectangle");
			rect->SetAttribute("startx", p.getStartX());
			rect->SetAttribute("starty", p.getStartY());
			rect->SetAttribute("width", p.getWidth());
			rect->SetAttribute("height", p.getHeight());
			selection->LinkEndChild(rect);
		}
		selections->LinkEndChild(selection);
		});
	
	TiXmlElement* operations = new TiXmlElement("Operations");
	img->LinkEndChild(operations);
	TiXmlElement* compositeOper;
	std::for_each(image.operations_.begin(), image.operations_.end(), [&](const std::pair<std::string, Operation*>& o) {
		CompositeOperation* co = dynamic_cast<CompositeOperation*>(o.second);
		if (co) {
			compositeOper = new TiXmlElement("CompositeOperations");
			compositeOper->SetAttribute("name", o.second->getName().c_str());
			_mkdir((dir + "\\files\\funs").c_str());
			std::string funpath = path + proj_name + "/files/funs/" + o.first + ".fun";
			//std::string funpath = path + proj_name + "/files/funs/"+o.first+".fun";
			this->saveFun(funpath,(const CompositeOperation*)o.second);
			compositeOper->LinkEndChild(new TiXmlText(funpath.c_str()));
			operations->LinkEndChild(compositeOper);
		}
		});


	doc.SaveFile(filename.c_str());
}

void XMLFormatter::loadImage(Image& image, const std::string& path)
{
	std::string filename = path;
	Image* img = new Image();
	TiXmlDocument doc(filename.c_str());
	if (!doc.LoadFile()) throw LoadingError("Greska pri ucitavanju projekta!");
	TiXmlHandle hDoc(&doc);
	TiXmlElement* elem;
	TiXmlHandle hRoot(0);
	elem = hDoc.FirstChildElement().Element();
	// should always have a valid root but handle gracefully if it does
	if (!elem) throw LoadingError("Greska pri ucitavanju XML-a!");
	int w, h;
	elem->QueryIntAttribute("width", &w);
	elem->QueryIntAttribute("height", &h);
	img->img_width_ = w;
	img->img_height_ = h;
	hRoot = TiXmlHandle(elem);
	elem = hRoot.FirstChild("Layers").FirstChild().Element();
	for (elem; elem; elem = elem->NextSiblingElement())
	{
		std::string laypath = elem->GetText();
		int opacity, active, visible;
		elem->QueryIntAttribute("opacity", &opacity);
		elem->QueryIntAttribute("active", &active);
		elem->QueryIntAttribute("visible", &visible);
		Layer&& l = ReadFile(laypath);
		if (opacity > 100)opacity = 100;
		if (opacity < 0)opacity = 0;
		l.setOpacity(opacity);
		l.setActive(active);
		l.setVisible(visible);
		img->addLayer(l);
	}

	elem = hRoot.FirstChild("Selections").FirstChild().Element();
	for (elem; elem; elem = elem->NextSiblingElement()) {
		int active;
		std::string sname = elem->Attribute("name");
		elem->QueryIntAttribute("active", &active);
		TiXmlElement* pElem = elem->FirstChildElement();
		std::vector<Pravougaonik> rectangles;
		int sx, sy, rw, rh;
		for (pElem; pElem; pElem = pElem->NextSiblingElement()) {//rectangles
			pElem->QueryIntAttribute("startx", &sx);
			pElem->QueryIntAttribute("starty", &sy);
			pElem->QueryIntAttribute("width", &rw);
			pElem->QueryIntAttribute("height", &rh);
			rectangles.push_back(Pravougaonik(sy, sx, rw, rh));
		}
		img->addSelection(sname, rectangles);
		img->activateSelection(sname);
	}

	elem = hRoot.FirstChild("Operations").FirstChild().Element();
	for (elem; elem; elem = elem->NextSiblingElement())
	{
		std::string oppath = elem->GetText();
		std::string oname = elem->Attribute("name");
		std::string opercmd = loadFun(oppath);
		img->createCompositeOperation(oname, opercmd);
	}
	image = *img;
	delete img;
}

Image* XMLFormatter::loadImage(const std::string& path)
{
	std::string filename = path;
	Image* img = new Image();
	TiXmlDocument doc(filename.c_str());
	if (!doc.LoadFile()) throw LoadingError("Greska pri ucitavanju projekta!");
	TiXmlHandle hDoc(&doc);
	TiXmlElement* elem;
	TiXmlHandle hRoot(0);
	elem = hDoc.FirstChildElement().Element();
	// should always have a valid root but handle gracefully if it does
	if (!elem) throw LoadingError("Greska pri ucitavanju XML-a!");
	int w, h;
	elem->QueryIntAttribute("width", &w);
	elem->QueryIntAttribute("height", &h);
	img->img_width_ = w;
	img->img_height_ = h;
	hRoot = TiXmlHandle(elem);
	elem = hRoot.FirstChild("Layers").FirstChild().Element();
	for (elem; elem; elem = elem->NextSiblingElement())
	{
		std::string laypath = elem->GetText();
		int opacity, active, visible;
		elem->QueryIntAttribute("opacity", &opacity);
		elem->QueryIntAttribute("active", &active);
		elem->QueryIntAttribute("visible", &visible);
		Layer&& l = ReadFile(laypath);
		if (opacity > 100)opacity = 100;
		if (opacity < 0)opacity = 0;
		l.setOpacity(opacity);
		l.setActive(active);
		l.setVisible(visible);
		img->addLayer(l);
	}

	elem = hRoot.FirstChild("Selections").FirstChild().Element();
	for (elem; elem; elem = elem->NextSiblingElement()) {
		int active;
		std::string sname = elem->Attribute("name");
		elem->QueryIntAttribute("active", &active);
		TiXmlElement* pElem = elem->FirstChildElement();
		std::vector<Pravougaonik> rectangles;
		int sx, sy, rw, rh;
		for (pElem; pElem; pElem = pElem->NextSiblingElement()) {//rectangles
			pElem->QueryIntAttribute("startx", &sx);
			pElem->QueryIntAttribute("starty", &sy);
			pElem->QueryIntAttribute("width", &rw);
			pElem->QueryIntAttribute("height", &rh);
			rectangles.push_back(Pravougaonik(sy, sx, rw, rh));
		}
		img->addSelection(sname, rectangles);
		img->activateSelection(sname);
	}

	elem = hRoot.FirstChild("Operations").FirstChild().Element();
	for (elem; elem; elem = elem->NextSiblingElement())
	{
		std::string oppath = elem->GetText();
		std::string oname = elem->Attribute("name");
		std::string opercmd = loadFun(oppath);
		img->createCompositeOperation(oname, opercmd);
	}
	return img;
}

