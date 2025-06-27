package com.mybank.infra.config;

import com.mybank.domain.calculator.impl.AveragePriceCalculatorMyBankImpl;
import com.mybank.domain.solution.CapitalGainSolution;
import com.mybank.domain.solution.impl.CapitalGainSolutionMyBankImpl;

public class CapitalGainConfig {
	
	private CapitalGainSolution solution;
	
	public CapitalGainConfig() {
		solution = new CapitalGainSolutionMyBankImpl(new AveragePriceCalculatorMyBankImpl());
	}

	public CapitalGainSolution getSolution() {
		return solution;
	}

	public void setSolution(CapitalGainSolution solutionImpl) {
		this.solution = solutionImpl;
	}

}
