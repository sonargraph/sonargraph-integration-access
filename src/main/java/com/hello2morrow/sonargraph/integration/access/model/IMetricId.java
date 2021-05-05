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
package com.hello2morrow.sonargraph.integration.access.model;

import com.hello2morrow.sonargraph.integration.access.foundation.IEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;

import java.util.List;

public interface IMetricId extends IElementWithDescription
{
    public enum SortDirection implements IEnumeration
    {
        // DO NOT CHANGE NAMES BELOW
        //
        // They are stored in a database
        INDIFFERENT,
        HIGHER_WORSE,
        LOWER_WORSE,
        OPTIMUM_AT_ZERO,
        OPTIMUM_AT;

        @Override
        public String getStandardName()
        {
            return Utility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return Utility.convertConstantNameToPresentationName(name());
        }

        public static SortDirection fromStandardName(final String direction)
        {
            return SortDirection.valueOf(Utility.convertStandardNameToConstantName(direction));
        }
    }

    public enum StandardName implements IEnumeration
    {
        CORE_VIOLATIONS_PARSER_DEPENDENCIES("Number of Violations (Parser Dependencies)"),
        CORE_COMPONENTS("Number of Components"),
        CORE_UNASSIGNED_COMPONENTS("Number of Unassigned Components"),
        CORE_VIOLATING_COMPONENTS("Number of Components with Violations"),
        CORE_NCCD("NCCD");

        String presentationName;

        private StandardName(final String presentationName)
        {
            assert presentationName != null
                    && presentationName.length() > 0 : "Parameter 'presentationName' of method 'StandardMetricNames' must not be empty";
            this.presentationName = presentationName;
        }

        @Override
        public String getStandardName()
        {
            return Utility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return presentationName;
        }
    }

    public List<IMetricCategory> getCategories();

    public IMetricProvider getProvider();

    public boolean isFloat();

    public List<IMetricLevel> getLevels();

    @Deprecated
    public Double getBestValue();

    @Deprecated
    public Double getWorstValue();

    public double getBest();

    public double getMin();

    public double getMax();

    public SortDirection getSortDirection();

    double getWorst();
}