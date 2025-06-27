package com.mybank.port.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;

import com.mybank.domain.solution.CapitalGainSolution;
import com.mybank.domain.solution.impl.CapitalGainSolutionMyBankImpl;
import com.mybank.infra.config.CapitalGainConfig;


public class AppCLI {

	public static void main(String[] input) throws IOException {
		/*
		 * Leitura do arquivo
		 */
		Queue<String> analyzedLines = new ArrayDeque<>();
		String line;
		try ( BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			while ( (line = reader.readLine()) != null ) {
				if (line.trim().isEmpty()) break;
	        	analyzedLines.offer(line);
	        }   
		} catch (IOException ioe) {
			System.out.println("An error ocurred while reading the file: " + ioe.getMessage());
		}
		
		/*
		 * Configurações do programa
		 */
		CapitalGainConfig config = new CapitalGainConfig();
        
        /*
		 * Resolução
		 */
        while ( (line = analyzedLines.poll()) != null) {
        	System.out.println(config.getSolution().resolveCapitalGainTaxes(line));
        }

	}
}
