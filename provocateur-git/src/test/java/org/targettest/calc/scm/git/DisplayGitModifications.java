package org.targettest.calc.scm.git;

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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class DisplayGitModifications {
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws IOException, NoHeadException {


        if (args.length != 1) {
            System.err.println("No git root specified");
            System.exit(1);
        }
        String url = args[0];

        File localRepositoryDirectory = new File(url);

        RepositoryBuilder builder = new RepositoryBuilder();

        Repository repository = builder.findGitDir(localRepositoryDirectory).build();
        Git git = new Git(repository);
        Status status = git.status().call();
        Set<String> changed = status.getAdded();

        for (String changedFile : changed) {
            System.out.println(changedFile);
        }

    }
}

