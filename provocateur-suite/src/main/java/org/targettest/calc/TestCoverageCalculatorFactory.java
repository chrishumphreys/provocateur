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
import org.targettest.calc.rules.ModifiedPomFileRule;
import org.targettest.calc.rules.ModifiedSpringConfigFileRule;
import org.targettest.calc.rules.ModifiedTestRule;
import org.targettest.calc.scm.ModificationStrategy;
import org.targettest.provocateur.storage.DataStore;
import org.targettest.provocateur.storage.DataStoreFactory;

/**
 * A factory to construct a fully configured TestCoverageCalculator using supplied arguments
 * and default behaviour.
 * 
 * The test suite code uses this to create the TestCoverageCalculator for use.
 *
 */

public class TestCoverageCalculatorFactory {

	/**
	 * Create a TestCoverageCalculator uses the default set of rules and the supplied ModificationStrategy 
	 */
    public static TestCoverageCalculator createWithModificationStrategy(ModificationStrategy modificationStrategy) {
        return createWithModificationStrategy(modificationStrategy, false);
    }

    
    /**
     * Create a TestCoverageCalculator uses the default set of rules and the supplied ModificationStrategy but
     * with the POM rule disabled.
     * 
     * This is only useful in debugging Provocateur and not for normal usage.
     * 
     * The POM rule detects a changed Maven POM file and instructs Provocateur to run all tests. The 
     * rationale behind this rule is that if you have a Maven project and you have changed its POM file
     * then the effects can be significant so you should run all tests. By default this rule is enabled.
     * 
     * However when debugging Provocateur or experimenting with applying it to your project you normally
     * need to edit your POM file to enable Provocateur. With this rule applied you would not be able
     * experiment with Provocateur filtering test suites as it would always detect your changed POM. In
     * this circumstance you can pass the ignorePomChanges attribute.
     *  
     */
	public static TestCoverageCalculator createWithModificationStrategy(ModificationStrategy modificationStrategy, boolean ignorePomChanges) {
		Collection<CalculationStrategy> priorityOrderedStrategies = new ArrayList<CalculationStrategy>();
		//TODO This should be configurable...
		priorityOrderedStrategies.add(new ModifiedHibernateConfigFileRule());
		priorityOrderedStrategies.add(new ModifiedSpringConfigFileRule());
		
        if (!ignorePomChanges) {
            //TODO currently it is the spring rule which matches POM changes
		    priorityOrderedStrategies.add(new ModifiedPomFileRule());
        }
		priorityOrderedStrategies.add(new ModifiedTestRule());
		
		DataStore dataStore = DataStoreFactory.getDataStore(false);
		
		priorityOrderedStrategies.add(new ModifiedJavaSourceRule(dataStore));
		
		TestCoverageCalculator calculator = new TestCoverageCalculator(modificationStrategy, priorityOrderedStrategies);
		return calculator;
	}

}
