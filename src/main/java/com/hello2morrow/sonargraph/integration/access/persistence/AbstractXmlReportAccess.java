/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.persistence;

import java.net.URL;

import javax.xml.bind.JAXBElement;

import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;

abstract class AbstractXmlReportAccess
{
    private static final String REPORT_NAMESPACE = "com.hello2morrow.sonargraph.integration.access.persistence.report";
    private static final String REPORT_SCHEMA = "com/hello2morrow/sonargraph/integration/access/persistence/report/report.xsd";
    private static final String METADATA_SCHEMA = "com/hello2morrow/sonargraph/integration/access/persistence/report/exportMetaData.xsd";

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
