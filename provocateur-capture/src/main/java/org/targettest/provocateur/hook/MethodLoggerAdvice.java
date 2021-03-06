package org.targettest.provocateur.hook;

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


import org.targettest.provocateur.capture.MethodLogger;

/**
 * The ASM hook 'Advice' called when any test or production 
 * method is entered or exited. This is called as the method 
 * is actually executed.
 *
 * This Advice delegates most of its behaviour to the MethodLogger
 * singleton.
 * 
 * This class forms an anti-corruption boundary between the 
 * ASM mechanics of runtime hooking and the profiling logic.
 */

public class MethodLoggerAdvice {

	public static void onMethodEnter(String clazzName, String name, boolean testMethod) {
		if (testMethod) {
			MethodLogger.INSTANCE.handleTestEntry(clazzName, name);
		} else {
			if(isNotMockMethod(clazzName)) {
				MethodLogger.INSTANCE.logUnderTest(clazzName, name);
			}
		}
	}

	public static void onMethodExit(String clazzName, String name, boolean testMethod) {
		if (testMethod) {
			MethodLogger.INSTANCE.handleTestExit(clazzName, name);
		} else {
			if(isNotMockMethod(clazzName)) {
				MethodLogger.INSTANCE.logUnderTest(clazzName, name);
			}
		}
	}

	private static boolean isNotMockMethod(String clazzName) {
		return !clazzName.contains("EnhancerByMockitoWithCGLIB");
	}
}
