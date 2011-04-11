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


import java.util.List;

public interface DataStore {

    /**
     * @return Has the datastore successfully shutdown
     */
    boolean isClosed();

    /**
     * Gracefully shutdown the datastore
     */
	void close();

    /**
     * Start up the datastore.
     * @param recreate create a new index or overwrite the existing one
     */
    void initialise(boolean recreate);

    /**
     * Insert a test invocation -> production class mapping into the datastore
     * @param testClassName name of the test class
     * @param testMethodName name of the test method
     * @param className name of the "production" class
     * @param methodName name of the "production" method
     */
    void insertSrcMethod(String testClassName, String testMethodName, String className, String methodName);

    /**
     * @return A list of all test methods
     */
    List<String> getAllTestMethods();

    /**
     * @return A list of all production methods
     */
    List<String> getAllProductionMethods();


    /**
     * Returns a list of all test classes that exercise the prodction class.
     * @param className the name of the production class that we want to validate.
     * @return all the known tests that exercise the supplied production class.
     */
    List<String> getTestClassesForProductionClass(String className);
}
