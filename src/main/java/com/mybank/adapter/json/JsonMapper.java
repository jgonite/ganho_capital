package com.mybank.adapter.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mybank.domain.parameter.SolutionParameters;

public class JsonMapper {

	public static String generateObjectFromKeyValue(String key, BigDecimal value) {
		BigDecimal truncated = value.setScale(SolutionParameters.SCALE, RoundingMode.DOWN);
		return String.format("{\"%s\": %s}", key, truncated.toPlainString());
	}
	
	public static Matcher findTransactionMatches(String transactionList) {
		Pattern pattern = Pattern.compile("\\{[^{}]*\\}"); 
		return pattern.matcher(transactionList);
	}
	
	public static char extractFirstByteFromValue(String keyValue) {
		return keyValue.split(":")[1].replaceAll("\\\"", "").trim().charAt(0);
	}
	
	public static double extractValueAsDouble(String keyValue) {
		return Double.parseDouble(keyValue.split(":")[1].trim());
	}
	
	public static long extractValueAsLong(String keyValue) {
		return Long.parseLong( keyValue.split(":")[1].trim() );
	}

}
