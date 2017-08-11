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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Comparator;

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

final class NamedElementComparator implements Comparator<INamedElement>
{
    NamedElementComparator()
    {
        super();
    }

    @Override
    public int compare(final INamedElement e1, final INamedElement e2)
    {
        assert e1 != null : "Parameter 'e1' of method 'compare' must not be null";
        assert e2 != null : "Parameter 'e2' of method 'compare' must not be null";

        if (e1 == e2)
        {
            return 0;
        }

        int compared = e1.getFqName().compareTo(e2.getFqName());
        if (compared == 0)
        {
            if (e1.isLocationOnly())
            {
                assert !e2.isLocationOnly() : "No 'isLocationOnly' expected for: " + e2.getFqName();
                compared = 1;
            }
            else
            {
                assert e2.isLocationOnly() : "'isLocationOnly' expected for: " + e2.getFqName();
                compared = -1;
            }
        }

        return compared;
    }
}