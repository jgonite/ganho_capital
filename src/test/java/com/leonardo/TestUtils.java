package com.leonardo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mybank.port.cli.AppCLI;

public class TestUtils {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";

	public String readTestCase(String filepath) throws IOException {
		Path path = Path.of("src", "test", "resources", filepath);
		return Files.readString(path, StandardCharsets.UTF_8);
	}

	public void testCase(String filename) throws IOException {
		String input = readTestCase("in/" + filename);
		String outputExpected = readTestCase("out/" + filename);
		InputStream sysinBefore = System.in;
		PrintStream sysoutBefore = System.out;
		ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream testOut = new ByteArrayOutputStream();
		System.setIn(testIn);
		System.setOut(new PrintStream(testOut));

		try {
			AppCLI.main(new String[0]);
		} finally {
			// Restaura os streams
			System.setIn(sysinBefore);
			System.setOut(sysoutBefore);
		}

		List<String> testResult = List.of(testOut.toString().split("\\R"));
		List<String> testGauge = List.of(outputExpected.split("\\R"));

		assertEquality(testResult, testGauge, filename);

	}
	
	private void assertEquality(List<String> testResult, List<String> testGauge, String filename) {
		assertEquals(testGauge.size(), testResult.size(), "Divergência no número de saídas esperado");

		Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		for (int i = 0; i < testResult.size(); i++) {
			String resultLine = testResult.get(i);
			String expectedLine = testGauge.get(i);

			Matcher resultMatcher = numberPattern.matcher(resultLine);
			Matcher expectedMatcher = numberPattern.matcher(expectedLine);
			
			int j = 0;
			while (resultMatcher.find() & expectedMatcher.find()) {
				BigDecimal resultNumber = new BigDecimal(resultMatcher.group());
				BigDecimal expectedNumber = new BigDecimal(expectedMatcher.group());
				BigDecimal diff = resultNumber.subtract(expectedNumber).abs();

				if (diff.compareTo(new BigDecimal("0.01")) > 0) {
					throw new AssertionError("Divergência de valor experado: Esperado=" + expectedNumber + " Obtido=" + resultNumber + " - " + filename + " (linha "+(i+1)+", cálculo " + (j+1) + ")");
				}
				j++;
			}
			
			BigDecimal anyResultMatcherYet = null;
			BigDecimal anyExpectedMatcherYet = null;
			try {
				anyResultMatcherYet = new BigDecimal(resultMatcher.group());
			} catch (IllegalStateException e) {}
			try {
				anyExpectedMatcherYet =  new BigDecimal(expectedMatcher.group());
			} catch (IllegalStateException e) {}
			
			if (anyResultMatcherYet != null || anyExpectedMatcherYet != null) {
				String maisOuMenos = anyResultMatcherYet == null ? "menos" : "mais";
				throw new AssertionError("Há " + maisOuMenos + " cálculos que o esperado." + " - " + filename + "  (linha "+(i+1)+")" );
			}
		}

		System.out.println(ANSI_GREEN + "Entrada " + filename + " testado e aprovado!" + ANSI_RESET);
	}

}
