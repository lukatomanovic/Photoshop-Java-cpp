
#pragma once
#ifndef LayerError_HPP_
#define LayerError_HPP_

#include<iostream>
#include <exception>
class LayerError
{
public:
	LayerError(std::string s = "") :message_error_(s) {};
	friend std::ostream& operator<<(std::ostream& it, const LayerError& le) {
		return it << le.message_error_;
	}
private:
	std::string message_error_;//error message
};

#endif
