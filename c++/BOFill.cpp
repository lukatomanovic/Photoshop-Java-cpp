#include "BOFill.h"
#include<regex>
#include "ParamsError.h"


BOFill::BOFill() :Operation("fill",3), R_(0),G_(0),B_(0)
{
}

bool BOFill::ValidateAndSetParams(std::string params)
{
	std::regex rx ("[ ]*([0-9]+)[,| ]([0-9]+)[,| ]([0-9]+)[,| ]*");
	std::smatch result;
	if (std::regex_match(params, result, rx)) {
		//nije potrebno poredjenje na manje od 0, regex bi vratio false ako bi naisao na -
		std::string param = result.str(1);
		R_ = atoi(param.c_str());
		//if(R>255)throw ParamsError();

		param = result.str(2);
		G_ = atoi(param.c_str());
		//if (G > 255)throw ParamsError();

		param = result.str(3);
		B_ = atoi(param.c_str());
		//if (B > 255)throw ParamsError();
	}
	else {
		throw ParamsError();
		return false; //i ne mora
	}
	params_set_ = true;
	return true;
}

void BOFill::Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_)
{
	int w = layer[layer.size() - 2];
	if (selected_pixels_.size()) {
		std::for_each(selected_pixels_.begin(), selected_pixels_.end(), [&layer, w, this](const std::pair<std::pair<unsigned, unsigned>, unsigned>& tek) {
			int start_position = (tek.first.first * w + tek.first.second) * 3;
			layer[start_position] = R_;
			layer[start_position + 1] = G_;
			layer[start_position + 2] = B_;
			});
	}
	else {
		int h = layer[layer.size() - 1];
		for (int i = 0; i < w * h; i++) {
			int start_position = i * 3;
			layer[start_position] = R_;
			layer[start_position + 1] = G_;
			layer[start_position + 2] = B_;
		}
	}
	params_set_ = false;
}


