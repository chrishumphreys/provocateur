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

/**
 * A rule which detects if the modified file is a Test class.
 * If it is this rule votes to run that Test class.
 */

public class ModifiedTestRule implements CalculationStrategy {
	private ClassnameHelper classnameHelper;

	public ModifiedTestRule() {
		this.classnameHelper = new ClassnameHelper();
	}
	
	@Override
	public CalculationResult calculate(List<String> modified) {
		Collection<String> tests = new ArrayList<String>();
		for (String file : modified) {
			if(isTest(file)) {
				tests.add(classnameHelper.convertScmFilenameToClassName(file));
			}
		}
		return CalculationResult.tests(tests);
	}

	private boolean isTest(String file) {
		//TODO write a better matcher
		return file.endsWith("Test.java");
	}

}
