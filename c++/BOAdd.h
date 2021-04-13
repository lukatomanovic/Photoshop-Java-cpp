#pragma once
#ifndef BOADD_HPP_
#define BOADD_HPP_
#include "Operation.h"
class BOAdd :
	public Operation
{
public:
	BOAdd();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
private:
	unsigned constant_num_;
};
#endif

