#include "BOSub.h"
#include "ParamsError.h"
#include<regex>



BOSub::BOSub() :Operation("sub",1),constant_num_(0)
{
}


bool BOSub::ValidateAndSetParams(std::string params)
{
	std::regex rx("[ ]*([0-9]+)[ ]*");
	std::smatch result;
	if (std::regex_match(params, result, rx)) {
		//nije potrebno poredjenje na manje od 0, regex bi vratio false ako bi naisao na -
		std::string param = result.str(1);
		constant_num_ = atoi(param.c_str());
	}
	else {
		throw ParamsError();
		params_set_ = false;
		return false; //i ne mora
	}
	params_set_ = true;
	return true;
}

void BOSub::Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_)
{
	int w = layer[layer.size() - 2];
	if (selected_pixels_.size()) {
		std::for_each(selected_pixels_.begin(), selected_pixels_.end(), [&layer, w, this](const std::pair<std::pair<unsigned, unsigned>, unsigned>& tek) {
			int start_position = (tek.first.first * w + tek.first.second) * 3;
			layer[start_position] -= constant_num_;
			layer[start_position + 1] -= constant_num_;
			layer[start_position + 2] -= constant_num_;
			});
	}
	else {
		int h = layer[layer.size() - 1];
		for_each(layer.begin(), layer.end(), [this](int& color) {color -= constant_num_; });
		layer[layer.size() - 2] = w;
		layer[layer.size() - 1] = h;
	}
	params_set_ = false;
}
