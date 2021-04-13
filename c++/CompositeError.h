#pragma once
#ifndef CompositeError_HPP_
#define CompositeError_HPP_

#include<iostream>
#include <exception>
class CompositeError
{
public:
	CompositeError(std::string s = "Greska kompozitne operacije") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const CompositeError&ce) {
		return it << ce.message_error_ << std::endl;
	}
private:
	std::string message_error_;
};
#endif