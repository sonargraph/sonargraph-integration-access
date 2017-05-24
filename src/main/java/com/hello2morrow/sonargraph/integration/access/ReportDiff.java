/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IReportDifferenceProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.IOMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IResolutionDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IWorkspaceDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.internal.ReportDeltaImpl;

public class ReportDiff
{
    private final String pathToReport1;
    private final String pathToReport2;

    public ReportDiff(final String pathToReport1, final String pathToReport2)
    {
        assert pathToReport1 != null : "Parameter 'pathToReport1' of method 'ReportDiff' must not be null";
        assert pathToReport2 != null : "Parameter 'pathToReport2' of method 'ReportDiff' must not be null";

        this.pathToReport1 = pathToReport1;
        this.pathToReport2 = pathToReport2;
    }

    public OperationResultWithOutcome<IReportDelta> calculateDelta()
    {
        final OperationResultWithOutcome<IReportDelta> result = new OperationResultWithOutcome<>("Calculating delta");
        result.addMessagesFrom(validatePaths());
        if (result.isFailure())
        {
            return result;
        }

        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final OperationResult load1 = controller.loadSystemReport(new File(pathToReport1));
        if (load1.isFailure())
        {
            result.addMessagesFrom(load1);
            return result;
        }
        final ISoftwareSystem system1 = controller.getSoftwareSystem();

        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();
        final OperationResult load2 = controller.loadSystemReport(new File(pathToReport2));
        if (load2.isFailure())
        {
            result.addMessagesFrom(load2);
            return result;
        }
        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();
        final ISoftwareSystem system2 = controller.getSoftwareSystem();

        final ICoreSystemDataDelta coreDelta = diffProcessor.getCoreSystemDataDelta(infoProcessor2);
        final IWorkspaceDelta workspaceDelta = diffProcessor.getWorkspaceDelta(infoProcessor2);
        final IIssueDelta issueDelta = diffProcessor.getIssueDelta(infoProcessor2, i -> !i.hasResolution());
        final IResolutionDelta resolutionDelta = diffProcessor.getResolutionDelta(infoProcessor2, null);
        final ReportDeltaImpl reportDelta = new ReportDeltaImpl(system1, system2, coreDelta, workspaceDelta, issueDelta, resolutionDelta);
        result.setOutcome(reportDelta);

        return result;
    }

    public OperationResult print(final IReportDelta delta, final String outputPath)
    {
        assert delta != null : "Parameter 'delta' of method 'print' must not be null";

        final OperationResult result = new OperationResult("Printing delta of reports");
        if (outputPath == null)
        {
            System.out.println(createPreamble(delta));
            System.out.println(delta.print(false));
            return result;
        }

        final File file = new File(outputPath);
        if (!file.getParentFile().exists())
        {
            if (!file.getParentFile().mkdirs())
            {
                result.addError(IOMessageCause.FAILED_TO_CREATE_DIRECTORY, "Failed to create parent directory of output file " + outputPath);
                return result;
            }
        }

        try (PrintWriter out = new PrintWriter(file))
        {
            out.println(createPreamble(delta));
            out.println(delta.print(false));
        }
        catch (final IOException ex)
        {
            result.addError(IOMessageCause.IO_EXCEPTION, ex);
        }
        return result;
    }

    private String createPreamble(final IReportDelta delta)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Delta of System Reports:");
        builder.append("\n").append(StringUtility.INDENTATION).append("Report1 (baseline): ").append(pathToReport1);
        builder.append("\n").append(StringUtility.INDENTATION).append("Report2           : ").append(pathToReport2);

        return builder.toString();
    }

    public OperationResult validatePaths()
    {
        final OperationResult result = new OperationResult("Validating XML report paths");
        validatePath(pathToReport1, result);
        validatePath(pathToReport2, result);
        return result;
    }

    private void validatePath(final String path, final OperationResult result)
    {
        final File file = new File(path);
        if (!file.exists())
        {
            result.addError(IOMessageCause.FILE_NOT_FOUND, path);
            return;
        }

        if (!file.isFile())
        {
            result.addError(IOMessageCause.NOT_A_FILE, path);
        }

        if (!file.canRead())
        {
            result.addError(IOMessageCause.NO_PERMISSION, path);
        }
    }

    public static void main(final String[] args)
    {
        if (!(args.length == 2 || args.length == 3))
        {
            printUsage();
            return;
        }

        final String pathToReport1 = args[0];
        final String pathToReport2 = args[1];
        final ReportDiff diff = new ReportDiff(pathToReport1, pathToReport2);

        final OperationResultWithOutcome<IReportDelta> result = diff.calculateDelta();
        if (result.isFailure())
        {
            System.err.println(result.toString());
            return;
        }

        final String outputPath = args.length == 3 ? args[2] : null;
        final OperationResult printResult = diff.print(result.getOutcome(), outputPath);
        if (printResult.isFailure())
        {
            System.err.println(result.toString());
            return;
        }
        if (outputPath != null)
        {
            System.out.println(ReportDiff.class.getCanonicalName() + ": Generated delta of systems to " + outputPath);
        }
    }

    private static void printUsage()
    {
        final StringBuilder builder = new StringBuilder("This program allows to calculate the difference of two Sonargrap XML reports,\n");
        builder.append("where report1 is taken as a reference or baseline and the delta is calculated of report2 with respect to this baseline.\n");
        builder.append("\nUsage: java ").append(ReportDiff.class.getCanonicalName())
                .append(" <path-to-baseline-report-xml> <path-to-report-xml> <optional: output-file-path>");
        System.out.println(builder.toString());
    }
}
