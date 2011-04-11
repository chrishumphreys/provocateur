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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.junit.Assert;
import org.junit.Test;

public class GitModificationStrategyTest {

    @Test
    public void testValidationFailsAsExpectedForNonExistantFile() {
        try {
            new GitModificationStrategy("mkvlfmklfkfmdl", new String[]{""});
        } catch (IllegalArgumentException expectedException) {
            assertThat(expectedException.getMessage(), is("repositoryRootDirectory: mkvlfmklfkfmdl does not exist"));
        }
    }

    @Test
    public void testValidationFailsAsExpectedForNonDirectoryFile() {
        String filePath = this.getClass().getResource("/aFile.txt").getFile();
        try {
            new GitModificationStrategy(filePath, new String[]{""});
        } catch (IllegalArgumentException expectedException) {
            assertThat(expectedException.getMessage(), is("repositoryRootDirectory: " + filePath + " should be a directory"));
        }
    }

    @SuppressWarnings("serial")
	@Test
    public void testConvertsModifiedFileAbsoluteToRelativePathsAndStripsSrcFolders() throws Exception {
        String srcFolder = "/src/main/java";
        String relativeFile = "/dir/anotherFile.txt";

        final String sampleFilePath = this.getClass().getResource("/testgitroot" + srcFolder + relativeFile).getFile();
        String gitRoot = new File(this.getClass().getResource("/testgitroot/gitRoot.txt").getFile()).getParent();

        Git mockGit = mock(Git.class);
        StatusCommand mockStatusCommand = mock(StatusCommand.class);
        when(mockGit.status()).thenReturn(mockStatusCommand);
        Status mockStatus = mock(Status.class);
        when(mockStatusCommand.call()).thenReturn(mockStatus);
        when(mockStatus.getChanged()).thenReturn(new HashSet<String>() {{
            add(sampleFilePath);
        }});
        GitModificationStrategy git = new GitModificationStrategy(mockGit, gitRoot, new String[]{"/src/main/java"});



        List<String> results = git.identifyModifications();
        Assert.assertEquals(relativeFile, results.get(0));
    }

}
