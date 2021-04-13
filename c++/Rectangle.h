#pragma once
#ifndef RECTANGLE_HPP_
#define RECTANGLE_HPP_

class Pravougaonik
{
public:
	Pravougaonik() :start_x_(0), start_y_(0), width_(0), height_(0) {}
	Pravougaonik(unsigned starty, unsigned startx, unsigned width, unsigned height);

	//using iterator = std::vector<Pravougaonik>::iterator;
	//using const_iterator = std::vector<Pravougaonik>::const_iterator;

	//Pravougaonik& operator[] (int p) { return selection_[p]; };
	//const Pravougaonik& operator[] (int p)const { return selection_[p]; };



	//void push_back(const Pravougaonik& p) { selection_.push_back(p); }



	//iterator begin() { return selection_.begin(); }
	//iterator end() { return selection_.end(); }

	//const_iterator begin() const { return selection_.cbegin(); }
	//const_iterator end() const { return selection_.cend(); }

	unsigned getStartX()const { return start_x_; };
	unsigned getStartY()const { return start_y_; };
	unsigned getWidth() const { return width_; };
	unsigned getHeight() const { return height_; };

private:
	unsigned start_x_;//left corner
	unsigned start_y_;
	unsigned width_;
	unsigned height_;
};

#endif

