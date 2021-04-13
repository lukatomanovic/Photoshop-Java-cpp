#include "Abs.h"
#include<cmath>
#include "ParamsError.h"
#include<regex>
#include<algorithm>


Abs::Abs() :Operation("abs",0)
{
}


bool Abs::ValidateAndSetParams(std::string params)
{
	std::regex rx("[,| ]*");
	std::smatch result;
	if (std::regex_match(params, result, rx)) {
		params_set_ = true;
		return true;
	}
	else {
		throw ParamsError();
		params_set_ = false;
		return false; //i ne mora
	}

}

void Abs::Execute(std::vector<int>& layer,const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_)
{
	int w = layer[layer.size() - 2];
	if (selected_pixels_.size()) {
		std::for_each(selected_pixels_.begin(), selected_pixels_.end(), [&layer, w](const std::pair<std::pair<unsigned, unsigned>, unsigned>& tek) {
			int start_position = (tek.first.first * w + tek.first.second) * 3;
			layer[start_position] = abs(layer[start_position]);
			layer[start_position + 1] = abs(layer[start_position + 1]);
			layer[start_position + 2] = abs(layer[start_position + 2]);
			});
	}
	else {
		int h= layer[layer.size() - 1];
		for_each(layer.begin(), layer.end(), [this](int& color) {color = abs(color); });
		layer[layer.size() - 2] = w;
		layer[layer.size() - 1] = h;
	}
	params_set_ = false;
}

