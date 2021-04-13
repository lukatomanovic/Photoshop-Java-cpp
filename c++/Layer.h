#pragma once
#ifndef LAYER_HPP_
#define LAYER_HPP_

#include"Pixel.h"
#include<vector>
#include<iostream>
#include<windows.h>
#include <algorithm> 



#define _W 500
#define _H 300

class Layer
{
public:
	Layer(int width_,int height_);
	Layer()=default;

	unsigned getWidth()const { return width_; }
	unsigned getHeight()const { return height_; }
	uint8_t getOpacity()const { return opacity_; }
	bool isActive()const { return active_; }
	bool isVisible()const { return visible_; }

	void setLayer(std::vector<Pixel>& l) {layer_ = l; };
	void setWidth(unsigned w) { width_ = w; }
	void setHeight(unsigned h) { height_ = h; }
	void setOpacity(uint8_t o) { opacity_ = o; }
	void setActive(bool val) { active_ = val; }
	void setVisible(bool val) { visible_ = val; }

	void resizeW(unsigned new_width);
	void resizeH(unsigned new_height);

	using iterator = std::vector<Pixel>::iterator;
	using const_iterator = std::vector<Pixel>::const_iterator;

	Pixel& operator[] (int p) { return layer_[p]; };
	const Pixel& operator[] (int p)const { return layer_[p]; };

	friend std::ostream& operator<<(std::ostream& os, const Layer& l);
	
	void push_back(const Pixel & p) {layer_.push_back(p);}



	iterator begin() { return layer_.begin(); }
	iterator end()  { return layer_.end(); }

	const_iterator begin() const { return layer_.cbegin(); }
	const_iterator end() const { return layer_.cend(); }

	//const_iterator begin() const { return layer_.cbegin(); }
	//const_iterator end() const { return layer_.cend(); }


private:
	//std::map<std::pair<unsigned,unsigned>, Pixel> layer_;
	std::vector<Pixel> layer_;
	unsigned width_, height_;
	bool active_;
	bool visible_;
	uint8_t opacity_=100;
};

#endif