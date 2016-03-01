package com.hello2morrow.sonargraph.integration.access.model.java;

import com.hello2morrow.sonargraph.integration.access.foundation.IStandardEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;

public interface IJavaMetricId extends IMetricId
{
    public enum StandardName implements IStandardEnumeration
    {
        JAVA_PACKAGES("Number of Packages"),
        JAVA_CYCLIC_PACKAGES("Number of Cyclic Packages"),
        JAVA_STRUCTURAL_DEBT_INDEX_PACKAGES("Structural Debt Index (Packages)");

        String m_presentationName;

        private StandardName(final String presentationName)
        {
            assert presentationName != null && presentationName.length() > 0 : "Parameter 'presentationName' of method 'StandardMetricNames' must not be empty";
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