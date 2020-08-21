/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.foundation;

public final class MigrationCheck
{
    public static final String PRE_UNIFICATION_OF_ISSUEIDS_VERSION = "10.3.1";

    public static boolean isPreUnificationOfIssueIds(final String version)
    {
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'isPreUnificationOfIssueIds' must not be empty";
        return Version.fromString(version).compareTo(Version.fromString(PRE_UNIFICATION_OF_ISSUEIDS_VERSION)) < 0;
    }
}