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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.targettest.org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.targettest.org.apache.lucene.document.Document;
import org.targettest.org.apache.lucene.document.Field;
import org.targettest.org.apache.lucene.index.CorruptIndexException;
import org.targettest.org.apache.lucene.index.IndexReader;
import org.targettest.org.apache.lucene.index.IndexWriter;
import org.targettest.org.apache.lucene.queryParser.QueryParser;
import org.targettest.org.apache.lucene.search.IndexSearcher;
import org.targettest.org.apache.lucene.search.Query;
import org.targettest.org.apache.lucene.search.ScoreDoc;
import org.targettest.org.apache.lucene.search.TopScoreDocCollector;
import org.targettest.org.apache.lucene.store.Directory;
import org.targettest.org.apache.lucene.store.FSDirectory;
import org.targettest.org.apache.lucene.util.Version;

public class LuceneDataStore implements DataStore {

	private static final String TEST_CLASS_FIELD = "testClass";
	private static final String SRC_CLASS_FIELD = "srcClass";
	private static final int MAX_HITS = 1000;
	private static final boolean USING_TOP_DOCS = false;
	private StandardAnalyzer analyzer;
	private IndexWriter iwriter;
	private Directory directory;
	
	@Override
	public void close() {
		try {
			iwriter.close();
			iwriter = null;
			directory.close();
			directory = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialise(boolean recreate) {
		try {
			analyzer = new StandardAnalyzer(Version.LUCENE_30);
			// Directory directory = new RAMDirectory();
			directory = FSDirectory.open(new File("/tmp/testindex"));
			iwriter = new IndexWriter(directory, analyzer, recreate,
					new IndexWriter.MaxFieldLength(25000));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to initialise LuceneStore", e);
		}
	}

	@Override
	public void insertSrcMethod(String testClassName, String testMethodName, String className, String methodName) {
		try {
			Document doc = new Document();
			doc.add(new Field(TEST_CLASS_FIELD, testClassName, Field.Store.YES,
					Field.Index.ANALYZED));
			doc.add(new Field(SRC_CLASS_FIELD, className, Field.Store.YES,
					Field.Index.ANALYZED));
			iwriter.addDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Unable to write src method into Lucene", e);
		}

	}

	@Override
	public List<String> getAllTestMethods() {
		return unique(getAllFields(TEST_CLASS_FIELD));
	}

	private List<String> getAllFields(String fieldname) {
		try {
			IndexReader reader = IndexReader.open(directory, true);
			int numDocs = reader.numDocs();

			List<String> results = new ArrayList<String>();
			for (int d = 0; d < numDocs; d++) {
				Document docs = reader.document(d);
				String s = docs.get(fieldname);
				results.add(s);
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getAllProductionMethods() {
		return unique(getAllFields(SRC_CLASS_FIELD));
	}

	private List<String> unique(List<String> allFields) {
		return new ArrayList<String>(new HashSet<String>(allFields));
	}

	@Override
	public List<String> getTestClassesForProductionClass(String className) {
		return query(SRC_CLASS_FIELD, className);
	}

	private List<String> query(String fieldName, String queryStr) {
		List<String> names = new ArrayList<String>();
		IndexSearcher isearcher = null;
		try {
			isearcher = new IndexSearcher(directory, true);
			QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName,
					analyzer);
			Query query = parser.parse(queryStr);
			// Could use Lucene's scores to present the best matches...
			if (USING_TOP_DOCS) {
				queryUsingTopDocs(names, isearcher, query);
			} else {
				queryUsingAllDocs(names, isearcher, query);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to find classes for src", e);
		} finally {
			if (isearcher != null) {
				try {
					isearcher.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return names;
	}

	private void queryUsingAllDocs(List<String> names, IndexSearcher isearcher,
			Query query) throws IOException, CorruptIndexException {
		AllResultsCollector collector = new AllResultsCollector();
		isearcher.search(query, collector);
		for (int i = 0; i < collector.docs().size(); i++) {
			Document hitDoc = isearcher.doc(collector.docs().get(i));
			names.add(hitDoc.getField(TEST_CLASS_FIELD).stringValue());
		}
	}

	private void queryUsingTopDocs(List<String> names, IndexSearcher isearcher,
			Query query) throws IOException, CorruptIndexException {
		TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_HITS,
				true);
		isearcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			names.add(hitDoc.getField(TEST_CLASS_FIELD).stringValue());
		}
	}
	
	@Override
	public boolean isClosed() {
		return directory == null;
	}
}
