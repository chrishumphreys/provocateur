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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;
import org.targettest.calc.TestCoverageCalculator;
import org.targettest.calc.TestCoverageCalculatorFactory;
import org.targettest.calc.TestCoverageResult;
import org.targettest.calc.scm.ModificationStrategy;

public class ProvocateurPatternMatchingSuite extends PatternMatchingSuite {

	private static final boolean DEBUG = true;
	private boolean filterResults = true;

	public ProvocateurPatternMatchingSuite(Class<?> testClass,
			RunnerBuilder builder) throws Throwable {
		super(testClass, builder);
		adjustSuiteContentsBasedOnChangeSet(testClass, builder);
	}

	private void adjustSuiteContentsBasedOnChangeSet(Class<?> testClass,
			RunnerBuilder builder) throws Throwable {
		initialiseHook(testClass);
		org.targettest.calc.scm.ModificationStrategy modificationStrategy = determineModificationStrategy(testClass);

		boolean ignorePomChanges = determineIfShouldIgnorePomChanges(testClass);

		TestCoverageCalculator calc = TestCoverageCalculatorFactory
				.createWithModificationStrategy(modificationStrategy,
						ignorePomChanges);

		TestCoverageResult testCovereageResult = calc
				.getTestClassesForChanges();
		if (!testCovereageResult.isRunAllTests()) {

			List<Runner> filteredRunners = new ArrayList<Runner>();
			for (String className : testCovereageResult.getNamedTestsToRun()) {
				Class<?> clazz = findClassForName(convertToClassName(className));
				if (!filterResults || isExistingMatch(clazz)) {
					filteredRunners.add(builder.runnerForClass(clazz));
				}
			}
                        int originalSize = runners.size();
			runners.clear();
			runners.addAll(filteredRunners);
			if (DEBUG) {
				System.out.println("Provocateur adjusting suite classes. From "
						+ originalSize + " to "
						+ runners.size());
			}

		} else {
			if (DEBUG) {
				System.out.println("Provcateur running existing suite...");
			}
		}
	}

	private boolean isExistingMatch(Class<?> clazz) {
		return matchedTestClasses.contains(clazz);
	}

	private boolean determineIfShouldIgnorePomChanges(Class<?> testClass) {
		UseModificationStrategy modificationStrategyAnnotation = getModificationStrategyAnnotation(testClass);
		return modificationStrategyAnnotation.ignorePom();
	}

	private String convertToClassName(String className) {
		return className.replaceAll("/", ".");
	}

	private void initialiseHook(Class<?> klass) {
		try {
			UseInitialiser annotation = klass
					.getAnnotation(UseInitialiser.class);
			if (annotation != null) {
				Class<? extends ProvocateurInitialiser> initClass = annotation
						.value();

				initClass.newInstance().initialise();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Class<?> findClassForName(String className)
			throws ClassNotFoundException {
		return Thread.currentThread().getContextClassLoader()
				.loadClass(className);
	}

	private org.targettest.calc.scm.ModificationStrategy determineModificationStrategy(
			Class<?> testClass) throws Exception {
		UseModificationStrategy modificationStrategyAnnotation = getModificationStrategyAnnotation(testClass);
		Class<? extends org.targettest.calc.scm.ModificationStrategy> modificationStrategyClass = modificationStrategyAnnotation
				.strategy();
		String[] rootDirs = modificationStrategyAnnotation.dirs();

		if (modificationStrategyAnnotation.base().length() > 0) {
			Constructor<? extends ModificationStrategy> construtor = modificationStrategyClass
					.getConstructor(String.class, String[].class);
			return construtor.newInstance(
					modificationStrategyAnnotation.base(), rootDirs);
		} else {
			return modificationStrategyClass.newInstance();
		}
	}

	private static UseModificationStrategy getModificationStrategyAnnotation(
			Class<?> klass) {
		UseModificationStrategy annotation = klass
				.getAnnotation(UseModificationStrategy.class);
		if (annotation == null)
			throw new RuntimeException(String.format(
					"class '%s' must have a ModificationStrategy annotation",
					klass.getName()));
		return annotation;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface UseModificationStrategy {
		public Class<? extends org.targettest.calc.scm.ModificationStrategy> strategy();

		public String base() default "";

		public String[] dirs() default { "/src/test/java",
				"/src/test/resources", "/src/main/java", "/src/main/resources" };

		public boolean ignorePom() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface UseInitialiser {
		public Class<? extends org.targettest.suite.ProvocateurInitialiser> value();
	}
}
