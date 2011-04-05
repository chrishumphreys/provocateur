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
