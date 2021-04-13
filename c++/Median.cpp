#include "Median.h"
#include<cmath>
#include "ParamsError.h"
#include<regex>
#include"Layer.h"



Median::Median() :Operation("median", 0)
{
}


bool Median::ValidateAndSetParams(std::string params)
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

void Median::Execute(std::vector<int>& layer, const std::map<std::pair<unsigned, unsigned>, unsigned>& selected_pixels_)
{
	//ne pozivam proccess usluznu funkciju jer je u tom slucaju operacija duplo sporija
	int w = layer[layer.size() - 2];
	int h = layer[layer.size() - 1];
	std::vector<uint8_t>Rarr;
	std::vector<uint8_t>Garr;
	std::vector<uint8_t>Barr;
	std::vector<int>layer_copy = layer;
	if (selected_pixels_.size() != 0) {
		std::for_each(selected_pixels_.begin(), selected_pixels_.end(), [&](const std::pair<std::pair<unsigned, unsigned>, unsigned>& tek) {
			int start_position = (tek.first.first * w + tek.first.second) * 3;
			Rarr.push_back(layer[start_position]);
			Garr.push_back(layer[start_position + 1]);
			Barr.push_back(layer[start_position + 2]);
			if (tek.first.first > 0) {
				//pixel above the curr with offset -width of layer
				Rarr.push_back(layer_copy[start_position - w * 3]);
				Garr.push_back(layer_copy[start_position + 1 - w * 3]);
				Barr.push_back(layer_copy[start_position + 2 - w * 3]);
			}
			if (tek.first.first < h - 1) {
				//pixel below the curr with offset -width of layer
				Rarr.push_back(layer_copy[start_position + w * 3]);
				Garr.push_back(layer_copy[start_position + 1 + w * 3]);
				Barr.push_back(layer_copy[start_position + 2 + w * 3]);
			}
			if (tek.first.second > 0) {
				//left pixel
				Rarr.push_back(layer_copy[start_position - 3]);
				Garr.push_back(layer_copy[start_position + 1 - 3]);
				Barr.push_back(layer_copy[start_position + 2 - 3]);
			}
			if (tek.first.second < w - 1) {
				//right pixel
				Rarr.push_back(layer_copy[start_position + 3]);
				Garr.push_back(layer_copy[start_position + 1 + 3]);
				Barr.push_back(layer_copy[start_position + 2 + 3]);
			}

			std::sort(Rarr.begin(), Rarr.end());
			std::sort(Garr.begin(), Garr.end());
			std::sort(Barr.begin(), Barr.end());
			int number_of_neighbours_and_me = Rarr.size();//same for all Rarr, Garr, Barr
			if (number_of_neighbours_and_me % 2 == 0) {
				layer[start_position] = (Rarr[number_of_neighbours_and_me / 2] + Rarr[number_of_neighbours_and_me / 2 - 1]) / 2;
				layer[start_position + 1] = (Garr[number_of_neighbours_and_me / 2] + Garr[number_of_neighbours_and_me / 2 - 1]) / 2;
				layer[start_position + 2] = (Barr[number_of_neighbours_and_me / 2] + Barr[number_of_neighbours_and_me / 2 - 1]) / 2;
			}
			else {
				layer[start_position] = Rarr[number_of_neighbours_and_me / 2];
				layer[start_position + 1] = Garr[number_of_neighbours_and_me / 2];
				layer[start_position + 2] = Barr[number_of_neighbours_and_me / 2];
			}
			Rarr.clear();
			Garr.clear();
			Barr.clear();


			});
	}
	else {
		for (int i = 0; i < w * h; i++) {
			int start_position = i * 3;
			Rarr.push_back(layer[start_position]);
			Garr.push_back(layer[start_position + 1]);
			Barr.push_back(layer[start_position + 2]);
			if (i/w > 0) {
				//pixel above the curr with offset -width of layer
				Rarr.push_back(layer_copy[start_position - w * 3]);
				Garr.push_back(layer_copy[start_position + 1 - w * 3]);
				Barr.push_back(layer_copy[start_position + 2 - w * 3]);
			}
			if (i/w < (h-1)) {
				//pixel below the curr with offset -width of layer
				Rarr.push_back(layer_copy[start_position + w * 3]);
				Garr.push_back(layer_copy[start_position + 1 + w * 3]);
				Barr.push_back(layer_copy[start_position + 2 + w * 3]);
			}
			if (i%w > 0) {
				//left pixel
				Rarr.push_back(layer_copy[start_position - 3]);
				Garr.push_back(layer_copy[start_position + 1 - 3]);
				Barr.push_back(layer_copy[start_position + 2 - 3]);
			}
			if (i%w < w - 1) {
				//right pixel
				Rarr.push_back(layer_copy[start_position + 3]);
				Garr.push_back(layer_copy[start_position + 1 + 3]);
				Barr.push_back(layer_copy[start_position + 2 + 3]);
			}

			std::sort(Rarr.begin(), Rarr.end());
			std::sort(Garr.begin(), Garr.end());
			std::sort(Barr.begin(), Barr.end());
			int number_of_neighbours_and_me = Rarr.size();//same for all Rarr, Garr, Barr
			if (number_of_neighbours_and_me % 2 == 0) {
				layer[start_position] = (Rarr[number_of_neighbours_and_me / 2] + Rarr[number_of_neighbours_and_me / 2 - 1]) / 2;
				layer[start_position + 1] = (Garr[number_of_neighbours_and_me / 2] + Garr[number_of_neighbours_and_me / 2 - 1]) / 2;
				layer[start_position + 2] = (Barr[number_of_neighbours_and_me / 2] + Barr[number_of_neighbours_and_me / 2 - 1]) / 2;
			}
			else {
				layer[start_position] = Rarr[number_of_neighbours_and_me / 2];
				layer[start_position + 1] = Garr[number_of_neighbours_and_me / 2];
				layer[start_position + 2] = Barr[number_of_neighbours_and_me / 2];
			}
			Rarr.clear();
			Garr.clear();
			Barr.clear();

		}
	}
	layer_copy.clear();
	params_set_ = false;

}

