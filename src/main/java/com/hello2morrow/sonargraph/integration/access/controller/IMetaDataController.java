package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.OperationResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMergedExportMetaData;

public interface IMetaDataController
{
    public OperationResultWithOutcome<IExportMetaData> loadExportMetaData(File exportMetaDataFile);

    public OperationResultWithOutcome<IMergedExportMetaData> mergeExportMetaDataFiles(List<File> files);

    public OperationResultWithOutcome<IExportMetaData> loadExportMetaData(InputStream inputStream, String identifier);
}
