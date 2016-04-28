package com.hello2morrow.sonargraph.integration.access.xprojectmetric;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class AnalysisResult
{
    private final String m_projectName;
    private final Map<String, Number> m_metrics = new HashMap<>();
    private final Map<String, Map<String, Number>> m_moduleMetrics = new HashMap<>();

    public AnalysisResult(final String projectName, final List<String> moduleNames)
    {
        assert projectName != null && projectName.length() > 0 : "Parameter 'projectName' of method 'AnalysisResult' must not be empty";
        m_projectName = projectName;
        moduleNames.stream().forEach(s -> m_moduleMetrics.put(s, new HashMap<>()));
    }

    public String getProjectName()
    {
        return m_projectName;
    }

    public void setMetricValue(final String metricName, final Number value)
    {
        assert metricName != null && metricName.length() > 0 : "Parameter 'metricName' of method 'setMetricValue' must not be empty";
        m_metrics.put(metricName, value);
    }

    public Map<String, Number> getMetrics()
    {
        return Collections.unmodifiableMap(m_metrics);
    }

    public void setModuleMetricValue(final String moduleName, final String metricName, final Number value)
    {
        assert moduleName != null && moduleName.length() > 0 : "Parameter 'moduleName' of method 'setModuleMetricValue' must not be empty";
        assert m_moduleMetrics.containsKey(moduleName) : "Unknown module '" + moduleName + "'";
        assert metricName != null && metricName.length() > 0 : "Parameter 'metricName' of method 'setModuleMetricValue' must not be empty";

        m_moduleMetrics.get(moduleName).put(metricName, value);
    }

    public Map<String, Map<String, Number>> getModuleMetrics()
    {
        return Collections.unmodifiableMap(m_moduleMetrics);
    }

    public String getSummary()
    {
        final StringBuilder summary = new StringBuilder();
        summary.append("\n-------- Result for '").append(m_projectName).append("' -------------\n");
        summary.append("System metrics:\n");
        for (final Map.Entry<String, Number> next : m_metrics.entrySet())
        {
            summary.append("   ").append(next.getKey()).append(": ").append(next.getValue()).append("\n");
        }

        boolean moduleMetricsExist = false;
        final StringBuilder moduleMetrics = new StringBuilder();
        moduleMetrics.append("Module metrics:\n");
        for (final Entry<String, Map<String, Number>> next : m_moduleMetrics.entrySet())
        {
            final Map<String, Number> values = next.getValue();
            if (values.isEmpty())
            {
                continue;
            }

            moduleMetricsExist = true;
            moduleMetrics.append("    ").append(next.getKey()).append(":\n");
            for (final Entry<String, Number> nextValue : values.entrySet())
            {
                moduleMetrics.append("        ").append(nextValue.getKey()).append(": ").append(nextValue.getValue()).append("\n");
            }
        }

        if (moduleMetricsExist)
        {
            summary.append(moduleMetrics.toString());
        }

        summary.append("-----------------------------------------");
        return summary.toString();
    }
}
