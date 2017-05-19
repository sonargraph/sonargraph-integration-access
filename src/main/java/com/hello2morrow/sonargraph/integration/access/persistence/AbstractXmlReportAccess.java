package com.hello2morrow.sonargraph.integration.access.persistence;

import java.net.URL;

import javax.xml.bind.JAXBElement;

import com.hello2morrow.sonargraph.core.persistence.report.XsdSoftwareSystemReport;

abstract class AbstractXmlReportAccess
{
    private static final String REPORT_NAMESPACE = "com.hello2morrow.sonargraph.core.persistence.report";
    private static final String REPORT_SCHEMA = "com/hello2morrow/sonargraph/core/persistence/report/report.xsd";
    private static final String METADATA_SCHEMA = "com/hello2morrow/sonargraph/core/persistence/report/exportMetaData.xsd";

    protected final JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> createJaxbAdapter(final boolean enableSchemaValidation) throws Exception
    {
        if (enableSchemaValidation)
        {
            final URL reportXsd = getClass().getClassLoader().getResource(REPORT_SCHEMA);
            final URL metricsXsd = getClass().getClassLoader().getResource(METADATA_SCHEMA);

            return new JaxbAdapter<>(REPORT_NAMESPACE, metricsXsd, reportXsd);
        }

        return new JaxbAdapter<>(REPORT_NAMESPACE);
    }
}
