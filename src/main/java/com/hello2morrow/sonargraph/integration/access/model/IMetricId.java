package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.IStandardEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;

public interface IMetricId extends IElementWithDescription
{
    public static enum StandardName implements IStandardEnumeration
    {
        CORE_VIOLATIONS_PARSER_DEPENDENCIES("Number of Violations (Parser Dependencies)"),
        CORE_COMPONENTS("Number of Components"),
        CORE_UNASSIGNED_COMPONENTS("Number of Unassigned Components"),
        CORE_VIOLATING_COMPONENTS("Number of Components with Violations"),
        CORE_NCCD("NCCD");

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

    public List<IMetricCategory> getCategories();

    public IMetricProvider getProvider();

    public boolean isFloat();

    public List<IMetricLevel> getLevels();
}