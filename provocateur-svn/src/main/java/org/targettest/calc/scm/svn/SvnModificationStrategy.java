package org.targettest.calc.scm.svn;

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

import org.targettest.calc.scm.ModificationStrategy;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A ModificationStrategy identifies changed files based on Subversion status.
 * 
 * To use with a ProvocateurPatternMatchingSuite add the annotation
 * @UseModificationStrategy(strategy=SvnModificationStrategy.class, base="/path/to/svnroot")
 * 
 * This strategy requires the local SVN root folder to be specified.
 * 
 * Changes are identified relative to the the SVN root. SVN will 
 * determine changed filepaths relative the the root directory folder.
 * 
 * Provocateur requires filepaths to be converted to classnames.
 * This conversion requires leading source project/folder names to be stripped
 * leaving a filepath which can be converted to a fully qualified classname.
 *  
 * To achieve this the strategy will look for common source folder sub-paths. 
 * A set suitable for Maven style projects is used by default. You can 
 * change this by adding the dir={"somedir","somedir"...} attribute 
 * to the @UseModificationStrategy annotation.  
 */

public class SvnModificationStrategy implements ModificationStrategy {
    private File repositoryRootDirectoryFile;
    private final SVNClientManager clientManager;
    private final String[] sourceFolders;

    public SvnModificationStrategy(String repositoryRootDir, String[] sourceFolders) {
        this(SVNClientManager.newInstance(), repositoryRootDir, sourceFolders);
    }

    public SvnModificationStrategy(SVNClientManager clientManager, String repositoryRootDir, String[] sourceFolders) {
        this.clientManager = clientManager;
        this.sourceFolders = sourceFolders;
        setSvnRoot(repositoryRootDir);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<String> identifyModifications() {
        final List<String> changedFiles = new ArrayList<String>();

        try {
            clientManager.getStatusClient().doStatus(repositoryRootDirectoryFile, SVNRevision.WORKING, SVNDepth.INFINITY, false, true, false, false, new ISVNStatusHandler() {
                public void handleStatus(SVNStatus status) throws SVNException {
                    if (status.getContentsStatus().equals(SVNStatusType.STATUS_MODIFIED) || status.getContentsStatus().equals(SVNStatusType.STATUS_ADDED)) {
                        changedFiles.add(toPath(status.getFile()));
                    }
                }

            }, new ArrayList());

        } catch (SVNException e) {
            e.printStackTrace();
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

    private void setSvnRoot(String repositoryRootDirectory) {
        repositoryRootDirectoryFile = new File(repositoryRootDirectory);

        if (!repositoryRootDirectoryFile.exists()) {
            throw new IllegalArgumentException("repositoryRootDirectory: " + repositoryRootDirectoryFile.getPath() + " does not exist");
        }
        if (!repositoryRootDirectoryFile.isDirectory()) {
            throw new IllegalArgumentException("repositoryRootDirectory: " + repositoryRootDirectoryFile.getPath() + " should be a directory");
        }
    }
}
