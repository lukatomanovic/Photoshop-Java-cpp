#include "Operation.h"
#include<regex>
#include<iostream>
std::vector<int> Operation::ParamsParserToIntVect(std::string params)
{
	std::vector<int>tmp_params_vector;
	std::regex rx("[ ]+");
	std::sregex_token_iterator iter(params.begin(),params.end(), rx, -1);
	std::sregex_token_iterator end;
	std::string s;
	for (; iter != end; ++iter) {
		 s= *iter;
		tmp_params_vector.push_back(atoi(s.c_str()));
	}
	return tmp_params_vector;
}
