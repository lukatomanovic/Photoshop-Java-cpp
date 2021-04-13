#include "Layer.h"

Layer::Layer(int width, int height):width_(width),height_(height),active_(true),opacity_(0),visible_(true)
{
	for (int i = 0; i < width * height; i++)layer_.push_back(Pixel(0, 0, 0, 255));
}

void Layer::resizeW(unsigned new_width)
{

	for (int i = 0; i < height_;i++) {
		unsigned start_row_position =i*new_width+ width_;
		for (int j = width_; j < new_width; j++) {
			layer_.insert(layer_.begin()+start_row_position,Pixel(0, 0, 0, 0));
		}
	}
	width_ = new_width;
}

void Layer::resizeH(unsigned new_height)
{
	unsigned start_position = width_*height_;
	for (int i = 0; i < (new_height-height_); i++) {
		for (int j = 0; j < width_; j++) {
			layer_.push_back(Pixel(0, 0, 0, 0));
		}
	}
	height_ = new_height;
}

std::ostream& operator<<(std::ostream& os, const Layer& l)
{

	HWND myconsole = GetConsoleWindow();
	HDC mydc = GetDC(myconsole);
	int i = 0;
	for (auto p  : l) {
		COLORREF COLOR = RGB(p.getRed(), p.getGreen(), p.getBlue());
		SetPixel(mydc,  i%l.width_,i/l.width_, COLOR);
		i++;
	}
	ReleaseDC(myconsole, mydc);
	std::cin.ignore();
	return os;

}
