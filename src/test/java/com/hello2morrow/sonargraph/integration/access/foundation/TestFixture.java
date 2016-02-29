package com.hello2morrow.sonargraph.integration.access.foundation;

public class TestFixture
{
    /**Report containing all available metric levels */
    public static final String TEST_REPORT = "./src/test/resources/AlarmClock.xml";
    public static final String INVALID_TEST_REPORT = "./src/test/resources/AlarmClock_invalid.xml";

    public static final String META_DATA_PATH = "./src/test/resources/ExportMetaData.xml";
    public static final String TEST_REPORT_MULTI_MODULE = "./src/test/resources/AlarmClockMain_Ant.xml";
    public static final String APPLICATION_MODULE = "Application";
    public static final String MODEL_MODULE = "Model";

    public static final String TEST_REPORT_WITHOUT_ISSUES = "./src/test/resources/ReportWithoutIssues.xml";
    public static final String TEST_REPORT_WITHOUT_ELEMENTS = "./src/test/resources/ReportWithoutElements.xml";

    /**
     * Resources to test merge functionality
     */
    public static final String META_DATA_PATH1 = "./src/test/resources/mergeMetaData/ExportMetaData1.xml";
    public static final String META_DATA_PATH1_OLD = "./src/test/resources/mergeMetaData/ExportMetaData1_old.xml";
    public static final String META_DATA_PATH2 = "./src/test/resources/mergeMetaData/ExportMetaData2.xml";
}
