#pragma once
#ifndef ParamsError_HPP_
#define ParamsError_HPP_

#include<iostream>
#include <exception>
class ParamsError
{
public:
	ParamsError(std::string s= "Neispravni argumenti") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const ParamsError& pe) {
		return it<<pe.message_error_;
	}
private:
	std::string message_error_;
};

#endif
