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
package com.hello2morrow.sonargraph.integration.access;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultCause;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.diff.IReportDelta;

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

    public ResultWithOutcome<IReportDelta> calculateDelta()
    {
        final ResultWithOutcome<IReportDelta> result = new ResultWithOutcome<>("Calculating delta");
        result.addMessagesFrom(validatePaths());
        //TODO
        //        if (result.isFailure())
        //        {
        return result;
        //        }

        //        
        //        final ISonargraphSystemController controller = ControllerAccess.createController();
        //        final Result load1 = controller.loadSystemReport(new File(pathToReport1));
        //        if (load1.isFailure())
        //        {
        //            result.addMessagesFrom(load1);
        //            return result;
        //        }
        //        final ISoftwareSystem system1 = controller.getSoftwareSystem();
        //
        //        final IReportDifferenceProcessor diffProcessor = controller.createReportDifferenceProcessor();
        //        final Result load2 = controller.loadSystemReport(new File(pathToReport2));
        //        if (load2.isFailure())
        //        {
        //            result.addMessagesFrom(load2);
        //            return result;
        //        }
        //        final ISystemInfoProcessor infoProcessor2 = controller.createSystemInfoProcessor();
        //        final ISoftwareSystem system2 = controller.getSoftwareSystem();
        //
        //        final ICoreSystemDataDelta coreDelta = diffProcessor.getCoreSystemDataDelta(infoProcessor2);
        //        final IWorkspaceDelta workspaceDelta = diffProcessor.getWorkspaceDelta(infoProcessor2);
        //        final IIssueDelta issueDelta = diffProcessor.getIssueDelta(infoProcessor2, i -> !i.hasResolution());
        //        final ReportDeltaImpl reportDelta = new ReportDeltaImpl(system1, system2, coreDelta, workspaceDelta, issueDelta);
        //        result.setOutcome(reportDelta);
        //
        //        return result;
    }

    public Result print(final IReportDelta delta, final String outputPath)
    {
        assert delta != null : "Parameter 'delta' of method 'print' must not be null";

        final Result result = new Result("Printing delta of reports");
        if (outputPath == null)
        {
            System.out.println(createPreamble());
            System.out.println(delta);
            return result;
        }

        final File file = new File(outputPath);
        if (!file.getParentFile().exists())
        {
            if (!file.getParentFile().mkdirs())
            {
                result.addError(ResultCause.FAILED_TO_CREATE_DIRECTORY, "Failed to create parent directory of output file " + outputPath);
                return result;
            }
        }

        try (PrintWriter out = new PrintWriter(file))
        {
            out.println(createPreamble());
            out.println(delta);
        }
        catch (final IOException ex)
        {
            result.addError(ResultCause.IO_EXCEPTION, ex);
        }
        return result;
    }

    private String createPreamble()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Delta of System Reports:");
        builder.append("\n").append(Utility.INDENTATION).append("Report1 (baseline): ").append(pathToReport1);
        builder.append("\n").append(Utility.INDENTATION).append("Report2           : ").append(pathToReport2);

        return builder.toString();
    }

    public Result validatePaths()
    {
        final Result result = new Result("Validating XML report paths");
        validatePath(pathToReport1, result);
        validatePath(pathToReport2, result);
        return result;
    }

    private void validatePath(final String path, final Result result)
    {
        final File file = new File(path);
        if (!file.exists())
        {
            result.addError(ResultCause.FILE_NOT_FOUND, path);
            return;
        }

        if (!file.isFile())
        {
            result.addError(ResultCause.NOT_A_FILE, path);
        }

        if (!file.canRead())
        {
            result.addError(ResultCause.NO_PERMISSION, path);
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

        final ResultWithOutcome<IReportDelta> result = diff.calculateDelta();
        if (result.isFailure())
        {
            System.err.println(result.toString());
            return;
        }

        final String outputPath = args.length == 3 ? args[2] : null;
        final Result printResult = diff.print(result.getOutcome(), outputPath);
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
