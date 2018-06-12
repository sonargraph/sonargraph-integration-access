/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.foundation.ResultCause;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.ISingleExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.internal.SingleExportMetaDataImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.XmlExportMetaDataReader;

final class MetaDataControllerImpl implements IMetaDataController
{
    @Override
    public ResultWithOutcome<IExportMetaData> loadExportMetaData(final File exportMetaDataFile)
    {
        assert exportMetaDataFile != null : "Parameter 'exportMetaDataFile' of method 'loadExportMetaData' must not be null";

        final ResultWithOutcome<IExportMetaData> result = new ResultWithOutcome<>("Load meta-data from '" + exportMetaDataFile + "'");
        try (final FileInputStream inputStream = new FileInputStream(exportMetaDataFile))
        {
            final ResultWithOutcome<IExportMetaData> loadExportMetaData = loadExportMetaData(inputStream, exportMetaDataFile.getCanonicalPath());
            result.addMessagesFrom(loadExportMetaData);
            result.setOutcome(loadExportMetaData.getOutcome());
        }
        catch (final FileNotFoundException ex)
        {
            result.addError(ResultCause.FILE_NOT_FOUND, ex);
        }
        catch (final IOException ex)
        {
            result.addError(ResultCause.IO_EXCEPTION, ex);
        }
        return result;
    }

    @Override
    public ResultWithOutcome<IExportMetaData> loadExportMetaData(final InputStream inputStream, final String identifier)
    {
        assert inputStream != null : "Parameter 'inputStream' of method 'loadExportMetaData' must not be null";
        assert identifier != null && identifier.length() > 0 : "Parameter 'identifier' of method 'loadExportMetaData' must not be empty";

        final ResultWithOutcome<IExportMetaData> result = new ResultWithOutcome<>("Load data from stream");
        final ResultWithOutcome<ISingleExportMetaData> readResult = internLoadExportMetaData(inputStream, identifier);
        result.addMessagesFrom(readResult);
        result.setOutcome(readResult.getOutcome());
        return result;
    }

    private static ResultWithOutcome<ISingleExportMetaData> internLoadExportMetaData(final InputStream inputStream, final String identifier)
    {
        assert inputStream != null : "Parameter 'inputStream' of method 'loadExportMetaData' must not be null";

        final ResultWithOutcome<ISingleExportMetaData> result = new ResultWithOutcome<>("Load data from stream");
        final XmlExportMetaDataReader persistence = new XmlExportMetaDataReader();
        final Optional<SingleExportMetaDataImpl> readResult = persistence.readMetaDataFromStream(inputStream, identifier, result);
        if (!readResult.isPresent() && result.isSuccess())
        {
            result.addError(ResultCause.READ_ERROR, "Failed to read meta-data from stream");
        }

        if (result.isFailure())
        {
            return result;
        }

        if (readResult.isPresent())
        {
            result.setOutcome(readResult.get());
        }
        return result;
    }
}