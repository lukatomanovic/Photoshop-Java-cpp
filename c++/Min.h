#pragma once
#ifndef MIN_HPP_
#define MIN_HPP_
#include "Operation.h"
class Min :
	public Operation
{
public:
	Min();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
private:
	unsigned constant_num_;
};
#endif

