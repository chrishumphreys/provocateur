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
import java.util.List;

import org.targettest.provocateur.storage.DataStore;

/**
 * A rule which matches a modified Java production source file.
 * This rule uses Provocateur's DataStore to identify all Tests
 * which 'exercise' this production class.
 * 
 * All Tests found to 'exercise' this class are added to the list
 * of Test cases to run.
 * 
 * This rule uses Provocateur's DataStore to calculate tests. This
 * store must have previously been created by running Provocateur's
 * capture profiler. See MethodLoggerPreloader.
 *
 */
public class ModifiedJavaSourceRule implements CalculationStrategy {

	private DataStore dataStore;
	private ClassnameHelper classnameHelper;
	
	public ModifiedJavaSourceRule(DataStore dataStore) {
		this.dataStore = dataStore;
		this.classnameHelper = new ClassnameHelper();
	}
	
	@Override
	public CalculationResult calculate(List<String> modified) {
		Collection<String> tests = new ArrayList<String>();
		for (String file : modified) {
			tests.addAll(findTestsForFile(file));
		}
		return CalculationResult.tests(tests);
	}

	private Collection<? extends String> findTestsForFile(String file) {
		String className = classnameHelper.convertScmFilenameToClassName(file);
		return dataStore.getTestClassesForProductionClass(className);
	}

}
