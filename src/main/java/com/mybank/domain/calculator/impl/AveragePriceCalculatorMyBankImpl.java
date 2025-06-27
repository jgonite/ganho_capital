package com.mybank.domain.calculator.impl;

import com.mybank.domain.calculator.AveragePriceCalculator;

public class AveragePriceCalculatorMyBankImpl implements AveragePriceCalculator {
	
	public double calculate( long previousAccumulatedQuantity, long acquiredQuantity, double previousAveragePrice,  double acquiredUnitCost) {
		return ( (previousAccumulatedQuantity*previousAveragePrice) + (acquiredQuantity*acquiredUnitCost) ) / (previousAccumulatedQuantity + acquiredQuantity);
	}

}
