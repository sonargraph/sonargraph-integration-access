package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.IMergedIssueCategory;

public class MergedIssueCategoryImpl extends IssueCategoryImpl implements IMergedIssueCategory
{
    private final List<IBasicSoftwareSystemInfo> m_projects = new ArrayList<>();

    public MergedIssueCategoryImpl(final String name, final String presentationName, final IBasicSoftwareSystemInfo project)
    {
        super(name, presentationName);
        assert project != null : "Parameter 'project' of method 'MergedIssueCategoryImpl' must not be null";
        m_projects.add(project);
    }

    @Override
    public List<IBasicSoftwareSystemInfo> getProjects()
    {
        return Collections.unmodifiableList(m_projects);
    }

    @Override
    public void addSystem(final IBasicSoftwareSystemInfo systemInfo)
    {
        assert systemInfo != null : "Parameter 'systemInfo' of method 'addSystem' must not be null";
        m_projects.add(systemInfo);
    }
}
