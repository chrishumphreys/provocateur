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

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.targettest.calc.scm.ModificationStrategy;
import org.targettest.provocateur.storage.DataStore;
import org.targettest.provocateur.storage.DataStoreFactory;

public class TestCoverageCalculatorIntTest {

	private static final String TEST_CLASS_SCM = "org/undertest/MyTest.java";
	private static final String TEST_CLASS = "org/undertest/MyTest";
	
	private static final String SRC_CLASS_SCM = "org/undertest/MyClass.java";
	private static final String SRC_CLASS = "org/undertest/MyClass";
	
	private static final String TEST_CLASS2 = "org/undertest/TestClass2";
	private static final String SPRING_CONFIG_FILE_SCM = "org/context.xml";
	private static final String HIBERNATE_FILE_SCM = "org/context.xml";
	private static final String POM_FILE_SCM = "aproject/pom.xml";
	private static DataStore dataStore;
	private static ModificationStrategy modificationStrategy;
	private static TestCoverageCalculator calc;
	
	@BeforeClass
	public static void setupData() {
		dataStore = DataStoreFactory.getDataStore(true);
        dataStore.insertSrcMethod(TEST_CLASS, "testMethod", SRC_CLASS, "srcMethod");
        dataStore.insertSrcMethod(TEST_CLASS2, "testMethod", SRC_CLASS, "srcMethod");
        dataStore.close();
        
		modificationStrategy = Mockito.mock(ModificationStrategy.class);
		calc = TestCoverageCalculatorFactory.createWithModificationStrategy(modificationStrategy);
	}
	
	@After
	public void close() {
	}
	
	@Test
	public void canCalculateTestsForJavaSourceChanges() {
		Mockito.when(modificationStrategy.identifyModifications()).thenReturn(Arrays.asList(SRC_CLASS_SCM));
		Assert.assertFalse(calc.getTestClassesForChanges().isRunAllTests());
		Assert.assertEquals(2, calc.getTestClassesForChanges().getNamedTestsToRun().size());
		Iterator<String> iterator = calc.getTestClassesForChanges().getNamedTestsToRun().iterator();
		Assert.assertEquals(TEST_CLASS, iterator.next());
		Assert.assertEquals(TEST_CLASS2, iterator.next());
	}
	
	@Test
	public void canCalculateTestsForSpringConfigChanges() {
		Mockito.when(modificationStrategy.identifyModifications()).thenReturn(Arrays.asList(SPRING_CONFIG_FILE_SCM));
		Assert.assertTrue(calc.getTestClassesForChanges().isRunAllTests());
	}
	
	@Test
	public void canCalculateTestsForHibernateConfigChanges() {
		Mockito.when(modificationStrategy.identifyModifications()).thenReturn(Arrays.asList(HIBERNATE_FILE_SCM));
		Assert.assertTrue(calc.getTestClassesForChanges().isRunAllTests());
	}
	
	@Test
	public void canCalculateTestsForPomConfigChanges() {
		Mockito.when(modificationStrategy.identifyModifications()).thenReturn(Arrays.asList(POM_FILE_SCM));
		Assert.assertTrue(calc.getTestClassesForChanges().isRunAllTests());
	}
	
	@Test
	public void canCalculateTestsForTestSrcChanges() {
		Mockito.when(modificationStrategy.identifyModifications()).thenReturn(Arrays.asList(TEST_CLASS_SCM));
		Assert.assertFalse(calc.getTestClassesForChanges().isRunAllTests());
		Assert.assertEquals(1, calc.getTestClassesForChanges().getNamedTestsToRun().size());
		Iterator<String> iterator = calc.getTestClassesForChanges().getNamedTestsToRun().iterator();
		Assert.assertEquals(TEST_CLASS, iterator.next());
	}
}
