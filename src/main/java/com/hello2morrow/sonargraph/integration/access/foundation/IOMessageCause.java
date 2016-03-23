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
package com.hello2morrow.sonargraph.integration.access.foundation;

public enum IOMessageCause implements OperationResult.IMessageCause
{
    WRONG_FORMAT,
    READ_ERROR,
    WRITE_ERROR,
    UNEXPECTED_FILE_EXTENSION,
    FILE_NOT_FOUND,
    NOT_A_FILE,
    FAILED_TO_CREATE_FILE,
    NOT_A_DIRECTORY,
    DIRECTORY_NOT_FOUND,
    UNEXPECTED_DIRECTORY_NAME,
    FAILED_TO_CREATE_DIRECTORY,
    FAILED_TO_READ_DIRECTORY,
    FAILED_TO_DELETE_DIRECTORY,
    FAILED_TO_DELETE,
    FAILED_TO_COPY,
    FAILED_TO_MOVE,
    FAILED_TO_DETERMINE_CANONICAL_PATH,
    IO_EXCEPTION,
    NO_PERMISSION,
    FILE_SYSTEM_OUT_OF_SYNC;

    @Override
    public String getStandardName()
    {
        return StringUtility.convertConstantNameToStandardName(name());
    }

    @Override
    public String getPresentationName()
    {
        return StringUtility.convertConstantNameToPresentationName(name());
    }
}