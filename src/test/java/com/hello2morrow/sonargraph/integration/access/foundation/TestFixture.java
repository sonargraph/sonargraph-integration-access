/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.foundation;

public class TestFixture
{
    /**Report containing all available metric levels */
    public static final String TEST_REPORT = "./src/test/resources/AlarmClock.xml";
    public static final String TEST_REPORT_NOT_EXISTING_BASE_PATH = "./src/test/resources/AlarmClock_notExistingBaseDir.xml";
    public static final String INVALID_TEST_REPORT = "./src/test/resources/AlarmClock_invalid.xml";

    public static final String TEST_REPORT_THRESHOLD_VIOLATIONS = "./src/test/resources/AlarmClock_thresholdViolations.xml";
    public static final String TEST_REPORT_WITH_DUPLICATES = "./src/test/resources/DuplicateInSameFileReport.xml";

    public static final String META_DATA_PATH = "./src/test/resources/ExportMetaData.xml";
    public static final String TEST_REPORT_MULTI_MODULE = "./src/test/resources/AlarmClockMain_Ant.xml";
    public static final String APPLICATION_MODULE = "Application";
    public static final String MODEL_MODULE = "Model";

    public static final String TEST_REPORT_WITHOUT_ISSUES = "./src/test/resources/ReportWithoutIssues.xml";
    public static final String TEST_REPORT_WITHOUT_ELEMENTS = "./src/test/resources/ReportWithoutElements.xml";
    public static final String CSHARP_REPORT = "./src/test/resources/NHibernate_2017-04-03_11-59-50.xml";
    public static final String CPP_REPORT = "./src/test/resources/project_2_2017-04-03_14-54-53.xml";
    public static final String CPP_REPORT_HILO = "./src/test/resources/Hilo_2017-04-03_19-08-39.xml";

    /**
     * Resources to test merge functionality
     */
    public static final String META_DATA_PATH1 = "./src/test/resources/mergeMetaData/ExportMetaData1.xml";
    public static final String META_DATA_PATH1_OLD = "./src/test/resources/mergeMetaData/ExportMetaData1_old.xml";
    public static final String META_DATA_PATH2 = "./src/test/resources/mergeMetaData/ExportMetaData2.xml";
}
