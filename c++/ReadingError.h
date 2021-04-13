#pragma once
#ifndef ReadingError_HPP_
#define ReadingError_HPP_

#include<iostream>
#include <exception>
class ReadingError
{
public:
	ReadingError(std::string s = "Greska pri citanju") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const ReadingError& pe) {
		return it << pe.message_error_;
	}
private:
	std::string message_error_;
};

#endif