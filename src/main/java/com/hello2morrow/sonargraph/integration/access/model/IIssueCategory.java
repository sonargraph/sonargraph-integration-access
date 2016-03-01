package com.hello2morrow.sonargraph.integration.access.model;

import com.hello2morrow.sonargraph.integration.access.foundation.IStandardEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;

public interface IIssueCategory extends IElement
{
    public enum StandardName implements IStandardEnumeration
    {
        ARCHITECTURE_VIOLATION("Architecture Violation"),
        ARCHITECTURE_CONSISTENCY("Architecture Consistency"),
        THRESHOLD_VIOLATION("Threshold Violation"),
        WORKSPACE("Workspace"),
        TODO("Todo"),
        CYCLE_GROUP("Cycle Group"),
        REFACTORING("Refactoring");

        private final String m_presentationName;

        private StandardName(final String presentationName)
        {
            assert presentationName != null
                    && presentationName.length() > 0 : "Parameter 'presentationName' of method 'StandardMetricNames' must not be empty";
            m_presentationName = presentationName;
        }

        @Override
        public String getStandardName()
        {
            return StringUtility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return m_presentationName;
        }
    }
}