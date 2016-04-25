package com.hello2morrow.sonargraph.integration.access.xprojectmetric;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IModuleInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.controller.ISonargraphSystemController;
import com.hello2morrow.sonargraph.integration.access.controller.ISystemInfoProcessor;
import com.hello2morrow.sonargraph.integration.access.foundation.Platform;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;

/**
 * Example code for the blog post about "Cross Project Analysis", published on <a href="http://blog.hello2morrow.com/">http://blog.hello2morrow.com/</a>.
 * It runs the Maven goal sonargraph:dynamic-report in version 8.9.0 for the specified projects and extracts only certain metrics from the generated XML reports.<br/>
 * See the blog post for more details.<br/>
 * <br/>
 * Precondition: All projects have been built.<br/>
 * Usage: Executor &lt;activationCode&gt; &lt;qualityModelFilePath&gt; &lt;projectDir1&gt; &lt;projectDir2&gt; &lt;projectDirN&gt;
 */
public class Executor
{
    private static final String PLUGIN_VERSION = "8.9.0";
    private static final String INSTALLATION_DIRECTORY = "D:/00_repos/00_e4-sgng/com.hello2morrow.sonargraph.build/dist/release/SonargraphBuild";

    private static final String CORE_NCCD = "CoreNccd";
    private static final String SUPERTYPE_USES_SUBTYPE = "Supertype uses subtype";
    private static final String ARCHITECTURE_VIOLATION = "ArchitectureViolation";
    private static final String THRESHOLD_VIOLATION = "ThresholdViolation";

    private static final String OCCURRENCES_OF_SUPER_TYPE_USES_SUB_TYPE = "Occurrences of " + SUPERTYPE_USES_SUBTYPE;
    private static final String REFERENCES_TO_UNSAFE = "References To Unsafe";
    private static final String FILES_WITH_REFERENCES_TO_UNSAFE = "Files With References To Unsafe";
    private static final String MODULES_VIOLATING_NCCD_THRESHOLD = "Modules violating NCCD threshold";

    public static void main(final String[] args)
    {
        if (args.length < 3)
        {
            System.err.println("You need to specify at least the activation code, qualityModel path and one path to maven root directory");
            return;
        }

        final long overallStart = System.currentTimeMillis();
        final Executor executor = new Executor();
        final File reportDirectory = new File("./reports");
        reportDirectory.mkdirs();

        final List<String> projectPaths = new ArrayList<>(Arrays.asList(args));
        final String activationCode = projectPaths.remove(0);
        final String qualityModelPath = projectPaths.remove(0);
        final File qualityModelFile = new File(qualityModelPath);
        if (!qualityModelFile.exists())
        {
            System.err.println(String.format("Specified qualityModelFile '%s' does not exist.", qualityModelPath));
            return;
        }

        final Map<String, AnalysisResult> results = new HashMap<>();
        for (final String projectPath : projectPaths)
        {
            final File projectDir = new File(projectPath);
            if (!projectDir.exists())
            {
                System.err.println(String.format("File '%s' does not exist", projectPath));
                continue;
            }
            try
            {
                final long start = System.currentTimeMillis();
                final AnalysisResult result;
                if (executor.runMaven(activationCode, reportDirectory, projectDir, qualityModelFile) == 0)
                {
                    System.out.println(String.format("Executed Maven for '%s' in %d ms.", projectPath, (System.currentTimeMillis() - start)));
                    result = executor.processReport(reportDirectory, projectDir);
                }
                else
                {
                    System.err.println(String.format("Failed to execute Maven for '%s'.", projectPath));
                    result = null;
                }
                results.put(projectPath, result);
            }
            catch (final Exception ex)
            {
                System.err.println(String.format("Failed to execute Maven for '%s': ", projectPath));
                ex.printStackTrace();
            }
        }

        printSummary(results);

        System.out.println(String.format("Finished processing in %d ms.", System.currentTimeMillis() - overallStart));
        System.exit(0);
    }

    private int runMaven(final String activationCode, final File reportDirectory, final File projectDir, final File qualityModelFile)
            throws IOException, InterruptedException
    {
        assert activationCode != null && activationCode.length() > 0 : "Parameter 'activationCode' of method 'runMaven' must not be empty";
        assert reportDirectory != null : "Parameter 'reportDirectory' of method 'runMaven' must not be null";
        assert projectDir != null : "Parameter 'projectDir' of method 'runMaven' must not be null";

        final String mavenHome = System.getenv("M2_HOME");
        final String mavenExecutable;
        if (Platform.isWindows())
        {
            mavenExecutable = mavenHome + "/bin/mvn.bat";
        }
        else
        {
            mavenExecutable = mavenHome + "/bin/mvn";
        }

        final String commandLine = new StringBuilder(mavenExecutable).append(" com.hello2morrow:sonargraph-maven-plugin:").append(PLUGIN_VERSION)
                .append(":dynamic-report -Dsonargraph.activationCode=").append(activationCode).append(" -Dsonargraph.qualityModelFile=")
                .append(qualityModelFile.getAbsolutePath()).append(" -Dsonargraph.reportDirectory=").append(reportDirectory.getAbsolutePath())
                .append(" -Dsonargraph.reportFileName=").append(projectDir.getName()).append(" -Dsonargraph.reportFormat=xml")
                .append(" -Dsonargraph.installationDirectory=").append(INSTALLATION_DIRECTORY).toString();
        System.out.println("Executing: " + commandLine);
        final List<String> command = Arrays.asList(commandLine.split(" "));

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(projectDir);
        builder.inheritIO();

        final Process process = builder.start();
        return process.waitFor();
    }

