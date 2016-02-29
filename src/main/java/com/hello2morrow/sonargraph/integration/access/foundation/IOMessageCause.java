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
    NO_PERMISSON,
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