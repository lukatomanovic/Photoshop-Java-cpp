#pragma once
#ifndef LoadingError_HPP_
#define LoadingError_HPP_

#include<iostream>
#include <exception>
class LoadingError
{
public:
	LoadingError(std::string s = "") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const LoadingError& le) {
		return it << le.message_error_;
	}
private:
	std::string message_error_;
};

#endif
