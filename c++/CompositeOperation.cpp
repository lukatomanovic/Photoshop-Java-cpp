#include "CompositeOperation.h"
#include "ParamsError.h"
#include <regex>

CompositeOperation::CompositeOperation(std::string name,std::vector<Operation*> op,int n):Operation(name,n),basic_operations_(op)
{
}

CompositeOperation::~CompositeOperation()
{
	//for (auto& bo : basic_operations_) {
	//	delete bo;
	//}
	basic_operations_.clear();
}

bool CompositeOperation::ValidateAndSetParams(std::string params)
{
	int i = 0;
	std::string exp = "";
	int num_of_operations = basic_operations_.size();
	std::string tmp_params = "";
	try {
		for (auto bo : basic_operations_) {
			exp = "[,| ]*";
			int n = bo->getNumberOfParams();
			if (n) {
				exp += "([^,]*";
				n--;
				while (n) {
					exp += ",[^,]*";
					n--;
				}
				exp += ")(.*)";
				std::regex rx(exp);
				std::smatch result;
				if (std::regex_match(params, result, rx)) {
					tmp_params = result.str(1);
					params = result.str(2);
				}
				else {
					throw ParamsError("Neispravni argumenti operacije!");
				}
			}
			else {
				tmp_params = "";
			}
			bo->ValidateAndSetParams(tmp_params);
		}
	}
	catch (ParamsError& pe) {
		throw pe;
	}
	return true;
}

void CompositeOperation::Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_)
{
	for (auto bo : basic_operations_) {
		bo->Execute(layer, selected_pixels_);
	}
}

std::vector<std::string> CompositeOperation::getDescription() const
{
	std::vector<std::string> description;
	for (auto bo : basic_operations_) {
		description.push_back(bo->getName());
	}
	return description;
}
