#include "Image.h"
#include<algorithm>
#include"ParamsError.h"//realno command error dodati
#include"SelectionError.h"
#include<regex>
#include<vector>
#include "BOAdd.h"
#include "BOSub.h"
#include"BOSubInv.h"
#include"BODiv.h"
#include"BODivInv.h"
#include"BOMul.h"
#include"BOFill.h"
#include"Abs.h"
#include"Log.h"
#include"Max.h"
#include"Min.h"
#include"Pow.h"
#include"Grayscale.h"
#include"Inversion.h"
#include"Median.h"

#include"CompositeOperation.h"
#include"CompositeError.h"
#include"SavingError.h"
#include"XMLFormatter.h"
#include"LoadingError.h"
#include"LayerError.h"
#include"BMPFormatter.h"
#include"PAMFormatter.h"
#include"ReadingError.h"
#include"BlackWhite.h"
//void Image::newLayer(std::string filename)
//{
//	//layers_.push_back(format_->ReadFile);
//}
int checkVal(int n) {
	if (n < 0)n = 0;
	if (n > 255)n = 255;
	return n;
}
Image::Image()
{
	operations_["add"] = new BOAdd(); 
	operations_["sub"] = new BOSub();
	operations_["subinv"] = new BOSubInv();
	operations_["div"] = new BODiv();
	operations_["divinv"] = new BODivInv();
	operations_["mul"] = new BOMul();
	operations_["fill"] = new BOFill;
	operations_["abs"] = new Abs();
	operations_["log"] = new Log();
	operations_["max"] = new Max();
	operations_["min"] = new Min();
	operations_["pow"] = new Pow();
	operations_["grayscale"] = new Grayscale();
	operations_["inversion"] = new Inversion();
	operations_["median"] = new Median();
	operations_["blackwhite"]=new BlackWhite();
	formatters_[".bmp"] = new BMPFormatter();
	formatters_[".pam"] = new PAMFormatter();
	formatters_[".xml"]= new XMLFormatter();

	saved_ = true;
	/*std::vector<Operation*> composite;
	composite.push_back(new BOAdd());
	composite.push_back(new BOSub());
	operations_["composite"] = new CompositeOperation("kompozitna",composite, composite[0]->getNumberOfParams() + composite[1]->getNumberOfParams());*/
}
Image::~Image()
{
	deleteImage();
}
void Image::addLayer(Layer& l)
{

	if (num_of_layers_ == 0) {
		img_width_ = l.getWidth();
		img_height_ = l.getHeight();
	}
	else{
		if (l.getWidth() > img_width_) {
			//resize width for all other layers
			img_width_ = l.getWidth();
			for (auto& layer : layers_) {
				layer.resizeW(img_width_);
			}
		}
		else if (l.getWidth() < img_width_) {
			//resize width for added layer l
			l.resizeW(img_width_);
		}
		if (l.getHeight() > img_height_) {
			//resize height for all other layers
			img_height_ = l.getHeight();
			for (auto& layer : layers_) {
				layer.resizeH(img_height_);
			}

		}
		else if (l.getHeight() < img_height_) {
			//resize height for added layer l
			l.resizeH(img_height_);
		}
	}
	layers_.push_back(l);
	num_of_layers_++;
	saved_ = false;
}

void Image::addLayer(int w , int h ) {
	if (w != -1 && h != -1&&layers_.size()==0) {
		img_width_ = w;
		img_width_ = h;
	}
	else if(layers_.size()==0){
		throw LayerError("Sloju moraju biti zadati sirina i visina!");
	}
	if (w != -1 && h != -1) {
		Layer l(w, h);
		this->addLayer(l);
	}
	else {
		Layer l(img_width_, img_height_);
		this->addLayer(l);
	}

	//broj se povecava u funkciji addLayer(l);isto i saved_
}

void Image::addLayer(std::string fname)
{
	int len = fname.size();
	std::string subs=(len > 4) ? fname.substr(fname.size() - 4) : "";
	auto it = formatters_.find(subs);
	if (it == formatters_.end()) {
		throw LoadingError("Nije moguce ucitati zadati tip fajla!");
	}
	Formatter* f = formatters_[subs];
	f->loadImage(*this,fname);
	//broj se povecava u funkciji addLayer(l);isto i saved_
}

void Image::setLayerStatus(unsigned pos, bool status)
{
	
	if (pos < layers_.size())layers_[pos].setActive(status);
	else throw LayerError("Lejer na zadatom indeksu ne postoji!");
	saved_ = false;
}

void Image::setLayerVisible(unsigned pos, bool visible)
{
	if (pos < layers_.size())layers_[pos].setVisible(visible);
	else throw LayerError("Lejer na zadatom indeksu ne postoji!");
	saved_ = false;
}

void Image::setLeyerOpacity(unsigned pos, unsigned opacity)
{
	if (opacity > 100)throw LayerError("Opacity mora biti u granicama 0-100!");
	if (pos < layers_.size()) {
		layers_[pos].setOpacity(opacity);
	}
	else throw LayerError("Lejer na zadatom indeksu ne postoji!");
	saved_ = false;
}

