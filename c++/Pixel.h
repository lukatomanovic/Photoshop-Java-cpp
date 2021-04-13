#pragma once
#ifndef PIXEL_HPP_
#define PIXEL_HPP_
#include<cstdint>
class Pixel
{
public:
	static const unsigned max_val_ = 255;

	Pixel(uint8_t red = 0, uint8_t green = 0, uint8_t blue = 0, uint8_t alpha=0);

	uint8_t getRed()const { return R_; }
	void setRed(int8_t red) { R_= red; }

	uint8_t getGreen()const { return G_; }
	void setGreen(uint8_t green) { G_ = green; }

	uint8_t getBlue()const { return B_; }
	void setBlue(uint8_t blue) { B_ = blue; }

	uint8_t getAlpha()const { return alpha_; }
	void setAlpha(uint8_t alpha) { alpha_ = alpha; }
 
private:
	uint8_t R_, G_, B_;
	uint8_t alpha_;
};

#endif