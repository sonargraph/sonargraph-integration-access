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

package com.hello2morrow.sonargraph.integration.architecture;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Optional;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.architecture.model.ArchitecturalModel;
import com.hello2morrow.sonargraph.integration.architecture.controller.XmlArchitectureAccess;

public class ArchitectureReaderTest
{
    @Test
    public void readArchitectureTest()
    {
        Result result = new Result("Reading Architecture");
        XmlArchitectureAccess reader = new XmlArchitectureAccess();
        Optional<ArchitecturalModel> modelOptional;

        modelOptional = reader.readArchitectureFile(new File("src/test/data/architecture/sonargraph.xml"), result);
        assertTrue(result.isSuccess());
        assertTrue(modelOptional.isPresent());
        assertEquals(11, modelOptional.get().getArtifacts().size());
        assertNotNull(modelOptional.get().findArtifact("LanguageProvider-Java.Command"));
        assertNotNull(modelOptional.get().findInterface("LanguageProvider-Java.Command.default"));
        assertNotNull(modelOptional.get().findConnector("LanguageProvider-Java.Command.default"));
    }
}
