/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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

import java.util.ArrayList;
import java.util.List;

public final class Platform
{
    private Platform()
    {
        super();
    }

    public enum OperatingSystem implements IStandardEnumeration
    {
        WINDOWS_32(4, 4),
        WINDOWS_64(4, 8),
        LINUX_32(4, 4),
        LINUX_64(8, 8),
        MAC_32(4, 4),
        MAC_64(8, 8),
        UNKNOWN(0, 0);

        private final int longSize;
        private final int pointerSize;

        OperatingSystem(final int longSize, final int pointerSize)
        {
            this.longSize = longSize;
            this.pointerSize = pointerSize;
        }

        public int getLongSize()
        {
            return longSize;
        }

        public int getPointerSize()
        {
            return pointerSize;
        }

        /**
         * @param standardName
         * @return OperatingSystem matching standardName
         * @throws IllegalArgumentException if no OS matches standardName
         */
        public static OperatingSystem fromStandardName(final String standardName)
        {
            assert standardName != null : "'standardName' must not be null";
            assert standardName.length() > 0 : "'standardName' must not be empty";
            final String name = StringUtility.convertStandardNameToConstantName(standardName);
            return OperatingSystem.valueOf(name);
        }

        @Override
        public String getStandardName()
        {
            return StringUtility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return name();
        }
    }

    public enum JavaVendor implements IStandardEnumeration
    {
        SUN,
        IBM,
        ORACLE,
        APPLE,
        UNKNOWN;

        /**
         * @param standardName
         * @return JavaVendor matching standardName
         * @throws IllegalArgumentException if no vendor matches standardName
         */
        public static JavaVendor fromStandardName(final String standardName)
        {
            assert standardName != null : "'standardName' must not be null";
            assert standardName.length() > 0 : "'standardName' must not be empty";
            final String name = StringUtility.convertStandardNameToConstantName(standardName);
            return JavaVendor.valueOf(name);
        }

        @Override
        public String getStandardName()
        {
            return StringUtility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return name();
        }
    }

    public enum JavaVersion implements IStandardEnumeration
    {
        JAVA_5,
        JAVA_6,
        JAVA_7,
        JAVA_8,
        UNKNOWN;

        /**
         * @param standardName
         * @return JavaVersion matching standardName
         * @throws IllegalArgumentException if no version matches standardName
         */
        public static JavaVersion fromStandardName(final String standardName)
        {
            assert standardName != null : "'standardName' must not be null";
            assert standardName.length() > 0 : "'standardName' must not be empty";
            final String name = StringUtility.convertStandardNameToConstantName(standardName);
            return JavaVersion.valueOf(name);
        }

        @Override
        public String getStandardName()
        {
            return StringUtility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return name();
        }
    }

    private static OperatingSystem OS;
    private static JavaVendor VENDOR;
    private static JavaVersion VERSION;

    public static JavaVersion getJavaVersion()
    {
        if (VERSION == null)
        {
            final String version = System.getProperty("java.specification.version", "unknown");

            switch (version)
            {
            case "1.5":
                VERSION = JavaVersion.JAVA_5;
                break;
            case "1.6":
                VERSION = JavaVersion.JAVA_6;
                break;
            case "1.7":
                VERSION = JavaVersion.JAVA_7;
                break;
            case "1.8":
                VERSION = JavaVersion.JAVA_8;
                break;
            default:
                VERSION = JavaVersion.UNKNOWN;
            }
        }
        return VERSION;
    }

    public static JavaVendor getJavaVendor()
    {
        if (VENDOR == null)
        {
            final String vendor = System.getProperty("java.vendor", "unknown").trim().toLowerCase();
            if (vendor.startsWith("oracle"))
            {
                // Java >= 7 (or JRockit Java <= 6)
                VENDOR = JavaVendor.ORACLE;
            }
            else if (vendor.startsWith("ibm corporation"))
            {
                // Java 4, 5, 6, 7
                VENDOR = JavaVendor.IBM;
            }
            else if (vendor.startsWith("sun microsystems"))
            {
                // Java <= 6
                VENDOR = JavaVendor.SUN;
            }
            else if (vendor.startsWith("apple"))
            {
                // Java <= 6
                VENDOR = JavaVendor.APPLE;
            }
            else
            {
                VENDOR = JavaVendor.UNKNOWN;
            }
        }
        return VENDOR;
    }

    public static boolean isOperatingSystemCaseSensitive()
    {
        switch (getOperatingSystem())
        {
        case LINUX_32:
        case LINUX_64:
            return true;
        default:
            break;
        }

        return false;
    }

    public static boolean isOperatingSystemUnixBased()
    {
        switch (getOperatingSystem())
        {
        case LINUX_32:
        case LINUX_64:
        case MAC_32:
        case MAC_64:
            return true;
        default:
            break;
        }

        return false;
    }

    public static boolean is64Bit()
    {
        final OperatingSystem os = getOperatingSystem();
        return os == OperatingSystem.WINDOWS_64 || os == OperatingSystem.LINUX_64 || os == OperatingSystem.MAC_64;
    }

    public static boolean isMac()
    {
        switch (getOperatingSystem())
        {
        case MAC_32:
        case MAC_64:
            return true;
        default:
            return false;
        }
    }

    public static boolean isLinux()
    {
        switch (getOperatingSystem())
        {
        case LINUX_32:
        case LINUX_64:
            return true;
        default:
            return false;
        }
    }

    public static boolean isWindows()
    {
        switch (getOperatingSystem())
        {
        case WINDOWS_32:
        case WINDOWS_64:
            return true;
        default:
            return false;
        }
    }

    /**
    * We can run with a 32 bit vm on 64 bit Windows - therefore we need this extra test
    *
    * @return true, if OS is 64 bit
    */
    public static boolean isOperatingSystem64Bit()
    {
        final String archName = System.getProperty("os.arch", "unknown").trim().toLowerCase();

        return archName.indexOf("64") > -1;
    }

    public static OperatingSystem getOperatingSystem()
    {
        if (OS == null)
        {
            final String osName = System.getProperty("os.name", "unknown").trim().toLowerCase();
            final String archName = System.getProperty("sun.arch.data.model", "unknown");
            final boolean is64Bit = archName.indexOf("64") > -1;

            if (osName.indexOf("windows") >= 0)
            {
                OS = is64Bit ? OperatingSystem.WINDOWS_64 : OperatingSystem.WINDOWS_32;
            }
            else if (osName.indexOf("linux") >= 0)
            {
                OS = is64Bit ? OperatingSystem.LINUX_64 : OperatingSystem.LINUX_32;
            }
            else if (osName.indexOf("mac") > -1 && osName.indexOf("x") > -1)
            {
                OS = is64Bit ? OperatingSystem.MAC_64 : OperatingSystem.MAC_32;
            }
            else
            {
                OS = OperatingSystem.UNKNOWN;
            }
        }
        return OS;
    }

    public static int[] getOperatingSystemVersion()
    {
        final String version = System.getProperty("os.version", "0.0");
        final String[] versionNumbers = version.split("\\.");
        final int[] result = new int[versionNumbers.length];
        int i = 0;

        for (final String v : versionNumbers)
        {
            result[i++] = Integer.valueOf(v);
        }
        return result;
    }

    public static List<String> getEnvironmentInfo()
    {
        final List<String> envInfo = new ArrayList<>();
        envInfo.add(getOperatingSystem().getPresentationName());
        envInfo.add(getJavaVendor().getPresentationName());
        envInfo.add(getJavaVersion().getPresentationName());
        return envInfo;
    }
}