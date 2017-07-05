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
package com.hello2morrow.sonargraph.integration.access.model;

public final class NamedElementUtil
{
    private static final char COLON = ':';

    private NamedElementUtil()
    {
        //hidden constructor
    }

    public static String getModuleName(final INamedElement element)
    {
        if (element instanceof ISoftwareSystem)
        {
            return null;
        }

        if (element instanceof IModule)
        {
            return element.getName();
        }
        final String fqName = element.getFqName();
        final int pos1 = fqName.indexOf(COLON);
        assert pos1 > 0 : "No '" + COLON + "' as separator in fqName found";
        final int pos2 = fqName.indexOf(COLON, pos1 + 1);
        assert pos2 > 0 : "No '" + COLON + "' as second separator in fqName found";

        return fqName.substring(pos1 + 1, pos2);
    }

}
