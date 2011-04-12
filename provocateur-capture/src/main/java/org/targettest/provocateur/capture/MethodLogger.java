package org.targettest.provocateur.capture;

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

/**
 * Singleton service implementing Provocateur's profiling behaviour.
 * 
 * The aim of this service is to create an index of which test methods
 * 'exercise' which production code methods.
 *   
 * During a (full) test run with the profiler enabled Provocateur will
 * intercept the execution of method calls.
 * 
 * This service will identify if the method is a test method or production 
 * (under test) method. For production methods it will insert an entry into
 * the index (via the DataStore) recording the method class and name and 
 * the running test method and class.
 * 
 * The service is responsble for remembering which test method is currently
 * executining for use in by all subsequent production method invocations. 
 * It uses a ThreadLocal for this purpose. The value is set when a test method 
 * is entered and cleared when the test method exits.
 *    
 * This service uses the DataStoreFactory to access the configured DataStore.
 * Usually this is a Lucene index.
 * 
 * This service is invoked at runtime by some ASM byte code instrumentatioin
 * added by MethodLoggerAdviceAdapter.
 */

public enum MethodLogger {
	INSTANCE;

	private DataStore dataStore;
	private ThreadLocal<String> currentTestClass = new ThreadLocal<String>();
	private ThreadLocal<String> currentTestMethod = new ThreadLocal<String>();

	private static final boolean DEBUG = false;

	private MethodLogger() {
		initialise();
	}

	private void initialise() {
		try {
			dataStore = DataStoreFactory.getDataStore(true);

			Runtime rt = Runtime.getRuntime();
			rt.addShutdownHook(new Thread() {
				@Override
				public void run() {
					shutdown();
				}
			});
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void logUnderTest(String clazzName, String methodName) {
		if (DEBUG)
			System.out.println("Handle src entry " + clazzName + " "
					+ methodName);
		try {
			if (currentTestClass.get() != null) {
				dataStore.insertSrcMethod(currentTestClass.get(),
						currentTestMethod.get(), clazzName, methodName);
			}
		} catch (Throwable t) {
		}
	}

	public void handleTestEntry(String clazzName, String methodName) {
		if (DEBUG)
			System.out.println("Handle test entry " + clazzName + " "
					+ methodName);
		currentTestClass.set(clazzName);
		currentTestMethod.set(methodName);
	}

	public void handleTestExit(String clazzName, String methodName) {
		if (DEBUG)
			System.out.println("Handle test exit " + clazzName + " "
					+ methodName);
		currentTestMethod.remove();
		currentTestClass.remove();
	}

	private void shutdown() {
		if (DEBUG) {
			System.out.println("Shutdown datastore");
		}
		dataStore.close();
	}
}
