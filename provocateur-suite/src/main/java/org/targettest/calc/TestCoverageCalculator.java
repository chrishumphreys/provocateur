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

/**
 * Provcateur support the creation of dynamic test suites based on
 * a change set. See ProvocateurPatternMatchingSuite. 
 * 
 * Internally Provocateur uses TestCoverageCalculator to calculate the
 * list of Test classes for a set of changed production code. It does this
 * by using a ModificationStrategy (e.g. SvnModificationtrategy) to determine
 * the list of changes and applies a series of rules to the change set.
 * 
 * The rules indicate how to deal with a certain changed file. For example
 * if the changed file is a Test then that test is included in the list to
 * run.
 * 
 * If the changed file is a production Java class, Provocateur queries its
 * DataStore to determine all Test classes which 'exercise' that class. All
 * matches are added to the list of tests to run.
 * 
 * There are a few other rules which are documented in the rules package.
 *  
 * The rules are executed in a priority order, an early rule can instruct 
 * Provocateur to run all tests available. In this case later rules need
 * not be executed. Typically though each rule is executed and their output
 * is aggregated into a single list of Test classes to run.
 *  
 *  
 * As an end user it is unusual to use TestCoverageCalculator directly. See
 * ProvocateurPatternMatchingSuite instead.
 *
 */

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
