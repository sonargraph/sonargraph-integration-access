package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IMergedExportMetaData extends IExportMetaData
{
    public List<IBasicSoftwareSystemInfo> getSystems();
}