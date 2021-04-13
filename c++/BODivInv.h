#pragma once
#ifndef BODIVINV_HPP_
#define BODIVINV_HPP_
#include "Operation.h"
class BODivInv :
	public Operation
{
public:
	BODivInv();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
private:
	unsigned constant_num_;
};
#endif

