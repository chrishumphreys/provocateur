package org.targettest.calc;

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

import java.util.ArrayList;
import java.util.Collection;

import org.targettest.calc.rules.CalculationStrategy;
import org.targettest.calc.rules.ModifiedHibernateConfigFileRule;
import org.targettest.calc.rules.ModifiedJavaSourceRule;
import org.targettest.calc.rules.ModifiedSpringConfigFileRule;
import org.targettest.calc.rules.ModifiedTestRule;
import org.targettest.calc.scm.ModificationStrategy;
import org.targettest.provocateur.storage.DataStore;
import org.targettest.provocateur.storage.DataStoreFactory;

public class TestCoverageCalculatorFactory {

    public static TestCoverageCalculator createWithModificationStrategy(ModificationStrategy modificationStrategy) {
        return createWithModificationStrategy(modificationStrategy, false);
    }

	public static TestCoverageCalculator createWithModificationStrategy(ModificationStrategy modificationStrategy, boolean ignorePomChanges) {
		Collection<CalculationStrategy> priorityOrderedStrategies = new ArrayList<CalculationStrategy>();
		//TODO This should be configurable...
		priorityOrderedStrategies.add(new ModifiedHibernateConfigFileRule());
        if (!ignorePomChanges) {
            //TODO currently it is the spring rule which matches POM changes
		    priorityOrderedStrategies.add(new ModifiedSpringConfigFileRule());
        }
		priorityOrderedStrategies.add(new ModifiedTestRule());
		
		DataStore dataStore = DataStoreFactory.getDataStore(false);
		
		priorityOrderedStrategies.add(new ModifiedJavaSourceRule(dataStore));
		
		TestCoverageCalculator calculator = new TestCoverageCalculator(modificationStrategy, priorityOrderedStrategies);
		return calculator;
	}

}
