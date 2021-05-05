/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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

import java.io.Serializable;

public final class Version implements Comparable<Version>, Serializable
{
    private static final long serialVersionUID = 6387606412181055570L;

    final int major;
    final int minor;
    final int micro;
    final int build;

    public Version(final int major, final int minor, final int micro, final int build)
    {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.build = build;
    }

    @Override
    public int compareTo(final Version o)
    {
        int diff = major - o.major;

        if (diff != 0)
        {
            return diff;
        }
        diff = minor - o.minor;
        if (diff != 0)
        {
            return diff;
        }
        diff = micro - o.micro;
        if (diff != 0)
        {
            return diff;
        }
        diff = build - o.build;
        return diff;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final Version version = (Version) o;

        if (major != version.major)
        {
            return false;
        }
        if (minor != version.minor)
        {
            return false;
        }
        if (micro != version.micro)
        {
            return false;
        }
        return build == version.build;
    }

    @Override
    public int hashCode()
    {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + micro;
        result = 31 * result + build;
        return result;
    }

    @Override
    public String toString()
    {
        return String.format("%d.%d.%d.%d", major, minor, micro, build);
    }

    public String getVersionWithoutBuildNumber()
    {
        return String.format("%d.%d.%d", major, minor, micro);
    }

    public static Version fromString(final String versionString)
    {
        assert versionString != null : "Parameter 'versionString' of method 'fromString' must not be null";

        final String[] parts = versionString.split("\\.", 4);
        if (parts.length < 3)
        {
            throw new IllegalArgumentException("Version '" + versionString + "' does not match the expected format a.b.c");
        }

        try
        {
            final int major = Integer.parseInt(parts[0]);
            final int minor = Integer.parseInt(parts[1]);
            final int micro = Integer.parseInt(parts[2]);

            final int build;
            if (parts.length == 4)
            {
                build = Integer.parseInt(parts[3]);
            }
            else
            {
                build = 0;
            }

            return new Version(major, minor, micro, build);
        }
        catch (final NumberFormatException ex)
        {
            throw new IllegalArgumentException("Version '" + versionString + "' does not match the expected format a.b.c.d", ex);
        }
    }
}