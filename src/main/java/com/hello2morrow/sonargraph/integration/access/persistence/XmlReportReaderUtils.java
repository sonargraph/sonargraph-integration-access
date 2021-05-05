/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementKind;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdNamedElement;

public class XmlReportReaderUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlReportReaderUtils.class);

    private XmlReportReaderUtils()
    {
        //utility
    }

    static XsdElementKind getXsdElementKind(final XsdNamedElement xsdNamedElement)
    {
        assert xsdNamedElement != null : "Parameter 'xsdNamedElement' of method 'getXsdElementKind' must not be null";

        final Object kind = xsdNamedElement.getKind();
        if (kind == null)
        {
            final String msg = "No associated element kind found for named element: " + xsdNamedElement.getFqName();
            LOGGER.error(msg);
            throw new NullPointerException(msg);
        }
        if (kind instanceof XsdElementKind == false)
        {
            final String msg = "Associated element kind is of wrong class '" + kind.getClass().getName() + "' for named element: "
                    + xsdNamedElement.getFqName();
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return (XsdElementKind) kind;
    }
}
