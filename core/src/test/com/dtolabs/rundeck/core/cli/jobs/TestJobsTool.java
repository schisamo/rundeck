/*
 * Copyright 2010 DTO Labs, Inc. (http://dtolabs.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dtolabs.rundeck.core.cli.jobs;
/*
* TestJobsTool.java
* 
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: Mar 29, 2010 4:21:29 PM
* $Id$
*/

import com.dtolabs.rundeck.core.cli.CLIToolOptionsException;
import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.dispatcher.*;
import com.dtolabs.rundeck.core.tools.AbstractBaseTest;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class TestJobsTool extends AbstractBaseTest {
    JobsTool jobsTool;

    public TestJobsTool(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestJobsTool.class);
    }

    protected void setUp() {
        super.setUp();

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    static class testCentralDispatcher1 implements CentralDispatcher {
        boolean listStoredJobsCalled = false;
        boolean loadJobsCalled = false;
        ILoadJobsRequest loadRequest;
        java.io.File loadInput;
        IStoredJobsQuery listStoredJobsQuery;
        OutputStream listStoredJobsOutput;
        Collection<IStoredJob> listJobsResult;
        Collection<IStoredJobLoadResult> loadJobsResult;


        public QueuedItemResult queueDispatcherJob(IDispatchedJob job) throws CentralDispatcherException {
            fail("unexpected call to queueDispatcherJob");
            return null;
        }

        public QueuedItemResult queueDispatcherScript(final IDispatchedScript dispatch) throws
            CentralDispatcherException {
            fail("unexpected call to queueDispatcherScript");
            return null;
        }

        public Collection<QueuedItem> listDispatcherQueue() throws CentralDispatcherException {
            //
            fail("unexpected call to listDispatcherQueue");
            return null;
        }

        public DispatcherResult killDispatcherExecution(final String id) throws CentralDispatcherException {
            fail("unexpected call to killDispatcherExecution");
            return null;
        }

        public Collection<IStoredJobLoadResult> loadJobs(ILoadJobsRequest request, java.io.File input) throws
            CentralDispatcherException {
            this.loadJobsCalled = true;
            this.loadRequest = request;
            this.loadInput = input;
            return loadJobsResult;
        }

        public Collection<IStoredJob> listStoredJobs(IStoredJobsQuery query, OutputStream output) throws
            CentralDispatcherException {
            this.listStoredJobsCalled = true;
            this.listStoredJobsQuery = query;
            this.listStoredJobsOutput = output;
            return listJobsResult;
        }
    }

    public void testRun() throws Exception {
        final Framework framework = getFrameworkInstance();
        {
            //test list action
            //test null  result

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            try {
                tool.run(new String[]{"list"});
                fail("run should fail");
            } catch (JobsToolException e) {
                assertTrue(e.getMessage().startsWith("List request returned null"));
            }
        }
        {
            //test list action
            //test 0 items result

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(new String[]{"list"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertNull(centralDispatcher1.listStoredJobsOutput);
        }
        {
            //test list action with output file
            //test 0 items result

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);
            File t = File.createTempFile("TestJobsTool", "xml");
            t.deleteOnExit();

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(new String[]{"list", "-f", t.getAbsolutePath()});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertNotNull(centralDispatcher1.listStoredJobsOutput);
        }
        {
            //test list action with query params, -n

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(new String[]{"list", "-" + JobsTool.NAME_OPTION, "name1"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNull(centralDispatcher1.listStoredJobsOutput);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertEquals("name1", centralDispatcher1.listStoredJobsQuery.getNameMatch());
        }
        {
            //test list action with query params, --name

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(new String[]{"list", "--" + JobsTool.NAME_OPTION_LONG, "name1"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNull(centralDispatcher1.listStoredJobsOutput);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertEquals("name1", centralDispatcher1.listStoredJobsQuery.getNameMatch());
        }
        {
            //test list action with query params, -g

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(new String[]{"list", "-" + JobsTool.GROUP_OPTION, "group1"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNull(centralDispatcher1.listStoredJobsOutput);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertEquals("group1", centralDispatcher1.listStoredJobsQuery.getGroupMatch());
        }
        {
            //test list action with query params, --group

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(new String[]{"list", "--" + JobsTool.GROUP_OPTION_LONG, "group2"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNull(centralDispatcher1.listStoredJobsOutput);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertEquals("group2", centralDispatcher1.listStoredJobsQuery.getGroupMatch());
        }
        {
            //test list action with query params, -i

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(
                new String[]{"list",  "-" + JobsTool.IDLIST_OPTION, "1,2"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNull(centralDispatcher1.listStoredJobsOutput);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertNull(centralDispatcher1.listStoredJobsQuery.getCommand());
            assertNull( centralDispatcher1.listStoredJobsQuery.getType());
            assertNull( centralDispatcher1.listStoredJobsQuery.getResource());
            assertEquals("1,2", centralDispatcher1.listStoredJobsQuery.getIdlist());
        }
        {
            //test list action with query params, --idlist

            final JobsTool tool = new JobsTool(framework);
            final testCentralDispatcher1 centralDispatcher1 = new testCentralDispatcher1();
            framework.setCentralDispatcherMgr(centralDispatcher1);

            final ArrayList<IStoredJob> jobs = new ArrayList<IStoredJob>();
            centralDispatcher1.listJobsResult = jobs;

            tool.run(
                new String[]{"list", "--" + JobsTool.IDLIST_OPTION_LONG, "3,4"});
            assertTrue("list action was not called", centralDispatcher1.listStoredJobsCalled);
            assertFalse("load action should not be called", centralDispatcher1.loadJobsCalled);
            assertNull(centralDispatcher1.listStoredJobsOutput);
            assertNotNull(centralDispatcher1.listStoredJobsQuery);
            assertNull( centralDispatcher1.listStoredJobsQuery.getCommand());
            assertNull( centralDispatcher1.listStoredJobsQuery.getType());
            assertNull( centralDispatcher1.listStoredJobsQuery.getResource());
            assertEquals("3,4", centralDispatcher1.listStoredJobsQuery.getIdlist());
        }
    }

    public void testParseArgs() throws Exception {
        {
            //test invalid action
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            try {
                final String[] args = {"invalid"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
                fail("invalid action should have failed");
            } catch (CLIToolOptionsException e) {
                assertNotNull(e);
            }

        }
        {
            //test valid actions missing require options
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            try {
                final String[] args = {"load"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
                fail("should have thrown missing argument exception.");
            } catch (CLIToolOptionsException e) {
                assertNotNull(e);
            }
        }
        {
            //test valid actions, with invalid option
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            try {
                final String[] args = {"load", "-d", "zamboni"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
                fail("should have thrown missing argument exception.");
            } catch (CLIToolOptionsException e) {
                assertNotNull(e);
                assertTrue("Wrong message: " + e.getMessage(), e.getMessage().startsWith(
                    "Illegal value for --duplicate"));
            }
        }
        {
            //test valid actions, missing required -f
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            try {
                final String[] args = {"load", "-d", "update"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
                fail("should have thrown missing argument exception.");
            } catch (CLIToolOptionsException e) {
                assertNotNull(e);
                assertTrue("wrong message:" + e.getMessage(), e.getMessage().startsWith(
                    "load action: -f/--file option is required"));
            }
        }
        {
            //test valid actions,  -f points to DNE file
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            try {
                final String[] args = {"load", "-f", "doesnotexist"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
                fail("should have thrown missing argument exception.");
            } catch (CLIToolOptionsException e) {
                assertNotNull(e);
                assertTrue("wrong message:" + e.getMessage(), e.getMessage().startsWith(
                    "load action: -f/--file option: File does not exist"));
            }
        }
        {
            //test valid actions
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            boolean success = false;
            try {
                final String[] args = {"list", "-n", "test1"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
                success = true;
            } catch (CLIToolOptionsException e) {
                fail("unexpected exception: " + e.getMessage());
            }
            assertTrue("parseArgs did not succeed", success);

        }
        {
            //test valid actions
            final JobsTool tool = new JobsTool(getFrameworkInstance());
            try {
                final String[] args = {"list"};
                final CommandLine line = tool.parseArgs(args);
                tool.validateOptions(line, args);
            } catch (CLIToolOptionsException e) {
                fail("unexpected exception: " + e.getMessage());
            }

        }
    }
}