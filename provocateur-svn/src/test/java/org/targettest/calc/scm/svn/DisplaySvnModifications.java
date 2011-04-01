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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import java.io.File;
import java.util.ArrayList;

public class DisplaySvnModifications {
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("No svn root specified");
            System.exit(1);
        }
        String url = args[0];

        File localRepositoryDirectory = new File(url);

        System.out.println("exists? " + localRepositoryDirectory.exists());

        SVNClientManager clientManager = SVNClientManager.newInstance();

        try {
            clientManager.getStatusClient().doStatus(localRepositoryDirectory,
                    SVNRevision.WORKING, SVNDepth.INFINITY, false, true, false,
                    false, new ISVNStatusHandler() {
                        public void handleStatus(SVNStatus status)
                                throws SVNException {
                            if (status.getContentsStatus().equals(
                                    SVNStatusType.STATUS_MODIFIED) || status.getContentsStatus().equals(SVNStatusType.STATUS_ADDED)) {
                                System.out.println(status.getFile()
                                        .getAbsolutePath());
                            }
                        }
                    }, new ArrayList());

        } catch (SVNException e) {
            e.printStackTrace();
        }

    }
}