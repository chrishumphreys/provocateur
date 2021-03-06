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
import java.util.List;

/**
 * Value object class for representing the result of a call
 * to TestCoverageCalculator.getTestClassesForChanges()
 * 
 */
public class TestCoverageResult {

	private final boolean runAllTests;
	private final Collection<String> namedTestsToRun;

	public TestCoverageResult(boolean runAllTests, List<String> namedTestsToRun) {
		this.runAllTests = runAllTests;
		this.namedTestsToRun = namedTestsToRun;
	}

	public boolean isRunAllTests() {
		return runAllTests;
	}

	public Collection<String> getNamedTestsToRun() {
		return new ArrayList<String>(namedTestsToRun);
	}
	
}
