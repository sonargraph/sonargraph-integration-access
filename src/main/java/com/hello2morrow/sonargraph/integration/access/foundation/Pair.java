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

import java.io.Serializable;

public class Pair<T1 extends Serializable, T2 extends Serializable> implements Serializable
{
    private static final long serialVersionUID = 7247108645914680029L;
    private final T1 t1;
    private final T2 t2;

    public Pair(final T1 t1, final T2 t2)
    {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getFirst()
    {
        return t1;
    }

    public T2 getSecond()
    {
        return t2;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
        result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (t1 == null)
        {
            if (other.t1 != null)
            {
                return false;
            }
        }
        else if (!t1.equals(other.t1))
        {
            return false;
        }
        if (t2 == null)
        {
            if (other.t2 != null)
            {
                return false;
            }
        }
        else if (!t2.equals(other.t2))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return t1 + "|" + t2;
    }
}
