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

/**
 * A rule which recognises if the modified file is a Spring
 * configuration file. If it is this rule votes to run all
 * Test classes available.
 * 
 * This behaviour is desirable as it is difficult to predict
 * the effect of changing a Spring file, often it can cause
 * test failure in unexpected areas. When in doubt Provocateur
 * will run all tests.
 */

public class ModifiedSpringConfigFileRule extends RunAllRule {

	protected boolean isMatchFile(String file) {
		// TODO Write a better matcher
		return file.endsWith(".xml") && !file.endsWith("pom.xml");
	}

}
