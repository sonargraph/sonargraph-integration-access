package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;

import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;

public interface ISonargraphSystemController
{
    public OperationResult loadSystemReport(File systemReportFile);

    public ISoftwareSystem getSoftwareSystem();

    public boolean hasSoftwareSystem();

    public IModuleInfoProcessor createModuleInfoProcessor(IModule module);

    public ISystemInfoProcessor createSystemInfoProcessor();
}