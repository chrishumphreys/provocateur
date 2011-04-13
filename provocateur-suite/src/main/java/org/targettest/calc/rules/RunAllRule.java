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

import java.util.List;


/**
 * An abstract super class for those strategies which force
 * Provcateur to run all available tests in the master suite.
 * 
 * For example the ModifiedSpringConfigRule will instruct 
 * Provocateur to give up attempting to determine the set of Test
 * classes and run all tests. This is sensible as modifying
 * spring configuration can have a big (and indeterminable) 
 * effect on your code.
 *
 */
public abstract class RunAllRule implements CalculationStrategy {

	@Override
	public CalculationResult calculate(List<String> modified) {
		if (match(modified)) {
			return CalculationResult.runAllTests();
		}
		return CalculationResult.noResult();
	}

	protected boolean match(List<String> allModifiedFiles) {
		for (String file : allModifiedFiles) {
			if(isMatchFile(file)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean isMatchFile(String file);

}
