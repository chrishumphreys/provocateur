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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import org.targettest.org.objectweb.asm.ClassReader;
import org.targettest.org.objectweb.asm.ClassVisitor;
import org.targettest.org.objectweb.asm.ClassWriter;

public class MethodLoggerPreloader implements ClassFileTransformer {

	private List<String> inspectPackages;

	public MethodLoggerPreloader(List<String> list) {
		this.inspectPackages = list;
	}

	public static void premain(String options, Instrumentation instrumentation) {
		String[] inspectPackages = new String[0];

		if (options != null) {
			inspectPackages = options.split(",");
		}
//		System.out.println(inspectPackages[0]);
		instrumentation.addTransformer(new MethodLoggerPreloader(Arrays
				.asList(inspectPackages)));
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			@SuppressWarnings("rawtypes") Class classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws IllegalClassFormatException {

		// Returning null means that no transformation was done.
		if (!isMatchingPackage(className)) {
			return null;
		}
		try {
			ClassReader reader = new ClassReader(classfileBuffer);
			ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
			ClassVisitor adapter = new MethodLoggerClassAdapter(writer);
			reader.accept(adapter, ClassReader.EXPAND_FRAMES);
			byte[] results = writer.toByteArray();
			return results;
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		}
	}

	private boolean isMatchingPackage(String className) {
		// System.out.println(className);
		for (String possible : inspectPackages) {
			if (className.startsWith(possible)) {
				return true;
			}
		}
		return false;
	}

}
