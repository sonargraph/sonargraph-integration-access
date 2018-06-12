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
