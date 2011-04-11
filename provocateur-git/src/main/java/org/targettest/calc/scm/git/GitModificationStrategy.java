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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.targettest.calc.scm.ModificationStrategy;

public class GitModificationStrategy implements ModificationStrategy {
    private File repositoryRootDirectoryFile;
    private Git git;
    private final String[] sourceFolders;


    public GitModificationStrategy(String repositoryRootDir, String[] sourceFolders)  {
        this(null,repositoryRootDir,sourceFolders);

        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        try {
            Repository repository = repositoryBuilder.findGitDir(repositoryRootDirectoryFile).build();
            this.git = new Git(repository);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public GitModificationStrategy(Git git, String repositoryRootDir, String[] sourceFolders) {
        gitRoot(repositoryRootDir);
        this.sourceFolders = sourceFolders;
        this.git = git;

    }


    @Override
    public List<String> identifyModifications() {
        final List<String> changedFiles = new ArrayList<String>();


        Status status = null;
        try {
            status = git.status().call();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String fileName : status.getChanged()) {
            changedFiles.add(toPath(new File(fileName)));

        }
        for (String fileName : status.getAdded()) {
            changedFiles.add(toPath(new File(fileName)));

        }

        System.out.println("Returning " + changedFiles + " modifications.");
        return changedFiles;
    }


    private String toPath(File file) {
        String relativeToRoot = file.getAbsolutePath().split(repositoryRootDirectoryFile.getAbsolutePath())[1];
        for (String folder : sourceFolders) {
            if (relativeToRoot.startsWith(folder)) {
                return relativeToRoot.substring(folder.length());
            }
        }
        return relativeToRoot;
    }

    private File gitRoot(String repositoryRootDirectory) {
        repositoryRootDirectoryFile = new File(repositoryRootDirectory);

        if (!repositoryRootDirectoryFile.exists()) {
            throw new IllegalArgumentException("repositoryRootDirectory: " + repositoryRootDirectoryFile.getPath() + " does not exist");
        }
        if (!repositoryRootDirectoryFile.isDirectory()) {
            throw new IllegalArgumentException("repositoryRootDirectory: " + repositoryRootDirectoryFile.getPath() + " should be a directory");
        }

        return repositoryRootDirectoryFile;
    }
}
