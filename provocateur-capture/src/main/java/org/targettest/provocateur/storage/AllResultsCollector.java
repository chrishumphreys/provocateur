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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.targettest.org.apache.lucene.index.IndexReader;
import org.targettest.org.apache.lucene.search.Collector;
import org.targettest.org.apache.lucene.search.Scorer;

public class AllResultsCollector extends Collector {

	private List<Integer> docs = new ArrayList<Integer>();

	@Override
	public void setScorer(Scorer scorer) throws IOException {
	}

	@Override
	public void collect(int doc) throws IOException {
		docs.add(doc);
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase)
			throws IOException {
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

	public List<Integer> docs() {
		return docs;
	}
}
