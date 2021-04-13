#pragma once
#ifndef BODIV_HPP_
#define BODIV_HPP_
#include "Operation.h"
class BODiv :
	public Operation
{

public:
	BODiv();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
private:
	unsigned constant_num_;
};
#endif



