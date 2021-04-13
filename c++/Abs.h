#pragma once
#ifndef ABS_HPP_
#define ABS_HPP_
#include "Operation.h"
class Abs :
	public Operation
{
public:
	Abs();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer,const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
};
#endif

