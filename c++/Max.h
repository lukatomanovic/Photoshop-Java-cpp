#pragma once
#ifndef MAX_HPP_
#define MAX_HPP_
#include "Operation.h"
class Max :
	public Operation
{
public:
	Max();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
private:
	unsigned constant_num_;
};
#endif

