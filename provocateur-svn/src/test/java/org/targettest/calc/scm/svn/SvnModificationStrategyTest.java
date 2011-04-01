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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;

public class SvnModificationStrategyTest {

    @Test
    public void testValidationFailsAsExpectedForNonExistantFile() {
        try {
            new SvnModificationStrategy("mkvlfmklfkfmdl", new String[] {""});
        } catch (IllegalArgumentException expectedException) {
            assertThat(expectedException.getMessage(), is("repositoryRootDirectory: mkvlfmklfkfmdl does not exist"));
        }
    }

    @Test
    public void testValidationFailsAsExpectedForNonDirectoryFile() {
        String filePath = this.getClass().getResource("/aFile.txt").getFile();
        try {
            new SvnModificationStrategy(filePath, new String[] {""});
        } catch (IllegalArgumentException expectedException) {
            assertThat(expectedException.getMessage(), is("repositoryRootDirectory: " + filePath + " should be a directory"));
        }
    }

    @Test
    public void testConvertsModifiedFileAbsoluteToRelativePathsAndStripsSrcFolders() {
        SVNClientManager mockSvnClient = mock(SVNClientManager.class);
        String srcFolder = "/src/main/java";
        String relativeFile = "/dir/anotherFile.txt";
        
        String sampleFilePath = this.getClass().getResource("/testsvnroot" + srcFolder + relativeFile).getFile();
        String svnRoot = new File(this.getClass().getResource("/testsvnroot/svnRoot.txt").getFile()).getParent();

        SvnModificationStrategy svn = new SvnModificationStrategy(mockSvnClient, svnRoot, new String[] {"/src/main/java"});
        SVNStatus mockStatus = mock(SVNStatus.class);
        when(mockStatus.getContentsStatus()).thenReturn(SVNStatusType.STATUS_MODIFIED);
        when(mockStatus.getFile()).thenReturn(new File(sampleFilePath));
        when(mockSvnClient.getStatusClient()).thenReturn(new StubbedStatusClient(mockStatus));
        List<String> results = svn.identifyModifications();
        Assert.assertEquals(relativeFile, results.get(0));
    }

    public static class StubbedStatusClient extends SVNStatusClient {

        private final SVNStatus mockStatus;

        public StubbedStatusClient(SVNStatus mockStatus) {
            super((ISVNAuthenticationManager) null, null);
            this.mockStatus = mockStatus;
        }

        @SuppressWarnings("rawtypes")
		@Override
        public long doStatus(java.io.File path,
                             SVNRevision revision,
                             org.tmatesoft.svn.core.SVNDepth depth,
                             boolean remote,
                             boolean reportAll,
                             boolean includeIgnored,
                             boolean collectParentExternals,
                             ISVNStatusHandler handler,
                             Collection changeLists) throws org.tmatesoft.svn.core.SVNException {
            handler.handleStatus(mockStatus);
            return 1;


        }
    }
}
