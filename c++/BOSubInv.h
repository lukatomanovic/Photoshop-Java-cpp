#pragma once
#ifndef BOSUBINV_HPP_
#define BOSUBINV_HPP_
#include "Operation.h"
class BOSubInv :
	public Operation
{
public:
	BOSubInv();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
private:
	unsigned constant_num_;
};
#endif
