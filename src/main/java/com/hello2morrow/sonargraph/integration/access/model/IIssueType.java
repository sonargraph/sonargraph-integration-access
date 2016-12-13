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
package com.hello2morrow.sonargraph.integration.access.model;

import com.hello2morrow.sonargraph.integration.access.foundation.IStandardEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;

public interface IIssueType extends IElement
{
    public enum StandardName implements IStandardEnumeration
    {
        ARCHITECTURE_VIOLATION("Architecture Violation");

        private final String presentationName;

        private StandardName(final String presentationName)
        {
            assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'StandardMetricNames' must not be empty";
            this.presentationName = presentationName;
        }

        @Override
        public String getStandardName()
        {
            return StringUtility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return presentationName;
        }
    }

    public IIssueCategory getCategory();

    public Severity getSeverity();

    /**
     * @return the {@link IIssueProvider} - can be null.
     */
    public IIssueProvider getProvider();

    public String getDescription();
}