    private AnalysisResult processReport(final File reportDirectory, final File projectDirectory)
    {
        final ISonargraphSystemController controller = new ControllerFactory().createController();
        final File reportFile = new File(reportDirectory, projectDirectory.getName() + ".xml");
        if (controller.loadSystemReport(reportFile).isFailure())
        {
            return null;
        }

        final ISoftwareSystem softwareSystem = controller.getSoftwareSystem();
        final Map<String, IModule> modules = softwareSystem.getModules();
        final AnalysisResult result = new AnalysisResult(projectDirectory.getName(), new ArrayList<>(modules.keySet()));
        final ISystemInfoProcessor infoProcessor = controller.createSystemInfoProcessor();

        processSuperTypeUsesSubType(result, infoProcessor);
        processArchitectureViolations(result, infoProcessor);
        processNccdThresholdViolations(result, infoProcessor, controller);

        return result;
    }

    private void processSuperTypeUsesSubType(final AnalysisResult result, final ISystemInfoProcessor infoProcessor)
    {
        final List<IIssue> superTypeUsesSubTypeIssues = infoProcessor.getIssues(i -> i.getIssueType().getName().equals(SUPERTYPE_USES_SUBTYPE));
        result.setMetricValue(OCCURRENCES_OF_SUPER_TYPE_USES_SUB_TYPE, superTypeUsesSubTypeIssues.size());
    }

    private void processArchitectureViolations(final AnalysisResult result, final ISystemInfoProcessor infoProcessor)
    {
        final List<IIssue> architectureViolations = infoProcessor.getIssues(i -> i.getIssueType().getName().equals(ARCHITECTURE_VIOLATION)
                && i.getDescription().endsWith("'Internal' cannot access 'Unsafe' from 'External.Unsafe'"));
        final int referencesToUnsafe = architectureViolations.size();

        final Map<INamedElement, List<IIssue>> groupedBySource = architectureViolations.stream().collect(Collectors.groupingBy(i ->
        {
            final INamedElement origin = i.getOrigins().get(0);
            if (origin.getSourceFile().isPresent())
            {
                return origin.getSourceFile().get();
            }
            System.err.println("No source found for architecture issue!");
            return null;
        }));

        result.setMetricValue(REFERENCES_TO_UNSAFE, referencesToUnsafe);
        result.setMetricValue(FILES_WITH_REFERENCES_TO_UNSAFE, groupedBySource.size());
    }

    private void processNccdThresholdViolations(final AnalysisResult result, final ISystemInfoProcessor infoProcessor,
            final ISonargraphSystemController controller)
    {
        final List<IIssue> nccdThresholdViolations = infoProcessor.getIssues(i -> i.getIssueType().getName().equals(THRESHOLD_VIOLATION)
                && i.getDescription().startsWith("NCCD =") && i.getOrigins().get(0) instanceof IModule);
        result.setMetricValue(MODULES_VIOLATING_NCCD_THRESHOLD, nccdThresholdViolations.size());
        if (nccdThresholdViolations.isEmpty())
        {
            return;
        }

        for (final IIssue next : nccdThresholdViolations)
        {
            final IModule module = (IModule) next.getOrigins().get(0);
            final IModuleInfoProcessor moduleProcessor = controller.createModuleInfoProcessor(module);
            final Optional<IMetricValue> value = moduleProcessor.getMetricValue(CORE_NCCD);
            if (value.isPresent())
            {
                result.setModuleMetricValue(module.getName(), "NCCD", value.get().getValue());
            }
        }
    }

    private static void printSummary(final Map<String, AnalysisResult> resultMap)
    {
        final List<AnalysisResult> results = resultMap.values().stream().filter(entry -> entry != null).collect(Collectors.toList());

        results.stream().forEach(r -> System.out.println(r.getSummary()));

        final StringBuilder summary = new StringBuilder();
        summary.append("\n-------- Cross-Project Result -----------\n");
        addMetricValue(OCCURRENCES_OF_SUPER_TYPE_USES_SUB_TYPE, summary, results);
        addMetricValue(FILES_WITH_REFERENCES_TO_UNSAFE, summary, results);
        addMetricValue(REFERENCES_TO_UNSAFE, summary, results);
        addMetricValue(MODULES_VIOLATING_NCCD_THRESHOLD, summary, results);
        summary.append("-----------------------------------------\n");
        System.out.println(summary.toString());
    }

    private static void addMetricValue(final String metric, final StringBuilder result, final List<AnalysisResult> results)
    {
        final int value = results.stream().mapToInt(r -> r.getMetrics().get(metric).intValue()).sum();
        result.append(metric).append(": ").append(value).append("\n");
    }
}
