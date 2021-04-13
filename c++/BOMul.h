#pragma once
#ifndef BOMUL_HPP_
#define BOMUL_HPP_
#include "Operation.h"
class BOMul :
	public Operation
{
public:
	BOMul();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
private:
	unsigned constant_num_;
};
#endif
