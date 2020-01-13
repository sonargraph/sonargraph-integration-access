package com.hello2morrow.sonargraph.integration.access.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;

public class XmlElementContentExtractorTest
{
    private static final String REPORT_CONTEXT_INFO = "reportContextInfo";

    @Test
    public void extractAttribute()
    {
        final String contextInfo = XmlElementContentExtractor.process(new File("./src/test/resources/ReportWithContextInfo.xml"),
                REPORT_CONTEXT_INFO);
        assertEquals("Wrong context info", "Multi-\nline\ncontext\ninfo", contextInfo);

        assertNull(XmlElementContentExtractor.process(new File("./src/test/resources/ReportStandard.xml"), REPORT_CONTEXT_INFO));
    }
}