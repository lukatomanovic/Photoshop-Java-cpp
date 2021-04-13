#pragma once
#ifndef OPERATION_HPP_
#define OPERATION_HPP_

#include"Pixel.h"
#include<string>
#include<vector>
#include<map>
//abstract
class Layer;
class Operation
{
public:
	Operation(int n) :params_set_(false),number_of_params_(n) {}
	Operation(std::string name,int n) :name_(name),params_set_(false), number_of_params_(n) {}
	virtual bool ValidateAndSetParams(std::string params) = 0;
	/*if you use operations like mediana is necesary to pass layer parameter*/
	virtual void Execute(std::vector<int>& pixels,const std::map<std::pair<unsigned, unsigned>, unsigned>& slected_pixels_)=0;
	int getNumberOfParams()const { return number_of_params_; }
	std::string getName()const { return name_; }
	virtual ~Operation() {};
	static std::vector<int> ParamsParserToIntVect(std::string params);
protected:
	bool params_set_;
	const int number_of_params_;
	std::string name_;
};
#endif
