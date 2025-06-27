package com.mybank.domain.calculator;

public interface AveragePriceCalculator {

	double calculate( long previousAccumulatedQuantity, long acquiredQuantity, double previousAveragePrice,  double acquiredUnitCost);
	
}