void Image::deleteLayer(unsigned pos) {
	if (pos < layers_.size())layers_.erase(layers_.begin()+pos);
	else throw LayerError("Lejer na zadatom indeksu ne postoji!");
	saved_ = false;
}

void Image::addSelection(std::string name, std::vector<Pravougaonik>& rectangles)
{
	auto it = selections_.find(name);
	if (it != selections_.end()) {
		throw SelectionError("Selekcija vec postoji!");
	}
	for (auto& p : rectangles) {
		if ((p.getStartX() + p.getWidth()) > img_width_ || p.getStartX() < 0 || p.getWidth() < 0
			|| (p.getStartY() + p.getHeight()) > img_height_ || p.getStartY() < 0 || p.getHeight() < 0)
			throw SelectionError("Neispravni argumenti pri zadavanju selekcije, proverite da ne izlazite van granica slike!");
	}
	selections_[name] = Selection(name, rectangles);
}

void Image::deleteSelection(std::string name) {
	auto it = selections_.find(name);
	if (it == selections_.end()) {
		throw SelectionError("Selekcija sa zadatim imenom ne postoji!");
	}
	if (selections_[name].isActive()) {
		deactivateSelection(name);
	}
	selections_.erase(name);


}



void Image::setSelectionStatus(std::string name, bool status) {
	auto it = selections_.find(name);
	if (it == selections_.end()) {
		throw SelectionError("Selekcija nije pronadjena!");
	}
	if (status) { 
		if (!it->second.isActive()) {
			activateSelection(name);
			it->second.setActive(true);
		}
	}
	else {
		if (it->second.isActive()) {
			deactivateSelection(name);
			it->second.setActive(false);
		}
	}

}

void Image::activateSelection(std::string name)
{
	Selection* s = &(selections_[name]);
	std::for_each(s->begin(), s->end(), [this](Pravougaonik& p) {
		std::map<std::pair<unsigned, unsigned>, unsigned>& sp = this->getSelected();
		unsigned sx = p.getStartX(), sy = p.getStartY();
		unsigned ex = sx + p.getWidth(), ey = sy + p.getHeight();
		for (unsigned i = sy; i < ey; i++)
			for (unsigned j = sx; j < ex; j++) {
				sp[{i, j}]++;
			}
		});
}

void Image::deactivateSelection(std::string name) {
	Selection* s = &(selections_[name]);
	std::for_each(s->begin(), s->end(), [this](Pravougaonik& p) {
		std::map<std::pair<unsigned, unsigned>, unsigned>& sp = this->getSelected();
		unsigned sx = p.getStartX(), sy = p.getStartY();
		unsigned ex = sx + p.getWidth(), ey = sy + p.getHeight();
		for (unsigned i = sy; i < ey; i++)
			for (unsigned j = sx; j < ex; j++) {
				sp[{i, j}]--;
				if (sp[{i, j}] == 0) sp.erase({i,j});
			}
		});
}


void Image::createCompositeOperation(std::string name, std::string operation)
{
	std::vector<Operation*>operatinos;
	std::regex rx("[\\|]");
	std::sregex_token_iterator iter(operation.begin(), operation.end(), rx, -1);
	std::sregex_token_iterator end;
	int n = 0;
	std::string s;
	for (; iter != end; ++iter) {
		s = *iter;
		auto it = operations_.find(s);
		Operation* o = nullptr;
		if (it != operations_.end()) {
			o = operations_[s];
			n += o->getNumberOfParams();
			operatinos.push_back(o);
		}
		else throw CompositeError();
	}
	operations_[name] = new CompositeOperation(name,operatinos,n);
}

void Image::exportCompositeOperation(std::string name, std::string path)
{
	XMLFormatter xmlf;
	Operation* o = nullptr;
	auto it = operations_.find(name);
	if (it != operations_.end()) {
		o = operations_[name];
		if (typeid(*o) != typeid(CompositeOperation))throw CompositeError("Uneta operacija nije kompozitna");
		xmlf.saveFun(path, dynamic_cast<CompositeOperation*>(o));
	}
	else {
		throw CompositeError();
	}	
}

void Image::importCompositeOperation(std::string name, std::string path)
{
	XMLFormatter xmlf;
	auto it = operations_.find(name);
	if (it != operations_.end()) throw LoadingError("Operacija sa tim imenom vec postoji!");
	createCompositeOperation(name,xmlf.loadFun(path));
}

