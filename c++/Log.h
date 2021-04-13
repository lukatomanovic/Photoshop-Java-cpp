#pragma once
#ifndef LOG_HPP_
#define LOG_HPP_
#include "Operation.h"
class Log :
	public Operation
{
public:
	Log();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
};

#endif

