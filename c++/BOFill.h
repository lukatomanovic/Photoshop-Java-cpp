#pragma once
#ifndef BOFILL_HPP_
#define BOFILL_HPP_

#include "Operation.h"
#include <vector>

class BOFill :
	public Operation
{
public:
	BOFill();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_);
private:
	int R_, G_, B_;
};

#endif