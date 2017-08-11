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

import javax.xml.bind.JAXBElement;

import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdExportMetaDataRoot;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;

abstract class XmlAccess
{
    public static final String NAMESPACE = "com.hello2morrow.sonargraph.integration.access.persistence.report";
    public static final String REPORT_XSD = "com/hello2morrow/sonargraph/integration/access/persistence/report/report.xsd";
    public static final String EXPORT_METADATA_XSD = "com/hello2morrow/sonargraph/integration/access/persistence/report/exportMetaData.xsd";

    protected XmlAccess()
    {
        super();
    }

    protected final JaxbAdapter<JAXBElement<XsdExportMetaDataRoot>> createExportMetaDataJaxbAdapter() throws XmlProcessingException
    {
        return new JaxbAdapter<>(NAMESPACE, getClass().getClassLoader().getResource(EXPORT_METADATA_XSD));
    }

    protected final JaxbAdapter<JAXBElement<XsdSoftwareSystemReport>> createReportJaxbAdapter() throws Exception
    {
        //No XML validation for reports - no URL parameter used 
        return new JaxbAdapter<>(NAMESPACE);
    }
}