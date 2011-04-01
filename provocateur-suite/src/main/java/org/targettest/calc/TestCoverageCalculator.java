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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.targettest.calc.rules.CalculationResult;
import org.targettest.calc.rules.CalculationStrategy;
import org.targettest.calc.scm.ModificationStrategy;

public class TestCoverageCalculator {

	private final ModificationStrategy modificationStrategy;
	private final List<CalculationStrategy> priorityOrderedStrategies;
	
	public TestCoverageCalculator(ModificationStrategy modificationStrategy, Collection<CalculationStrategy> priorityOrderedStrategies) {
		this.modificationStrategy = modificationStrategy;
		this.priorityOrderedStrategies = new ArrayList<CalculationStrategy>(priorityOrderedStrategies);
	}

	public TestCoverageResult getTestClassesForChanges() {
		List<String> modifiedFiles = modificationStrategy.identifyModifications();
		boolean runAllTests = false;
		List<String> namedTestsToRun = new ArrayList<String>();
		
		for (Iterator<CalculationStrategy> strategyIterator = priorityOrderedStrategies.iterator(); strategyIterator.hasNext() && !runAllTests;) {
			CalculationStrategy strategy = strategyIterator.next();
			CalculationResult calcResult = strategy.calculate(modifiedFiles);
			if (calcResult.isRunAllTests()) {
				namedTestsToRun.clear();
				runAllTests = true;
			} else {
				namedTestsToRun.addAll(calcResult.getRunTests());
			}
		}
		
		return new TestCoverageResult(runAllTests, namedTestsToRun);
	}
}
