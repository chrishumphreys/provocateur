package org.targettest.suite;

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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A helper class used internal to Provocateur when collecting
 * test classes by name for use in a PatternMatchingSuite run.
 */

final class PatternMatchingClassCollector {
    private static final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    public static Set<Class<?>> getClassesFromPattern(String pattern) {
        return resourcesToClasses(getResourcesFromPattern(pattern));
    }

    private static Set<Class<?>> resourcesToClasses(Resource[] testClassesAsResource) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (Resource resource : testClassesAsResource) {
            ClassMetadata classMetadata = getMetaDataFromResource(resource);

            if (classMetadata != null) {
                classes.add(getClassFromMetaData(classMetadata));
            }
        }
        return classes;
    }

    private static Class<?> getClassFromMetaData(ClassMetadata classMetadata) {
        Class<?> c;
        try {
            if(classLoader() == null) {
                c = Class.forName(classMetadata.getClassName());
            }else {
                c = classLoader().loadClass(classMetadata.getClassName());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return c;
    }

    private static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static ClassMetadata getMetaDataFromResource(Resource resource) {
        MetadataReader metadataReader;
        try {
            metadataReader = metadataReaderFactory.getMetadataReader(resource);
        } catch (IOException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
        return metadataReader.getClassMetadata();
    }

    private static Resource[] getResourcesFromPattern(String locationPattern) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] testClassesAsResource;
        try {
            testClassesAsResource = resolver.getResources(locationPattern);
        } catch (IOException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
        return testClassesAsResource;
    }
}