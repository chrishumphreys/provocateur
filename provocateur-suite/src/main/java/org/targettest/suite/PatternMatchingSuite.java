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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;

public class PatternMatchingSuite  extends Suite {

    private static Class<?>[] getClassesFromAnnotation(Class<?> klass) {
        SuiteClassPattern annotation = getSuiteClassAnnotation(klass);
        Set<Class<?>> classes = PatternMatchingClassCollector.getClassesFromPattern(annotation.include());

        classes.removeAll(Arrays.asList(annotation.exclude()));

        return classes.toArray(new Class<?>[]{});
    }

    private static SuiteClassPattern getSuiteClassAnnotation(Class<?> klass) {
        SuiteClassPattern annotation = klass.getAnnotation(SuiteClassPattern.class);
        if (annotation == null)
            throw new RuntimeException(String.format("class '%s' must have a SuiteClassPattern annotation", klass.getName()));
        return annotation;
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface SuiteClassPattern {
        public String include();

        public Class<?>[] exclude() default {};

        public boolean parallel() default false;
    }

    protected final List<Runner> runners = new ArrayList<Runner>();
	protected Set<Class<?>> matchedTestClasses = new HashSet<Class<?>>();

    public PatternMatchingSuite(Class<?> testClass, RunnerBuilder builder) throws Throwable {
        super(testClass, builder);
        for (Class<?> childTest : getClassesFromAnnotation(testClass)) {
        	matchedTestClasses.add(childTest);
            runners.add(builder.runnerForClass(childTest));
        }
    }

    public boolean shouldRunInParallel(Class<?> clazz) {
        return getSuiteClassAnnotation(clazz).parallel();
    }

    @Override
    public void run(final RunNotifier notifier) {
        if (shouldRunInParallel(getTestClass().getJavaClass())) {
            runTestsInParallel(notifier);
        } else {
            runSequential(notifier);
        }
    }

    private void runSequential(RunNotifier notifier) {
        for (Runner runner : runners) {
            runner.run(notifier);
        }
    }

    private void runTestsInParallel(final RunNotifier notifier) {
        final int parrellelBits = Runtime.getRuntime().availableProcessors();
        final ExecutorService executorService = Executors.newFixedThreadPool(parrellelBits);
        for (final Runner runner : runners) {
            executorService.submit(new Runnable() {
                public void run() {
                    runner.run(notifier);
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


}
