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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilityTest
{
    @Test
    public void testConvertConstantNameToMixedCaseString()
    {
        assertEquals("NumberOfViolations", Utility.convertConstantNameToMixedCaseString("NUMBER_OF_VIOLATIONS", true, false));
    }

    @Test
    public void testConvertMixedCaseStringToConstantName()
    {
        assertEquals("NUMBER_OF_VIOLATIONS", Utility.convertMixedCaseStringToConstantName("NumberOfViolations"));
    }

    @Test
    public void testTrimDescription()
    {
        assertEquals("", Utility.trimDescription(null, 4));
        assertEquals("", Utility.trimDescription("", 6));
        assertEquals("Test", Utility.trimDescription("Test", 4));
        assertEquals("T...", Utility.trimDescription("TestTest", 4));
        assertEquals("Te", Utility.trimDescription("Te", 2));
        assertEquals("T.", Utility.trimDescription("Tes", 2));
        assertEquals("T", Utility.trimDescription("Te", 1));
    }
}