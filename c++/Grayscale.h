#pragma once
#ifndef GRAYSCALE_HPP_
#define GRAYSCALE_HPP_
#include "Operation.h"
class Grayscale :
	public Operation
{
public:
	Grayscale();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
};
#endif

