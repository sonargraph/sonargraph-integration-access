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
package com.hello2morrow.sonargraph.integration.access.foundation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MigrationCheckTest
{
    @Test
    public void testIsPreUnificationOfIssueIds()
    {
        assertTrue(MigrationCheck.isPreUnificationOfIssueIds("10.3.1.632"));
        assertTrue(MigrationCheck.isPreUnificationOfIssueIds("10.4.0.100"));
        assertFalse(MigrationCheck.isPreUnificationOfIssueIds("10.4.1.640"));
        assertFalse(MigrationCheck.isPreUnificationOfIssueIds("10.5.0.100"));
    }
}