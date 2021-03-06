package org.targettest.calc.rules;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A value object used to represent the result of a 
 * CalculationStrategy.
 * 
 * It can include a list of Test classes to run or an
 * instruction indicating all available tests should be 
 * run.
 */
public class CalculationResult {

	private final boolean runAllTests;
	private final List<String> runTests;

	private CalculationResult() {
		runAllTests = false;
		runTests = Collections.emptyList();
	}
	
	private CalculationResult(String className) {
		runAllTests = false;
		runTests = Collections.singletonList(className);
	}

	private CalculationResult(Collection<String> classes) {
		runAllTests = false;
		runTests = new ArrayList<String>(classes);
	}
	
	private CalculationResult(boolean runAll) {
		runAllTests = runAll;
		runTests = Collections.emptyList();
	}

	public static CalculationResult runAllTests() {
		return new CalculationResult(true);
	}
	
	public static CalculationResult singleTest(String testName){
		return new CalculationResult(testName);
	}
	
	public boolean isRunAllTests() {
		return runAllTests;
	}

	public List<String> getRunTests() {
		return new ArrayList<String>(runTests);
	}

	public static CalculationResult noResult() {
		return new CalculationResult();
	}

	public static CalculationResult tests(Collection<String> tests) {
		return new CalculationResult(tests);
	}

	
}
