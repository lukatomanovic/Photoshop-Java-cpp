

#pragma once
#ifndef BlackWhite_HPP_
#define BlackWhite_HPP_
#include "Operation.h"
class BlackWhite :
	public Operation
{
public:
	BlackWhite();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
};
#endif

