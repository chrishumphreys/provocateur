package org.targettest.suite;

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

import org.targettest.provocateur.storage.DataStore;
import org.targettest.provocateur.storage.DataStoreFactory;

public class SampleDataStoreInitialiser implements ProvocateurInitialiser {

	private static final String TEST_CLASS = "org/targettest/suite/TestCaseForSampleSuiteTest2";
	private static final String TEST_CLASS2 = "org/targettest/suite/AnotherTestCaseForSampleSuiteTest2";
	private static final String SRC_CLASS = "org/undertest/MyClass";
	private static final String SRC_CLASS2 = "org/undertest/MyClass2";
	
	@Override
	public void initialise() {
		System.out.println("TestDatabaseInitialiser initialising....");
		DataStore dataStore = DataStoreFactory.getDataStore(true);
		initialiseTestData(dataStore);
		dataStore.close();
		System.out.println("TestDatabaseInitialiser initialised.");
	}

	private void initialiseTestData(DataStore dataStore) {
        dataStore.insertSrcMethod(TEST_CLASS, "testMethod", SRC_CLASS, "srcMethod");
        dataStore.insertSrcMethod(TEST_CLASS2, "testMethod", SRC_CLASS2, "srcMethod");
	}

}
