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
package com.hello2morrow.sonargraph.integration.access.apitest.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerAccess;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.Couple;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IElementKindDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricThresholdDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IStandardDelta;

public class ReportDifferenceProcessorCoreInfoTest
{
    @Test
    public void compareCoreSystemInfo()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result load1 = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_META_DATA_1));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final Result load2 = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_META_DATA_2));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        final ICoreSystemDataDelta metaDataDelta = diffProcessor.getCoreSystemDataDelta(infoProcessor2);

        final IStandardDelta<IIssueProvider> issueProviderDelta = metaDataDelta.getIssueProviderDelta();
        assertEquals("Wrong number of added providers", 1, issueProviderDelta.getAdded().size());
        assertEquals("Wrong added provider", "./Core/SuperTypeUsesSubType.scr", issueProviderDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed providers", 1, issueProviderDelta.getRemoved().size());
        assertEquals("Wrong removed provider", "JavaLanguageProvider", issueProviderDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged providers", 3, issueProviderDelta.getUnchanged().size());

        final IStandardDelta<IIssueCategory> issueCategoryDelta = metaDataDelta.getIssueCategoryDelta();
        assertEquals("Wrong number of added cateogries", 1, issueCategoryDelta.getAdded().size());
        assertEquals("Wrong added category", "ArchitectureConsistency", issueCategoryDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed categories", 1, issueCategoryDelta.getRemoved().size());
        assertEquals("Wrong removed category", "Workspace", issueCategoryDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged categories", 11, issueCategoryDelta.getUnchanged().size());

        final IStandardDelta<IIssueType> issueTypeDelta = metaDataDelta.getIssueTypeDelta();
        assertEquals("Wrong number of added types", 1, issueTypeDelta.getAdded().size());
        assertEquals("Wrong added type", "EmptyArchitectureElement", issueTypeDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed types", 1, issueTypeDelta.getRemoved().size());
        assertEquals("Wrong removed type", "JavaFileClassFileMissing", issueTypeDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged types", 9, issueTypeDelta.getUnchanged().size());

        final IStandardDelta<IMetricProvider> metricProviderDelta = metaDataDelta.getMetricProviderDelta();
        assertEquals("Wrong number of added metric providers", 1, metricProviderDelta.getAdded().size());
        assertEquals("Wrong added metric provider", "./Java/BadSmells/PhantasyY.scr", metricProviderDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed metric providers", 1, metricProviderDelta.getRemoved().size());
        assertEquals("Wrong removed metric provider", "./Java/BadSmells/PhantasyX.scr", metricProviderDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged metric providers", 3, metricProviderDelta.getUnchanged().size());

        final IStandardDelta<IMetricCategory> metricCategoryDelta = metaDataDelta.getMetricCategoryDelta();
        assertEquals("Wrong number of added metric categories", 1, metricCategoryDelta.getAdded().size());
        assertEquals("Wrong added metric category", "ArchitectureY", metricCategoryDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed metric categories", 1, metricCategoryDelta.getRemoved().size());
        assertEquals("Wrong removed metric category", "ArchitectureX", metricCategoryDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged metric categories", 10, metricCategoryDelta.getUnchanged().size());

        final IStandardDelta<IMetricLevel> metricLevelDelta = metaDataDelta.getMetricLevelDelta();
        assertEquals("Wrong number of added metric levels", 1, metricLevelDelta.getAdded().size());
        assertEquals("Wrong added metric level", "SystemY", metricLevelDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed metric levels", 1, metricLevelDelta.getRemoved().size());
        assertEquals("Wrong removed metric level", "SystemX", metricLevelDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged metric levels", 8, metricLevelDelta.getUnchanged().size());

        final IStandardDelta<IMetricId> metricIdDelta = metaDataDelta.getMetricIdDelta();
        assertEquals("Wrong number of added metric ids", 1, metricIdDelta.getAdded().size());
        assertEquals("Wrong added metric id", "CoreAcdY", metricIdDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed metric ids", 1, metricIdDelta.getRemoved().size());
        assertEquals("Wrong removed metric id", "CoreAcdX", metricIdDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged metric ids", 76, metricIdDelta.getUnchanged().size());

        final IStandardDelta<IFeature> featureDelta = metaDataDelta.getFeatureDelta();
        assertEquals("Wrong number of added features", 1, featureDelta.getAdded().size());
        assertEquals("Wrong added feature", "ArchitectureY", featureDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed features", 1, featureDelta.getRemoved().size());
        assertEquals("Wrong removed feature", "ArchitectureX", featureDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged features", 11, featureDelta.getUnchanged().size());

        final IStandardDelta<IAnalyzer> analyzerDelta = metaDataDelta.getAnalyzerDelta();
        assertEquals("Wrong number of added analyzers", 1, analyzerDelta.getAdded().size());
        assertEquals("Wrong added analyzer", "ArchitectureCheckY", analyzerDelta.getAdded().get(0).getName());
        assertEquals("Wrong number of removed analyzers", 1, analyzerDelta.getRemoved().size());
        assertEquals("Wrong removed analyzer", "ArchitectureCheckX", analyzerDelta.getRemoved().get(0).getName());
        assertEquals("Wrong number of unchanged analyzers", 19, analyzerDelta.getUnchanged().size());

        final IMetricThresholdDelta thresholdDelta = metaDataDelta.getMetricThresholdDelta();
        assertEquals("Wrong number of added metric thresholds", 1, thresholdDelta.getAdded().size());
        assertEquals("Wrong added metric threshold", "JavaByteCodeInstructions", thresholdDelta.getAdded().get(0).getMetricId().getName());
        assertEquals("Wrong number of removed metric thresholds", 1, thresholdDelta.getRemoved().size());
        assertEquals("Wrong removed metric threshold", "CoreCcd", thresholdDelta.getRemoved().get(0).getMetricId().getName());
        assertEquals("Wrong number of unchanged metric threshold", 4, thresholdDelta.getUnchanged().size());
        assertEquals("Wrong number of changed thresholds", 1, thresholdDelta.getChanged().size());
        final Couple<IMetricThreshold, IMetricThreshold> changed = thresholdDelta.getChanged().get(0);
        assertEquals("Wrong changed metric threshold", "CoreAcd", changed.getFirst().getMetricId().getName());
        final IMetricThreshold previous = changed.getFirst();
        assertEquals("Wrong old lower threshold", 0, previous.getLowerThreshold().intValue());
        assertEquals("Wrong old upper threshold", 45, previous.getUpperThreshold().intValue());
        final IMetricThreshold updated = changed.getSecond();
        assertEquals("Wrong new lower threshold", 1, updated.getLowerThreshold().intValue());
        assertEquals("Wrong new upper threshold", 46, updated.getUpperThreshold().intValue());

        final IElementKindDelta elementKindDelta = metaDataDelta.getElementKindDelta();
        assertEquals("Wrong number of added element kinds", 1, elementKindDelta.getAdded().size());
        assertEquals("Wrong added element kind", "JavaLogicalModuleNamespaceY", elementKindDelta.getAdded().get(0));
        assertEquals("Wrong number of removed element kinds", 1, elementKindDelta.getRemoved().size());
        assertEquals("Wrong removed element kind", "JavaLogicalModuleNamespaceX", elementKindDelta.getRemoved().get(0));
        assertEquals("Wrong number of unchanged element kinds", 14, elementKindDelta.getUnchanged().size());
    }

    @Test
    public void checkEqualSystems()
    {
        final ISonargraphSystemController controller = ControllerAccess.createController();
        final Result load1 = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_META_DATA_1));
        assertTrue(load1.toString(), load1.isSuccess());
        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();

        final Result load2 = controller.loadSystemReport(new File(TestFixture.TEST_REPORT_META_DATA_1));
        assertTrue(load2.toString(), load2.isSuccess());
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();

        final ICoreSystemDataDelta metaDataDelta = diffProcessor.getCoreSystemDataDelta(infoProcessor2);

        final IStandardDelta<IIssueProvider> issueProviderDelta = metaDataDelta.getIssueProviderDelta();
        assertEquals("Wrong number of added providers", 0, issueProviderDelta.getAdded().size());
        assertEquals("Wrong number of removed providers", 0, issueProviderDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged providers", 4, issueProviderDelta.getUnchanged().size());

        final IStandardDelta<IIssueCategory> issueCategoryDelta = metaDataDelta.getIssueCategoryDelta();
        assertEquals("Wrong number of added cateogries", 0, issueCategoryDelta.getAdded().size());
        assertEquals("Wrong number of removed categories", 0, issueCategoryDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged categories", 12, issueCategoryDelta.getUnchanged().size());

        final IStandardDelta<IIssueType> issueTypeDelta = metaDataDelta.getIssueTypeDelta();
        assertEquals("Wrong number of added types", 0, issueTypeDelta.getAdded().size());
        assertEquals("Wrong number of removed types", 0, issueTypeDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged types", 10, issueTypeDelta.getUnchanged().size());

        final IStandardDelta<IMetricProvider> metricProviderDelta = metaDataDelta.getMetricProviderDelta();
        assertEquals("Wrong number of added metric providers", 0, metricProviderDelta.getAdded().size());
        assertEquals("Wrong number of removed metric providers", 0, metricProviderDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged metric providers", 4, metricProviderDelta.getUnchanged().size());

        final IStandardDelta<IMetricCategory> metricCategoryDelta = metaDataDelta.getMetricCategoryDelta();
        assertEquals("Wrong number of added metric categories", 0, metricCategoryDelta.getAdded().size());
        assertEquals("Wrong number of removed metric categories", 0, metricCategoryDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged metric categories", 11, metricCategoryDelta.getUnchanged().size());

        final IStandardDelta<IMetricLevel> metricLevelDelta = metaDataDelta.getMetricLevelDelta();
        assertEquals("Wrong number of added metric levels", 0, metricLevelDelta.getAdded().size());
        assertEquals("Wrong number of removed metric levels", 0, metricLevelDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged metric levels", 9, metricLevelDelta.getUnchanged().size());

        final IStandardDelta<IMetricId> metricIdDelta = metaDataDelta.getMetricIdDelta();
        assertEquals("Wrong number of added metric ids", 0, metricIdDelta.getAdded().size());
        assertEquals("Wrong number of removed metric ids", 0, metricIdDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged metric ids", 77, metricIdDelta.getUnchanged().size());

        final IStandardDelta<IFeature> featureDelta = metaDataDelta.getFeatureDelta();
        assertEquals("Wrong number of added features", 0, featureDelta.getAdded().size());
        assertEquals("Wrong number of removed features", 0, featureDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged features", 12, featureDelta.getUnchanged().size());

        final IStandardDelta<IAnalyzer> analyzerDelta = metaDataDelta.getAnalyzerDelta();
        assertEquals("Wrong number of added analyzers", 0, analyzerDelta.getAdded().size());
        assertEquals("Wrong number of removed analyzers", 0, analyzerDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged analyzers", 20, analyzerDelta.getUnchanged().size());

        final IMetricThresholdDelta thresholdDelta = metaDataDelta.getMetricThresholdDelta();
        assertEquals("Wrong number of added metric thresholds", 0, thresholdDelta.getAdded().size());
        assertEquals("Wrong number of removed metric thresholds", 0, thresholdDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged metric threshold", 6, thresholdDelta.getUnchanged().size());

        final IElementKindDelta elementKindDelta = metaDataDelta.getElementKindDelta();
        assertEquals("Wrong number of added element kinds", 0, elementKindDelta.getAdded().size());
        assertEquals("Wrong number of removed element kinds", 0, elementKindDelta.getRemoved().size());
        assertEquals("Wrong number of unchanged element kinds", 15, elementKindDelta.getUnchanged().size());
    }
}
