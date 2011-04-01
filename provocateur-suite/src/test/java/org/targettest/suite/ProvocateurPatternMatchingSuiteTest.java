package org.targettest.suite;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.RunnerBuilder;
import org.targettest.provocateur.storage.DataStoreFactory;
import org.targettest.suite.PatternMatchingSuite.SuiteClassPattern;
import org.targettest.suite.ProvocateurPatternMatchingSuite.UseInitialiser;
import org.targettest.suite.ProvocateurPatternMatchingSuite.UseModificationStrategy;

public class ProvocateurPatternMatchingSuiteTest {
	
	@AfterClass
	public static void closeDown() {
		DataStoreFactory.close();
	}
	
	@Test
	public void patternMatchingSuiteMatchesAllTestClasesByName() throws Throwable {
		MyMockRunnerBuilder mockRunnerBuilder = new MyMockRunnerBuilder();
		PatternMatchingSuite suite = new PatternMatchingSuite(SamplePatternMatchingSuite.class, mockRunnerBuilder.create());
		suite.run(mock(RunNotifier.class));
		
		List<Class<?>> allTestClasses = mockRunnerBuilder.getExecutedTests();
		Assert.assertEquals(2,allTestClasses.size());
		Assert.assertTrue(allTestClasses.contains(TestCaseForSampleSuiteTest.class));
		Assert.assertTrue(allTestClasses.contains(TestCaseForSampleSuiteTest2.class));
	}
	
	@Test
	public void provocateurPatternMatchingSuiteFiltersAllMatchingTestClasesByModificationStatusAndDoesntIncludeOtherTestsNotCoveredByOriginalMatcher() throws Throwable {
		MyMockRunnerBuilder mockRunnerBuilder = new MyMockRunnerBuilder();
		ProvocateurPatternMatchingSuite suite = new ProvocateurPatternMatchingSuite(SampleProvocateurSuite.class, mockRunnerBuilder.create());
		suite.run(mock(RunNotifier.class));
		List<Class<?>> allTestClasses = mockRunnerBuilder.getExecutedTests();
		Assert.assertEquals(1,allTestClasses.size());
		Assert.assertEquals(TestCaseForSampleSuiteTest2.class, allTestClasses.get(0));
		Assert.assertFalse(allTestClasses.contains(AnotherTestCaseForSampleSuiteTest2.class));
	}
	
	public static class MyMockRunnerBuilder {
		final List<Class<?>> executedTests = new ArrayList<Class<?>>();
		
		public RunnerBuilder create() {
			return new RunnerBuilder() {
				@Override
				public Runner runnerForClass(Class<?> testClass) throws Throwable {
					return new MyMockRunner(testClass, executedTests);
				}
			};
		}

		public List<Class<?>> getExecutedTests() {
			return executedTests;
		}
	}
	
	public static class MyMockRunner extends Runner {
		private final Class<?> testClass;
		private final List<Class<?>> executedTests;

		public MyMockRunner(Class<?> testClass, List<Class<?>> executedTests) {
			this.testClass = testClass;
			this.executedTests = executedTests;
		}

		@Override
		public Description getDescription() {
			return null;
		}

		@Override
		public void run(RunNotifier notifier) {
			executedTests.add(testClass);
			System.out.println("Running "+testClass);
		}
	}
	
	@RunWith(PatternMatchingSuite.class)
	@SuiteClassPattern(include = "classpath:/org/targettest/**/TestCaseForSampleSuiteTest*.class")
	@SuiteClasses(value={})
	public class SamplePatternMatchingSuite {
	}
	
	@UseModificationStrategy(strategy=StubbedModificationStrategy.class)
	@RunWith(ProvocateurPatternMatchingSuite.class)
	@SuiteClassPattern(include = "classpath:/org/targettest/**/TestCaseForSampleSuiteTest*.class")
	@SuiteClasses(value={})
	@UseInitialiser(SampleDataStoreInitialiser.class)
	public class SampleProvocateurSuite {

	}
}
