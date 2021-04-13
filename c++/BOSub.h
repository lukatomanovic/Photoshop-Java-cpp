#pragma once
#ifndef BOSUB_HPP_
#define BOSUB_HPP_
#include "Operation.h"
class BOSub :
	public Operation
{
public:
	BOSub();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
private:
	unsigned constant_num_;
};
#endif
