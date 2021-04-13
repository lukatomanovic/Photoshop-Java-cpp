#pragma once
#ifndef SelectionError_HPP_
#define SelectionError_HPP_

#include<iostream>
#include <exception>
class SelectionError
{
public:
	SelectionError(std::string s="") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const SelectionError& se) {
		return it <<se.message_error_;
	}
private:
	std::string message_error_;
};

#endif
