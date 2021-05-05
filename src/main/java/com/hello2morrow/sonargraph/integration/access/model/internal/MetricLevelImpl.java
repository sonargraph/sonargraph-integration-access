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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;

public final class MetricLevelImpl extends ElementImpl implements IMetricLevel
{
    private static final long serialVersionUID = -1495880029918751555L;
    private final int orderNumber;

    public MetricLevelImpl(final String name, final String presentationName, final int orderNumber)
    {
        super(name, presentationName);
        assert orderNumber >= 0 : "orderNumber must a >= 0";
        this.orderNumber = orderNumber;
    }

    @Override
    public int getOrderNumber()
    {
        return orderNumber;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\norderNumber:" + orderNumber;
    }
}