bool Image::ExecuteOperation(std::string cmd)
{
	//operacija se zadaje u formatu npr.> fill,255 0 0;
	std::string command;
	std::string params;
	std::regex rx("[ ]*([^ ]*)[ ]*([^;]*)[;| ]*");
	std::smatch result;
	if (std::regex_match(cmd, result, rx)) {
		command = result.str(1);
		params = result.str(2);
	}
	else {
		throw ParamsError();
	}
	auto it = operations_.find(command);
	Operation* o = nullptr;
	if (it != operations_.end()) {
		o = operations_[command];
	}
	else {
		throw ParamsError("Ne postoji uneta operacija!");
	}

	o->ValidateAndSetParams(params);
	std::vector<int> layer_vector;
	int w, h;
	for (auto& l : layers_) {
		if (l.isActive()) {
			w = l.getWidth();
			h = l.getHeight();
			for (auto& p : l) {
				layer_vector.push_back(p.getRed());
				layer_vector.push_back(p.getGreen());
				layer_vector.push_back(p.getBlue());
			}
			layer_vector.push_back(w);
			layer_vector.push_back(h);
			o->Execute(layer_vector, slected_pixels_);
			layer_vector.pop_back();//remove height
			layer_vector.pop_back();//remove width
			auto it = layer_vector.begin();
			if (slected_pixels_.size()) {
				std::for_each(slected_pixels_.begin(), slected_pixels_.end(), [&l, &layer_vector, w](const std::pair<std::pair<unsigned, unsigned>, unsigned>& tek) {
					int pos = tek.first.first * w + tek.first.second;
					l[pos].setRed(checkVal(layer_vector[3 * pos]));
					l[pos].setGreen(checkVal(layer_vector[3 * pos + 1]));
					l[pos].setBlue(checkVal(layer_vector[3 * pos + 2]));
					});
			}
			else {
				int pos = 0;
				for (auto& p : l) {
					p.setRed(checkVal(layer_vector[pos++]));
					p.setGreen(checkVal(layer_vector[pos++]));
					p.setBlue(checkVal(layer_vector[pos++]));
				}
			}
			layer_vector.clear();
		}
	}
	saved_ = false;
	return true;
}

Layer Image::compressIntoOneLayer()
{
	Layer compressed_(img_width_, img_height_);
	bool first = true;
	for (auto& l : layers_) {
		if (l.isVisible()) {
			if (first) {
				int j = 0;
				for (auto& p : l) {
					compressed_[j].setRed(p.getRed());
					compressed_[j].setGreen(p.getGreen());
					compressed_[j].setBlue(p.getBlue());
					compressed_[j].setAlpha((uint8_t)round(p.getAlpha() *(double)l.getOpacity()/100));
					j++;
				}

				first = false;
			}
			else {
				int j = 0;
				for (auto& p : l) {
					compressed_[j].setRed((uint8_t)(((float)l.getOpacity() / 100) * (((float)p.getAlpha()) / 255) * p.getRed() + (1 - ((float)l.getOpacity() / 100) * ((float)p.getAlpha()) / 255) * compressed_[j].getRed()));
					compressed_[j].setGreen((uint8_t)(((float)l.getOpacity() / 100) * (((float)p.getAlpha()) / 255) * p.getGreen() + (1 - ((float)l.getOpacity() / 100) * ((float)p.getAlpha()) / 255) * compressed_[j].getGreen()));
					//compressed_[j].setBlue((uint8_t)((float)l.getOpacity() / 100 * p.getBlue() + (1 - ((float)l.getOpacity() / 100) * ((float)p.getAlpha()) / 255) * compressed_[j].getBlue()));
					compressed_[j].setBlue((uint8_t)(((float)l.getOpacity() / 100) * (((float)p.getAlpha()) / 255) * p.getBlue() + (1 - ((float)l.getOpacity() / 100) * ((float)p.getAlpha()) / 255) * compressed_[j].getBlue()));
					//compressed_[j].setAlpha(255);
					double a = ((float)l.getOpacity()) / 100 * ((float)p.getAlpha()) + (1 - ((float)l.getOpacity() / 100) * ((float)p.getAlpha()) / 255) * compressed_[j].getAlpha();
					if (compressed_[j].getAlpha() < 255) {
						if (a > 255)a = 255;
						else a = round(a);
						compressed_[j].setAlpha((uint8_t)a);
					}
					j++;
				}
			}
		}
	}
	saved_ = true;
	return compressed_;
}

void Image::loadImage(const std::string& path)
{
	XMLFormatter xmlf;
	Image* img = xmlf.loadImage(path);
	if (img) {
		deleteImage();
		*this = *img;
	}
	saved_ = false;
}

void Image::SaveImage(const std::string& path, std::string fname) {

	int len = fname.size();
	std::string subs = (len > 4) ? fname.substr(fname.size() - 4) : "";
	auto it = formatters_.find(subs);
	if (it == formatters_.end()) {
		throw SavingError("Greska pri cuvanju fajla!");
	}
	Formatter *f=formatters_[subs];
	f->saveImage(*this, path, fname.substr(0, fname.size()-4));
	saved_ = true;
}

unsigned Image::numofparams(std::string name)
{
	auto it = operations_.find(name);
	Operation* o = nullptr;
	if (it != operations_.end()) {
		return operations_[name]->getNumberOfParams();
	}
	return -1;
}



void Image::deleteImage()
{

	layers_.clear();
	selections_.clear();
	for (auto& it : operations_) {
		delete it.second;
	}
	for (auto& it : formatters_) {
		delete it.second;
	}
	operations_.clear();
	slected_pixels_.clear();
	formatters_.clear();
	img_width_ = 0;
	img_height_ = 0;
	num_of_layers_ = 0;
	saved_ = true;
}

