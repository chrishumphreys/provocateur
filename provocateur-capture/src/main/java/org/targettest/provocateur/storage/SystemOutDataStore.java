package org.targettest.provocateur.storage;

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

/**
 * A DataStore implementation which echos the test/production information
 * to System.out.
 * 
 * Useful for debugging purposes.
 */

import org.targettest.provocateur.exceptions.StillToImplement;

import java.util.List;

public class SystemOutDataStore implements DataStore {
	
    @Override
    public void close() {
    }

    
    @Override
    public List<String> getAllTestMethods() {
        throw new StillToImplement();

    }

    @Override
    public void insertSrcMethod(String testClassName, String testMethodName, String className, String methodName) {
        System.out.println("Test " + testClassName + " tests " + className);
    }

    @Override
    public List<String> getAllProductionMethods() {
        throw new StillToImplement();

    }

	@Override
	public void initialise(boolean recreate) {
	}

	@Override
	public List<String> getTestClassesForProductionClass(String className) {
		throw new StillToImplement();
	}


	@Override
	public boolean isClosed() {
		return false;
	}
}
