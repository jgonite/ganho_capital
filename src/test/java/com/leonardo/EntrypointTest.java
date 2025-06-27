package com.leonardo;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class EntrypointTest {
	
	private TestUtils testUtils = new TestUtils();

	@Test
	public void case1Test() throws IOException {
		String filename = "case1.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case2Test() throws IOException {
		String filename = "case2.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case1case2Test() throws IOException {
		String filename = "case1+case2.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case3Test() throws IOException {
		String filename = "case3.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case4Test() throws IOException {
		String filename = "case4.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case5Test() throws IOException {
		String filename = "case5.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case6Test() throws IOException {
		String filename = "case6.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case7Test() throws IOException {
		String filename = "case7.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case8Test() throws IOException {
		String filename = "case8.txt";
		testUtils.testCase(filename);
	}
	
	@Test
	public void case9Test() throws IOException {
		String filename = "case9.txt";
		testUtils.testCase(filename);
	}
}
