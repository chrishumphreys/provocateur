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


import org.targettest.org.objectweb.asm.AnnotationVisitor;
import org.targettest.org.objectweb.asm.Label;
import org.targettest.org.objectweb.asm.MethodVisitor;
import org.targettest.org.objectweb.asm.commons.AdviceAdapter;

/**
 * An ASM Method adapter used to inject our profiling instrumentation
 * into a Java class at runtime.
 * 
 * Note the bytecode of the class is modified to include calls to our
 * MethodLogger 'Aspect' but this code is not actually executed until
 * the instrumented Java class is used.
 * 
 * The purpose of this ASM Method adapter is to inject hooks for 
 * every method enter and exit within every Java class under
 * inspection.
 * 
 * These hooks delegate onto the MethodLoggerAdvice class to perform
 * the actual work. The profiling code must determine if a method is
 * a test method or production code. It does this by checking if the
 * method is within a JUnit3 class or if the method has been 
 * annotated by a JUnit4 @Test annotation. This determination
 * is passed to the MethodLoggerAdvice so it can process the method
 * correctly. 
 * 
 * Note the bytecode to inject can be calculated using the 
 * org.undertest.tools.ClassDumper tool.
 */

public class MethodLoggerAdviceAdapter extends AdviceAdapter {

	private final String name;
	private final String clazzName;
	private final boolean isJunit3Class;
	private boolean isJunit4Method;

	private static final boolean DEBUG = false;
	private static final boolean APPLY = true;

	protected MethodLoggerAdviceAdapter(MethodVisitor mv, int access,
			String name, String desc, String clazzName, boolean isJunit3Class) {
		super(mv, access, name, desc);
		this.name = name;
		this.clazzName = clazzName;
		this.isJunit3Class = isJunit3Class;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (isJunit4Method(desc)) {
			isJunit4Method = true;
		}
		return super.visitAnnotation(desc, visible);
	}
	
	@Override
	protected void onMethodEnter() {
		if (DEBUG) {
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitLdcInsn("Entering method " + name);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					"(Ljava/lang/String;)V");
		}
		if (APPLY) {
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLdcInsn(clazzName);
			mv.visitLdcInsn(name);
			mv.visitInsn(isTestMethod()?ICONST_1:ICONST_0);
			mv.visitMethodInsn(INVOKESTATIC,
					"org/targettest/provocateur/hook/MethodLoggerAdvice",
					"onMethodEnter", "(Ljava/lang/String;Ljava/lang/String;Z)V");
		}
	}

	private boolean isTestMethod() {
		return (isJunit3Class && isJunit3TestMethod()) || isJunit4Method;
	}

	private boolean isJunit3TestMethod() {
		return name.startsWith("test");
	}
	
	private boolean isJunit4Method(String desc) {
		return "Lorg/junit/Test;".equals(desc);
	}

	@Override
	protected void onMethodExit(int arg0) {
		if (DEBUG) {
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
					"Ljava/io/PrintStream;");
			mv.visitLdcInsn("Exiting method " + name);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
					"(Ljava/lang/String;)V");
		}
		if (APPLY) {
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLdcInsn(clazzName);
			mv.visitLdcInsn(name);
			mv.visitInsn(isTestMethod()?ICONST_1:ICONST_0);
			mv.visitMethodInsn(INVOKESTATIC,
					"org/targettest/provocateur/hook/MethodLoggerAdvice",
					"onMethodExit", "(Ljava/lang/String;Ljava/lang/String;Z)V");
		}
		isJunit4Method = false;
	}

}
