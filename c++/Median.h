#pragma once
#ifndef MEDIAN_HPP_
#define MEDIAN_HPP_
#include "Operation.h"
class Median :
	public Operation
{
public:
	Median();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
private:
	//ako se koristi process operacija je duplo sporija
	void process(std::vector<int>& layer, std::vector<int>& layer_copy, unsigned width,unsigned height, unsigned startpos);
};
#endif

