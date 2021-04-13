#pragma once
#ifndef SavingError_HPP_
#define SavingError_HPP_
#include<iostream>
#include <exception>
class SavingError
{
public:
	SavingError(std::string s = "Greska pri cuvanju!") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const SavingError& se) {
		return it << se.message_error_;
	}
private:
	std::string message_error_;
};
#endif