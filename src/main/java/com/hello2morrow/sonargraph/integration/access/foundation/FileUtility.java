package com.hello2morrow.sonargraph.integration.access.foundation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public final class FileUtility
{
    public static final char PATH_SEPARATOR_CHAR = '/';
    public static final char EXTENSION_SEPARATOR_CHAR = '.';
    public static final String REL_PATH_START = "./";
    public static final String PATH_SEPARATOR = "/";
    public static final String RELATIVE_PATH_CURRENT = ".";
    public static final String RELATIVE_PATH_UP = "..";
    public static final char PATH_SEPARATOR_CHAR_TO_BE_CONVERTED = '\\';
    public static final String UTF8_ENCODING = "UTF-8";
    public static final String SYSTEM_PATH_SEPARATOR = System.getProperty("file.separator");

    public static final String STANDARD_DIRECTORY_NAME_PATTERN = "[\\w\\-]+[ \\w\\-]*";

    private FileUtility()
    {
        super();
    }

    public static String removeExtension(final String fileName)
    {
        assert fileName != null : "Parameter 'fileName' of method 'removeExtension' must not be null";
        final int index = fileName.lastIndexOf('.');

        if (index > 0)
        {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     * Returns the file name without the ending. E.g. for c:\temp\fileName.txt the return value is "fileName"
     * @return the plain name of the specified file
     */
    public static String getFileNameWithoutExtension(final File file)
    {
        assert file != null : "Parameter 'file' of method 'getFileNameWithoutType' must not be null";
        final String fileName = file.getName();
        return FileUtility.removeExtension(fileName);
    }

    private static boolean onDifferentRootNodes(final String pathOne, final String pathTwo, final boolean ignoreCase)
    {
        assert pathOne != null : "'pathOne' must not be null";
        assert pathOne.length() > 0 : "'pathOne' must not be empty";
        assert pathTwo != null : "'pathTwo' must not be null";
        assert pathTwo.length() > 0 : "'pathTwo' must not be empty";

        if (!Platform.isOperatingSystemUnixBased())
        {
            if (ignoreCase)
            {
                return pathOne.toLowerCase().charAt(0) != pathTwo.toLowerCase().charAt(0);
            }
            return pathOne.charAt(0) != pathTwo.charAt(0);
        }

        return false;
    }

    private static List<String> getPathParts(final String path)
    {
        assert path != null : "'path' must not be null";
        final StringTokenizer tokenizer = new StringTokenizer(path, PATH_SEPARATOR);
        final List<String> relativePathParts = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
        {
            relativePathParts.add(tokenizer.nextToken());
        }
        return relativePathParts;
    }

    /**
     * @param path file or directory for that the path is calculated
     * @param basePath directory that is taken as a reference to calculate the relative path if possible. If <code>null</code> the absolute path of the file is returned.
     * @return the path relative to the given base directory.
     *
     * NOTE: If the basePath is a 'real' directory is not checked by this implementation
     * @throws IOException
     */
    public static String calculateRelativePath(final File path, final File basePath) throws IOException
    {
        assert path != null : "Parameter 'fileOrDirectory' of method 'calculatePath' must not be null";

        if (basePath == null)
        {
            return path.getAbsolutePath().replace(PATH_SEPARATOR_CHAR_TO_BE_CONVERTED, PATH_SEPARATOR_CHAR);
        }

        String basePathAsString = basePath.getCanonicalPath().replace(PATH_SEPARATOR_CHAR_TO_BE_CONVERTED, PATH_SEPARATOR_CHAR);
        final int length = basePathAsString.length();
        if (basePathAsString.charAt(length - 1) == PATH_SEPARATOR_CHAR)
        {
            //get rid of trailing "/". Relevant for root dirs on windows, e.g. C:/
            basePathAsString = basePathAsString.substring(0, length - 1);
        }
        final String pathAsString = path.getCanonicalPath().replace(PATH_SEPARATOR_CHAR_TO_BE_CONVERTED, PATH_SEPARATOR_CHAR);

        final boolean ignoreCase = !Platform.isOperatingSystemCaseSensitive();

        if (ignoreCase ? pathAsString.toLowerCase().startsWith(basePathAsString.toLowerCase()) : pathAsString.startsWith(basePathAsString))
        {
            //if two paths start with the same sequence, it does not mean that the path is underneath basePath.
            //e.g. path = "/home/sql-common/client.c" and basePath = "/home/sql"
            final int offset = basePathAsString.length();
            if (pathAsString.length() == offset)
            {
                return RELATIVE_PATH_CURRENT;
            }

            if (pathAsString.charAt(offset) == PATH_SEPARATOR_CHAR)
            {
                //check if path is underneath basePath
                return RELATIVE_PATH_CURRENT + pathAsString.substring(offset);
            }
        }

        if (!onDifferentRootNodes(basePathAsString, pathAsString, ignoreCase))
        {
            return getRelativePath(getPathParts(basePathAsString), getPathParts(pathAsString), ignoreCase);
        }
        return pathAsString;
    }

    /**
     * Replace Windows path separators with slashes
     * @param path  path string to be converted
     * @return      path in universal form
     */
    public static String convertPathToUniversalForm(final String path)
    {
        assert path != null : "Parameter 'path' of method 'calculatePath' must not be null";
        return path.replace(PATH_SEPARATOR_CHAR_TO_BE_CONVERTED, PATH_SEPARATOR_CHAR);
    }

    public static boolean normalizedPathMightDenoteFilePath(final String path)
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'normalizedPathMightDenoteAFilePath' must not be empty";
        final String converted = convertPathToUniversalForm(path);
        final char lastChar = converted.charAt(converted.length() - 1);
        return lastChar != EXTENSION_SEPARATOR_CHAR && lastChar != PATH_SEPARATOR_CHAR;
    }

    public static boolean isNormalizedPathAbsolute(final String path)
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'isNormalizedPathAbsolute' must not be empty";

        final String converted = convertPathToUniversalForm(path);
        if (converted.startsWith(PATH_SEPARATOR))
        {
            return true;
        }
        if (converted.length() >= 3)
        {
            return Pattern.matches("[A-Za-z]:/", converted.substring(0, 3));
        }

        return false;
    }

    private static String getRelativePath(final List<String> basePathParts, final List<String> convertToRelativePathParts, final boolean ignoreCase)
    {
        assert basePathParts != null : "'basePathParts' must not be null";
        assert convertToRelativePathParts != null : "'convertToRelativePathParts' must not be null";

        int numberOfEqualParts = 0;
        for (int i = 0; i < basePathParts.size() && i < convertToRelativePathParts.size(); i++)
        {
            if (ignoreCase ? basePathParts.get(i).equalsIgnoreCase(convertToRelativePathParts.get(i))
                    : basePathParts.get(i).equals(convertToRelativePathParts.get(i)))
            {
                numberOfEqualParts++;
            }
            else
            {
                break;
            }
        }

        final StringBuilder buffer = new StringBuilder();
        if (numberOfEqualParts == basePathParts.size())
        {
            if (numberOfEqualParts < convertToRelativePathParts.size())
            {
                for (int i = numberOfEqualParts; i < convertToRelativePathParts.size(); i++)
                {
                    if (i > numberOfEqualParts)
                    {
                        buffer.append(PATH_SEPARATOR_CHAR);
                    }
                    buffer.append(convertToRelativePathParts.get(i));
                }
            }
        }
        else
        {
            for (int i = 0; i < basePathParts.size() - numberOfEqualParts - 1; i++)
            {
                buffer.append(RELATIVE_PATH_UP);
                buffer.append(PATH_SEPARATOR_CHAR);
            }
            buffer.append(RELATIVE_PATH_UP);

            for (int i = numberOfEqualParts; i < convertToRelativePathParts.size(); i++)
            {
                buffer.append(PATH_SEPARATOR_CHAR);
                buffer.append(convertToRelativePathParts.get(i));
            }
        }

        return buffer.toString();
    }

    public static final String getCanonicalFilePath(final String fileName) throws IOException
    {
        assert fileName != null && fileName.length() > 0 : "Parameter 'fileName' of method 'getCanonicalFile' must not be empty";
        final File file = new File(fileName);
        return file.getCanonicalPath();
    }

    public static boolean pathExists(final String path)
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'readFile' must not be empty";
        return Files.exists(Paths.get(path));
    }

    public static String readFile(final String path, final Charset encoding) throws IOException
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'readFile' must not be empty";
        assert encoding != null : "Parameter 'encoding' of method 'readFile' must not be null";

        final byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    public static final File getAbsolutePath(final File basePath, final String relativePath)
    {
        assert basePath != null : "'basePath' must not be null";
        assert basePath.isDirectory() : "'basePath' must be a directory";
        assert relativePath != null : "'relativePath' must not be null";
        assert relativePath.length() > 0 : "'relativePath' must not be empty";

        final File absolutePath = new File(relativePath);
        if (!absolutePath.isAbsolute())
        {
            final String fullPath = basePath.getAbsolutePath() + FileUtility.PATH_SEPARATOR_CHAR + relativePath;
            final File calculatedPath = new File(fullPath);
            return calculatedPath;
        }

        return absolutePath;
    }

    public static boolean isAbsolutePath(final String path)
    {
        assert path != null : "Parameter 'path' of method 'isAbsolutePath' must not be null";
        return new File(path).isAbsolute();
    }

    /**
     * @return true if path start either with "." or ".."
     */
    public static boolean isRelativePath(final String path)
    {
        assert path != null : "Parameter 'path' of method 'isRelativePath' must not be null";

        if (path.startsWith(RELATIVE_PATH_CURRENT) || path.startsWith(RELATIVE_PATH_UP))
        {
            return true;
        }
        return false;
    }

    public static boolean areEqual(final File path1, final File path2) throws IOException
    {
        if (path1 == null && path2 == null)
        {
            return true;
        }
        if (path1 != null && path2 != null)
        {
            final String canPath1 = path1.getCanonicalPath();
            final String canPath2 = path2.getCanonicalPath();
            return canPath1.equals(canPath2);
        }
        //One path is 'null'
        return false;
    }

    /**
     * @return an array of files contained in the directory that matches the regular expression. If the directory does not exist or is not accessible, an empty array is returned.
     */
    public static File[] listFilesInDirectory(final File directory, final String regex)
    {
        assert directory != null : "Parameter 'directory' of method 'listFilesInDir' must not be null";

        final File[] files = directory.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(final File dir, final String name)
            {
                final boolean matches = name.matches(regex);
                return matches;
            }
        });
        if (files == null)
        {
            return new File[0];
        }
        return files;
    }

}