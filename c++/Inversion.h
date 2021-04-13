#pragma once
#ifndef INVERSION_HPP_
#define INVERSION_HPP_
#include "Operation.h"
class Inversion :
	public Operation
{
public:
	Inversion();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
};
#endif

