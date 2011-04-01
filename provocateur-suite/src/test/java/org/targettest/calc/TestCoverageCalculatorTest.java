package org.targettest.calc;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.targettest.calc.TestCoverageCalculator;
import org.targettest.calc.TestCoverageResult;
import org.targettest.calc.rules.CalculationResult;
import org.targettest.calc.rules.CalculationStrategy;
import org.targettest.calc.scm.ModificationStrategy;

import static org.mockito.Mockito.*;

public class TestCoverageCalculatorTest {

	private static final String TEST2 = "File1IntTest";
	private static final String TEST1 = "File1Test";
	private CalculationStrategy strategy1;
	private CalculationStrategy strategy2;
	private Collection<CalculationStrategy> strategies;
	private ModificationStrategy modificationStrategy;
	private List<String> modifiedFiles;

	@Before
	public void setup() {
		modifiedFiles = Arrays.asList("File1.java");
		modificationStrategy = setupChanges(modifiedFiles);
		strategy1 = mock(CalculationStrategy.class);
		strategy2 = mock(CalculationStrategy.class);
		strategies = new ArrayList<CalculationStrategy>(Arrays.asList(strategy1, strategy2));
	}
	
	@Test
	public void acumulatesResultsFromAllStrategies() {
		when(strategy1.calculate(modifiedFiles)).thenReturn(createResult(TEST1));
		when(strategy2.calculate(modifiedFiles)).thenReturn(createResult(TEST2));
		TestCoverageCalculator underTest = new TestCoverageCalculator(modificationStrategy, strategies);
		TestCoverageResult result = underTest.getTestClassesForChanges();
		verify(strategy1).calculate(modifiedFiles);
		verify(strategy2).calculate(modifiedFiles);
		Assert.assertFalse(result.isRunAllTests());
		Assert.assertEquals(Arrays.asList(TEST1, TEST2), result.getNamedTestsToRun());
	}

	public void stopsAsSoonAsOneStrategySaysRunAllTests() {
		when(strategy1.calculate(modifiedFiles)).thenReturn(createAllTestsResult());
		TestCoverageCalculator underTest = new TestCoverageCalculator(modificationStrategy, strategies);
		TestCoverageResult result = underTest.getTestClassesForChanges();
		verify(strategy1).calculate(modifiedFiles);
		verifyZeroInteractions(strategy2);
		Assert.assertTrue(result.isRunAllTests());
		Assert.assertTrue(result.getNamedTestsToRun().isEmpty());
	}
	
	private CalculationResult createAllTestsResult() {
		return CalculationResult.runAllTests();
	}

	private CalculationResult createResult(String className) {
		return CalculationResult.singleTest(className);
	}

	private ModificationStrategy setupChanges(List<String> modifiedFiles) {
		ModificationStrategy modificationStrategy = mock(ModificationStrategy.class);
		when(modificationStrategy.identifyModifications()).thenReturn(modifiedFiles);
		return modificationStrategy;
	}
}
