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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.targettest.org.objectweb.asm.ClassReader;
import org.targettest.org.objectweb.asm.ClassVisitor;
import org.targettest.org.objectweb.asm.ClassWriter;

public class MethodLoggerClassAdapterTest {

	private static final String JUNIT3_CLASS = "org/targettest/provocateur/hook/SampleJunit3Test.class";
	private static final String JUNIT4_CLASS = "org/targettest/provocateur/hook/MethodLoggerClassAdapterTest.class";

	@Test
	public void identifiesJunit3TestClassesCorrectly() throws IOException {
		MethodLoggerClassAdapter methodLoggerClassAdapter = parseClass(JUNIT3_CLASS);
		Assert.assertTrue(methodLoggerClassAdapter.isJunit3TestClass());
	}
	
	@Test
	public void doesntIncorrectlyIdentifiesJunit3TestClasses() throws IOException {
		MethodLoggerClassAdapter methodLoggerClassAdapter = parseClass(JUNIT4_CLASS);
		Assert.assertFalse(methodLoggerClassAdapter.isJunit3TestClass());
	}

	private MethodLoggerClassAdapter parseClass(String clazzName) throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(clazzName);
		byte[] classBytes = readBytes(is);
		ClassReader reader = new ClassReader(classBytes);
		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
		MethodLoggerClassAdapter methodLoggerClassAdapter = new MethodLoggerClassAdapter(writer);
		ClassVisitor adapter = methodLoggerClassAdapter;
		reader.accept(adapter, ClassReader.EXPAND_FRAMES);
		writer.toByteArray();
		return methodLoggerClassAdapter;
	}

	private byte[] readBytes(InputStream is) throws IOException {
		List<Byte> bytes = new ArrayList<Byte>();
		int read = -1;
		do {
			read = is.read();
			if (read > -1) {
				bytes.add((byte)read);
			}
		} while (read > -1);
		
		byte[] result = new byte[bytes.size()];
		for (int i=0; i<bytes.size(); i++) {
			result[i] = bytes.get(i);
		}
		return result;
	}

}

