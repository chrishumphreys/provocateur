package org.undertest.tools;

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

import org.targettest.provocateur.hook.MethodLoggerAdvice;

/**
 * A dummy implementation of the Aspect in Java
 * Use ClassDumper to create ASM instructions for real aspect
 * 
 * @author chris
 */
public class DummyAspect {

	public void enter() {
		System.out.println("Hi from aspect");
		MethodLoggerAdvice.onMethodEnter("clazzName", "name", true);
	}
	
	public void exit() {
		System.out.println("Hi from aspect");
		MethodLoggerAdvice.onMethodExit("clazzName", "name", true);
	}

}
