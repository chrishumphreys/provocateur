package org.targettest.calc.scm;

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

/**
 * A ProvocateurPatternMatchingSuite uses an SCM strategy
 * to determine the production code change set. This set
 * of files is then used by Provocateur to filter the 
 * suite contents.
 * 
 * Different strategies are available for different
 * source control systems.
 * 
 * It is not normal to use this directly in your
 * client code, instead configure one of the available
 * concrete implementations for your ProvocateurPatternMatchingSuite
 * using the @UseModificationStrategy annotation.
 * For example:
 * <pre>
 * {@code 
 *   @UseModificationStrategy(strategy=SvnModificationStrategy.class)
 * }
 * </pre>
 */

public interface ModificationStrategy {

	List<String> identifyModifications();
}
