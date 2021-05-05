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
package com.hello2morrow.sonargraph.integration.access.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;

public class DependencyPatternMigrationTest
{
    @Test
    public void migrate()
    {
        final ISonargraphSystemController controller = ControllerFactory.createController();
        final Result result = controller.loadSystemReport(new File("./src/test/migration/11.1.0_report.xml"));
        assertTrue(result.toString(), result.isSuccess());
        final ISystemInfoProcessor info = controller.createSystemInfoProcessor();
        final List<IResolution> resolutions = info.getResolutions(null);
        assertEquals("Wrong number of issues", 9, resolutions.size());

        final IResolution fixmeArchViolation = resolutions.get(0);
        validateDependencyPatterns(fixmeArchViolation);

    }

    private void validateDependencyPatterns(final IResolution fixmeArchViolation)
    {
        assertEquals("C11 -> C22", fixmeArchViolation.getDescription());
        final List<IDependencyPattern> patterns = fixmeArchViolation.getDependencyPatterns();
        final IDependencyPattern c11ToC22Unmodified = patterns.get(0);
        assertEquals("Wrong from pattern", "Workspace:m1:./src:com:h2m:test:ignore:modified:changedMatches:C11.java:C11",
                c11ToC22Unmodified.getFromPattern());
        assertEquals("Wrong to pattern", "Workspace:m1:./src:com:h2m:test:ignore:modified:changedMatches:C22.java:C22",
                c11ToC22Unmodified.getToPattern());

        final IDependencyPattern c22ToC22Modified = patterns.get(1);
        assertEquals("Wrong from pattern", "Workspace:m1:./src:com:h2m:test:ignore:modified:changedMatches:C11.java:C11:c22|f",
                c22ToC22Modified.getFromPattern());
        assertEquals("Wrong to pattern", "Workspace:m1:./src:com:h2m:test:ignore:modified:changedMatches:C22.java:C22",
                c22ToC22Modified.getToPattern());
    }
}