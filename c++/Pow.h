#pragma once
#ifndef POW_HPP_
#define POW_HPP_
#include "Operation.h"
class Pow :
	public Operation
{
public:
	Pow();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
private:
	unsigned constant_num_;
};
#endif

