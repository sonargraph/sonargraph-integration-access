package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IMergedIssueCategory extends IIssueCategory
{
    public List<IBasicSoftwareSystemInfo> getProjects();

    public void addSystem(IBasicSoftwareSystemInfo systemInfo);
}