#pragma once
#ifndef COMPOSITEOPERATION_HPP_
#define COMPOSITEOPERATION_HPP_
#include "Operation.h"
class CompositeOperation :
	public Operation
{
public:
	CompositeOperation(std::string, std::vector<Operation*> op,int n);
	virtual ~CompositeOperation();
	bool ValidateAndSetParams(std::string params);
	void Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_);
	std::vector<std::string> getDescription()const;
private:
	std::vector<Operation*> basic_operations_;

};

#endif