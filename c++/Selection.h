#pragma once
#ifndef SELECTION_HPP_
#define SELECTION_HPP_

#include <vector>
#include<string>
#include "Rectangle.h"




class Selection
{
public:
	Selection() = default;//zbog operatora [] u map 
	Selection(std::string name, std::vector<Pravougaonik>& selection);

	bool isActive()const { return active_; }
	void setActive(bool activate) { active_ = activate; }


	using iterator = std::vector<Pravougaonik>::iterator;
	using const_iterator = std::vector<Pravougaonik>::const_iterator;

	Pravougaonik& operator[] (int p) { return selection_[p]; };
	const Pravougaonik& operator[] (int p)const { return selection_[p]; };



	void push_back(const Pravougaonik& p) { selection_.push_back(p); }



	iterator begin() { return selection_.begin(); }
	iterator end() { return selection_.end(); }

	const_iterator begin() const { return selection_.cbegin(); }
	const_iterator end() const { return selection_.cend(); }

private:
	std::string name_;
	std::vector<Pravougaonik> selection_;
	bool active_;
};
#endif

