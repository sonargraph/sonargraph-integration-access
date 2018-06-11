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
package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.IMetaDataController;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;

public final class ExportMetaDataControllerTest
{
    private static final String META_DATA_PATH = TestFixture.META_DATA_PATH;
    private static final String WRONG_ENCODING = TestFixture.META_DATA_PATH_WRONG_ENCODING;

    private IMetaDataController m_controller;

    @Before
    public void before()
    {
        m_controller = ControllerAccess.createMetaDataController();
    }

    @Test
    public void testReadExportMetaData() throws IOException
    {
        final File exportMetaDataFile = new File(META_DATA_PATH);
        final ResultWithOutcome<IExportMetaData> result = m_controller.loadExportMetaData(exportMetaDataFile);
        assertTrue("Failed to load metric meta-data file: " + result.toString(), result.isSuccess());

        final IExportMetaData metaData = result.getOutcome();

        assertEquals("Identifier for metadata does not match", new File(META_DATA_PATH).getCanonicalPath(), metaData.getResourceIdentifier());
        assertEquals("Wrong issue categories size", 13, metaData.getIssueCategories().size());
        final String issueCategory = "ArchitectureViolation";
        assertNotNull("Issue Category '" + issueCategory + "' not found", metaData.getIssueCategories().get(issueCategory));

        assertEquals("Wrong issue providers size", 8, metaData.getIssueProviders().size());
        final String issueProvider = "./Core/BadSmells/UnusedTypes.scr";
        assertNotNull("Issue Provier '" + issueProvider + "' not found", metaData.getIssueProviders().get(issueProvider));

        assertEquals("Wrong issue types size", 109, metaData.getIssueTypes().size());
        final String issueType = "WorkspaceDependencyProblematic";
        assertNotNull("Issue Type '" + issueType + "' not found", metaData.getIssueTypes().get(issueType));

        assertEquals("Wrong metric categories size", 10, metaData.getMetricCategories().size());
        final String architectureCategory = "Architecture";
        assertNotNull("Metric category '" + architectureCategory + "' not found", metaData.getMetricCategories().get(architectureCategory));

        final String johnLakosCategoryName = "JohnLakos";
        assertEquals("Metric category '" + johnLakosCategoryName + "' has wrong presentation name", "John Lakos",
                metaData.getMetricCategories().get(johnLakosCategoryName).getPresentationName());

        final Map<String, IMetricLevel> metricLevelMap = metaData.getMetricLevels();
        assertEquals("Wrong number of levels", 8, metricLevelMap.size());

        final List<IMetricId> systemLevelMetrics = metaData.getMetricIdsForLevel(metricLevelMap.get("System"));
        assertEquals("Wrong number of metrics for System level", 45, systemLevelMetrics.size());

        final IMetricId coreLinesOfCode = metaData.getMetricIds().get("CoreLinesOfCode");
        assertThat("Wrong number of levels for metric", coreLinesOfCode.getLevels(),
                hasItems(metricLevelMap.get("System"), metricLevelMap.get("Module"), metricLevelMap.get("SourceFile")));

        assertEquals("Wrong number of metrics", 76, metaData.getMetricIds().size());
        final String scriptMetricId = "Unused Types";
        assertEquals("Metric id '" + scriptMetricId + "' has wrong presentation name", "Number of Unused Types",
                metaData.getMetricIds().get(scriptMetricId).getPresentationName());
    }

    @Test
    public void testWrongEncoding()
    {
        final File exportMetaDataFile = new File(WRONG_ENCODING);
        final ResultWithOutcome<IExportMetaData> result = m_controller.loadExportMetaData(exportMetaDataFile);
        assertFalse("Failure expected: " + result.toString(), result.isSuccess());
    }
}