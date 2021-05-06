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
package com.hello2morrow.sonargraph.integration.architecture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Optional;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.architecture.controller.ArchitectureReader;
import com.hello2morrow.sonargraph.integration.architecture.model.ArchitecturalModel;

public class ArchitectureReaderTest
{
    @Test
    public void readArchitectureTest()
    {
        final Result result = new Result("Reading Architecture");
        final ArchitectureReader reader = new ArchitectureReader();
        final Optional<ArchitecturalModel> modelOptional = reader.readArchitectureFile(new File("src/test/data/architecture/anytest.xml"), result);
        assertTrue("Failed to read architecture", result.isSuccess());
        assertTrue(modelOptional.isPresent());
        final ArchitecturalModel architecturalModel = modelOptional.get();
        assertEquals("Wrong number of artifacts", 2, architecturalModel.getArtifacts().size());
        assertNotNull("Missing artifact", architecturalModel.findArtifact("Alarm.App"));
        assertNotNull("Missing interface", architecturalModel.findInterface("Alarm.App.default"));
        assertNotNull("Missing connector", architecturalModel.findConnector("Alarm.App.default"));

        assertEquals("Wrong version", "9.11.2.100", architecturalModel.getVersion());
        assertEquals("Wrong system path",
                "D:\\00_repo\\sgng\\com.hello2morrow.sonargraph.language.provider.java\\src\\test\\architecture\\AlarmClockWithArchitecture\\AlarmClock.sonargraph",
                architecturalModel.getSystemPath());
        assertEquals("Wrong system id", "4df288656010188b4d84a2a03bb0ecb9", architecturalModel.getSystemId());
        assertEquals("Wrong timestamp", 1562668320367L, architecturalModel.getTimestamp());
    }
}
