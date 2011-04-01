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


import org.targettest.org.objectweb.asm.ClassAdapter;
import org.targettest.org.objectweb.asm.ClassVisitor;
import org.targettest.org.objectweb.asm.MethodVisitor;
import org.targettest.org.objectweb.asm.Opcodes;


public class MethodLoggerClassAdapter extends ClassAdapter {

	private boolean isInterface;
	private String clazzName;
	private boolean isJunit3Class;

	public MethodLoggerClassAdapter(ClassVisitor cv) {
		super(cv);
	}
	
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		this.clazzName = name;
		cv.visit(version, access, name, signature, superName, interfaces);
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		isJunit3Class = isSuperJunit3TestCase(superName);
	}

	private boolean isSuperJunit3TestCase(String superName) {
		return "junit/framework/TestCase".equals(superName);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, final String name, String desc,
			String signature, String[] exceptions) {

		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

		if (!isInterface && mv != null && !name.equals("<init>")) {
//			System.out.println("Visiting "+name);
            mv= new MethodLoggerAdviceAdapter(mv, access, name, desc, clazzName, isJunit3Class);
		}
		return mv;
	}

	public boolean isJunit3TestClass() {
		return isJunit3Class;
	}

}
