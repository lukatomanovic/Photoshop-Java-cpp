#include "Grayscale.h"
#include<cmath>
#include "ParamsError.h"
#include<regex>



Grayscale::Grayscale() :Operation("grayscale", 0)
{
}


bool Grayscale::ValidateAndSetParams(std::string params)
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

void Grayscale::Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_)
{
	int w = layer[layer.size() - 2];
	if (selected_pixels_.size()) {
		std::for_each(selected_pixels_.begin(), selected_pixels_.end(), [&layer, w, this](const std::pair<std::pair<unsigned, unsigned>, unsigned>& tek) {
			int start_position = (tek.first.first * w + tek.first.second) * 3;
			double asr = 0;
			asr += layer[start_position];
			asr += layer[start_position + 1];
			asr += layer[start_position + 2];
			asr /= 3;
			layer[start_position] = asr;
			layer[start_position + 1] = asr;
			layer[start_position + 2] = asr;
			});
	}
	else {
		int h = layer[layer.size() - 1];
		for (int i = 0; i < w * h; i++) {
			int start_position = i * 3;
			double asr = 0;
			asr += layer[start_position];
			asr += layer[start_position + 1];
			asr += layer[start_position + 2];
			asr /= 3;
			layer[start_position] = asr;
			layer[start_position + 1] = asr;
			layer[start_position + 2] = asr;
		}
	}
	params_set_ = false;
}