void Median::process(std::vector<int>& layer, std::vector<int>& layer_copy, unsigned w, unsigned h, unsigned start_position)
{
	std::vector<uint8_t>Rarr;
	std::vector<uint8_t>Garr;
	std::vector<uint8_t>Barr;
	Rarr.push_back(layer[start_position]);
	Garr.push_back(layer[start_position + 1]);
	Barr.push_back(layer[start_position + 2]);
	int i = start_position / 3;
	if (i / w > 0) {
		//pixel above the curr with offset -width of layer
		Rarr.push_back(layer_copy[start_position - w * 3]);
		Garr.push_back(layer_copy[start_position + 1 - w * 3]);
		Barr.push_back(layer_copy[start_position + 2 - w * 3]);
	}
	if (i / w < (h - 1)) {
		//pixel below the curr with offset -width of layer
		Rarr.push_back(layer_copy[start_position + w * 3]);
		Garr.push_back(layer_copy[start_position + 1 + w * 3]);
		Barr.push_back(layer_copy[start_position + 2 + w * 3]);
	}
	if (i % w > 0) {
		//left pixel
		Rarr.push_back(layer_copy[start_position - 3]);
		Garr.push_back(layer_copy[start_position + 1 - 3]);
		Barr.push_back(layer_copy[start_position + 2 - 3]);
	}
	if (i % w < w - 1) {
		//right pixel
		Rarr.push_back(layer_copy[start_position + 3]);
		Garr.push_back(layer_copy[start_position + 1 + 3]);
		Barr.push_back(layer_copy[start_position + 2 + 3]);
	}

	std::sort(Rarr.begin(), Rarr.end());
	std::sort(Garr.begin(), Garr.end());
	std::sort(Barr.begin(), Barr.end());
	int number_of_neighbours_and_me = Rarr.size();//same for all Rarr, Garr, Barr
	if (number_of_neighbours_and_me % 2 == 0) {
		layer[start_position] = (Rarr[number_of_neighbours_and_me / 2] + Rarr[number_of_neighbours_and_me / 2 - 1]) / 2;
		layer[start_position + 1] = (Garr[number_of_neighbours_and_me / 2] + Garr[number_of_neighbours_and_me / 2 - 1]) / 2;
		layer[start_position + 2] = (Barr[number_of_neighbours_and_me / 2] + Barr[number_of_neighbours_and_me / 2 - 1]) / 2;
	}
	else {
		layer[start_position] = Rarr[number_of_neighbours_and_me / 2];
		layer[start_position + 1] = Garr[number_of_neighbours_and_me / 2];
		layer[start_position + 2] = Barr[number_of_neighbours_and_me / 2];
	}
	Rarr.clear();
	Garr.clear();
	Barr.clear();
}
