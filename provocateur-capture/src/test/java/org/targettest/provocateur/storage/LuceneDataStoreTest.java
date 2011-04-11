package org.targettest.provocateur.storage;

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

import junit.framework.Assert;

import org.junit.Test;

public class LuceneDataStoreTest {

	private static final String SRC_CLASS_NAME = "SrcClassName";
	private static final String TEST_CLASS_NAME = "TestClassName";
	private static final String TEST_CLASS2_NAME = "TestClass2Name";

	@Test
	public void canInsertAndSearchStore() {
		DataStore dataStore = getDataStore();
		dataStore.initialise(true);

		dataStore.insertSrcMethod(TEST_CLASS_NAME, "ignored", SRC_CLASS_NAME, "ignored");
		dataStore.insertSrcMethod(TEST_CLASS2_NAME, "ignored", SRC_CLASS_NAME, "ignored");

		dataStore.close();

		dataStore = getDataStore();
		dataStore.initialise(true);
		List<String> results = dataStore
				.getTestClassesForProductionClass(SRC_CLASS_NAME);
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(results.contains(TEST_CLASS_NAME));
		Assert.assertTrue(results.contains(TEST_CLASS2_NAME));
		dataStore.close();
	}

	private DataStore getDataStore() {
		return new LuceneDataStore();
	}

}