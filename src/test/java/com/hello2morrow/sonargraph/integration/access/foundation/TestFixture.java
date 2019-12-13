/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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
    /** Report containing all available metric levels */
    public static final String TEST_REPORT = "./src/test/resources/AlarmClock.xml";
    public static final String INVALID_TEST_REPORT = "./src/test/resources/AlarmClock_invalid.xml";

    public static final String TEST_REPORT_THRESHOLD_VIOLATIONS = "./src/test/resources/AlarmClock_thresholdViolations.xml";
    public static final String TEST_REPORT_WITH_DUPLICATES = "./src/test/resources/DuplicateInSameFileReport.xml";

    public static final String META_DATA_PATH = "./src/test/resources/ExportMetaData.xml";
    public static final String TEST_REPORT_MULTI_MODULE = "./src/test/resources/AlarmClockMain_Ant.xml";
    public static final String APPLICATION_MODULE = "Application";
    public static final String MODEL_MODULE = "Model";

    public static final String REPORT_WITH_PLUGINS = "./src/test/resources/AlarmClock_with_plugins.xml";

    public static final String TEST_REPORT_9_3 = "./src/test/resources/AlarmClock_9.3.0.xml";
    public static final String TEST_REPORT_CLASSFILE_ISSUES = "./src/test/resources/ClassFileIssues.xml";

    public static final String REPORT_WITH_RESOLUTION_PATTERNS = "./src/test/resources/ResolutionsWithPatterns.xml";
    public static final String TEST_REPORT_WITHOUT_ISSUES = "./src/test/resources/ReportWithoutIssues.xml";
    public static final String TEST_REPORT_WITHOUT_ELEMENTS = "./src/test/resources/ReportWithoutElements.xml";
    public static final String TEST_REPORT_WITH_CYCLE_METRICS = "./src/test/resources/AlarmClock_with_cycle_metrics.xml";
    public static final String CSHARP_REPORT = "./src/test/resources/NHibernate_2017-04-03_11-59-50.xml";
    public static final String CPP_REPORT = "./src/test/resources/project_2_2017-04-03_14-54-53.xml";
    public static final String CPP_REPORT_HILO = "./src/test/resources/Hilo_2017-04-03_19-08-39.xml";

    public static final String TEST_REPORT_INTEGRATION_ACCESS_WITH_CYCLE_GROUP = "./src/test/resources/IntegrationAccess_WithCycleGroup.xml";

    public static final String ALARM_CLOCK_CLASS_FILE_ISSUES_REPORT = "./src/test/resources/AlarmClock_ClassFileIssues.xml";

    public static final String TEST_REPORT_STANDARD = "./src/test/resources/ReportStandard.xml";
    public static final String TEST_REPORT_WITH_DERIVED = "./src/test/resources/ReportWithDerived.xml";
    public static final String TEST_REPORT_WITH_PACKAGE_TODO = "./src/test/resources/ReportWithPackageTodo.xml";

    public static final String TEST_REPORT_META_DATA_1 = "./src/test/diff/MetaData_AlarmClockMain_01.xml";
    public static final String TEST_REPORT_META_DATA_2 = "./src/test/diff/MetaData_AlarmClockMain_02.xml";

    public static final String TEST_REPORT_RESOLUTIONS_1 = "./src/test/diff/AlarmClockMain_Resolutions_01.xml";
    public static final String TEST_REPORT_RESOLUTIONS_2 = "./src/test/diff/AlarmClockMain_Resolutions_02.xml";

    public static final String TEST_REPORT_REFACTORINGS = "./src/test/resources/AlarmClockMain_Refactorings.xml";
    public static final String TEST_REPORT_REFACTORINGS_DUPLICATE_FQNAMES = "./src/test/resources/AlarmClockMain_Refactorings_DuplicateFqName.xml";

    public static final String REPORT_WITH_PLUGIN_EXTERNAL = "./src/test/resources/SwaggerTestProject.xml";

    public static final String META_DATA_PATH_WRONG_ENCODING = "./src/test/resources/ExportMetaData_Prolog_Error.xml";
    public static final String META_DATA_PATH_OF_FOUR_LANGUAGES_SYSTEM = "./src/test/data/multilanguage/ExportMetaDataFourLanguages.xml";

    public static final String REPORT_FROM_DIFFERENT_ORIGIN = "./src/test/resources/IntegrationAccess_From_Different_Origin.xml";

    public static final String REPORT_WITH_SYSTEM_METADATA = "./src/test/resources/AlarmClock_with_metadata.xml";
    public static String REPORT_WITH_METRIC_METADATA = "./src/test/resources/AlarmClock_with_metric_metadata.xml";

    /** Reports for testing diff functionality */
    public static final String ALARM_CLOCK_BASELINE_PLUGIN_NOT_EXECUTED = "./src/test/diff/AlarmClock_with_plugin_not_executed.xml";
    public static final String ALARM_CLOCK_PLUGIN_EXECUTED = "./src/test/diff/AlarmClock_with_plugin_executed.xml";

    public static final String ALARM_CLOCK_WITH_WORKSPACE_FILTERS = "./src/test/resources/AlarmClock_with_workspace_filters.xml";
    public static final String ALARM_CLOCK_WITH_WORKSPACE_FILTERS_CHANGED = "./src/test/resources/AlarmClock_with_workspace_filters_changed.xml";
}