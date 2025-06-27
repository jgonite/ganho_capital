package com.mybank.domain.solution.impl;

import static com.mybank.domain.parameter.SolutionParameters.TAXABLE_TRANSACTION_THRESHOLD;
import static com.mybank.domain.parameter.SolutionParameters.TAX_PERCENT;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import com.mybank.adapter.json.JsonMapper;
import com.mybank.domain.calculator.AveragePriceCalculator;
import com.mybank.domain.solution.CapitalGainSolution;

public class CapitalGainSolutionMyBankImpl implements CapitalGainSolution {
	
	private final static char BUY_CODE = 'b';
	private final static char SELL_CODE = 's';
	
	private AveragePriceCalculator calculator;
	
	public CapitalGainSolutionMyBankImpl(AveragePriceCalculator calculator) {
		this.calculator = calculator;
	}
	
	@Override
	public String resolveCapitalGainTaxes(String transactionList) {
		StringBuffer solution = new StringBuffer();
		solution.append("[");
		
		long accumulatedQuantity = 0l; double averagePrice = 0.0; double loss = 0.0; boolean firstLoop = true; 
		Matcher matcher = JsonMapper.findTransactionMatches(transactionList);
        while (matcher.find()) {

        	double tax = 0.0;
			String trasactionJson = matcher.group();
			String[] keyValues = trasactionJson.replace("{", "").replace("}", "").split(",");
			
			char operationCode = JsonMapper.extractFirstByteFromValue(keyValues[0]);
			double unitCost = JsonMapper.extractValueAsDouble(keyValues[1]);
			long quantity = JsonMapper.extractValueAsLong(keyValues[2]);
			
			if (operationCode == BUY_CODE) {
				averagePrice =  calculator.calculate(accumulatedQuantity, quantity, averagePrice, unitCost );
				accumulatedQuantity += quantity;
			}
			
			if (operationCode == SELL_CODE) {
				accumulatedQuantity -= quantity;
				double taxableAmount = 0.0;
				
				if (unitCost < averagePrice) { 
					loss += quantity*(averagePrice-unitCost);
				}
				
				if (unitCost > averagePrice && quantity*unitCost > TAXABLE_TRANSACTION_THRESHOLD) {
					double profit = quantity*(unitCost-averagePrice);
					if (loss > profit) {
						loss -= profit;
					} else {
						taxableAmount = profit-loss;
						loss = 0;
						
					}
				}
				tax = TAX_PERCENT*taxableAmount;	
			}
			
			if (!firstLoop) solution.append(", "); firstLoop = false;
			solution.append(JsonMapper.generateObjectFromKeyValue("tax", new BigDecimal(Double.toString(tax))));
		}
		
		solution.append("]");
		return solution.toString();
	}	
	
}
