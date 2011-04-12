package org.targettest.provocateur.storage;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * A factory for the Singleton DataStore instance.
 * 
 * By default a LuceneDataStore is created which stores test/production
 * code information in a local on-disk Lucene index.
 * 
 * The type of the DataStore can be controlled using the PROVBACK
 * environment variable, e.g. by using -DPROVBACK=SystemOutDataStore
 *  
 */
public class DataStoreFactory {

	private static final String PROVOCATEUR_BACKEND = "PROVBACK";
	private static final String DEFAULT_BACKEND = "LuceneDataStore";

	private static DataStore currentInstance;

	public synchronized static DataStore getDataStore(boolean recreate) {
		if (currentInstance == null || currentInstance.isClosed()) {
			DataStore ds;
			String backendProp = System.getProperty(PROVOCATEUR_BACKEND,
					DEFAULT_BACKEND);
			if ("SystemOutDataStore".equals(backendProp)) {
				ds = new SystemOutDataStore();
			} else if ("LuceneDataStore".equals(backendProp)) {
				ds = new LuceneDataStore();
			} else {
				throw new IllegalArgumentException(
						"Unknown DataStore configured: " + backendProp);
			}
			ds.initialise(recreate);
			currentInstance = ds;
		}
		return currentInstance;
	}
	
	public synchronized static void close() {
		if (currentInstance != null) {
			currentInstance.close();
			currentInstance = null;
		}
	}
}
