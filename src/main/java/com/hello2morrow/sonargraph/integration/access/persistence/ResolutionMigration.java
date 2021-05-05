/*
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
package com.hello2morrow.sonargraph.integration.access.persistence;

import java.util.ArrayList;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Version;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroupIssue;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockIssue;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMoveRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.IMoveRenameRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.INamedElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.IRenameRefactoring;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;
import com.hello2morrow.sonargraph.integration.access.model.internal.AbstractResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DependencyPatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ElementPatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ProgrammingElementImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ResolutionImpl;

public class ResolutionMigration
{
    private static final String FIELD_DESCRIPTOR = "|f";

    static boolean isPatternMigrationNeeded(final Version version)
    {
        return version.compareTo(Version.fromString("11.2.0")) < 0;
    }

    public static IResolution migrate(final IResolution resolution)
    {
        if (resolution instanceof ResolutionImpl)
        {
            return resolution;
        }

        if (resolution instanceof IRenameRefactoring || resolution instanceof IMoveRefactoring || resolution instanceof IMoveRenameRefactoring)
        {
            return resolution;
        }

        if (resolution.getIssues().size() == 0)
        {
            return resolution;
        }

        final IIssue issueProbe = resolution.getIssues().get(0);
        if (issueProbe instanceof ICycleGroupIssue || issueProbe instanceof IDuplicateCodeBlockIssue
                || issueProbe instanceof IThresholdViolationIssue)
        {
            return resolution;
        }

        final List<ProgrammingElementImpl> fieldsOfElementIssues = getAffectedFields(resolution.getIssues(), true);
        if (!fieldsOfElementIssues.isEmpty())
        {
            final List<IElementPattern> migratedPatterns = new ArrayList<>();
            final boolean elementPatternMigrationNeeded = migrateElementPatterns(fieldsOfElementIssues, resolution.getElementPatterns(),
                    migratedPatterns);
            if (elementPatternMigrationNeeded)
            {
                ((AbstractResolutionImpl) resolution).updateElementPatterns(migratedPatterns);
            }
            updateFields(fieldsOfElementIssues);
        }

        final List<ProgrammingElementImpl> fieldsOfDependencyIssues = getAffectedFields(resolution.getIssues(), false);
        if (!fieldsOfDependencyIssues.isEmpty())
        {
            final List<IDependencyPattern> migratedDependencyPatterns = new ArrayList<>();
            final boolean dependencyPatternMigrationNeeded = migrateDependencyPatterns(fieldsOfDependencyIssues, resolution.getDependencyPatterns(),
                    migratedDependencyPatterns);
            if (dependencyPatternMigrationNeeded)
            {
                ((AbstractResolutionImpl) resolution).updateDependencyPatterns(migratedDependencyPatterns);
            }

            updateFields(fieldsOfDependencyIssues);
        }

        return resolution;
    }

    private static void updateFields(final List<ProgrammingElementImpl> fields)
    {
        for (final ProgrammingElementImpl next : fields)
        {
            next.updateFqName(next.getFqName() + FIELD_DESCRIPTOR);
        }
    }

    private static boolean migrateDependencyPatterns(final List<ProgrammingElementImpl> fields, final List<IDependencyPattern> dependencyPatterns,
            final List<IDependencyPattern> migratedDependencyPatterns)
    {
        boolean migrated = false;
        for (final IDependencyPattern nextPattern : dependencyPatterns)
        {
            final String fromPattern = updatePattern(nextPattern.getFromPattern(), fields);
            final String toPattern = updatePattern(nextPattern.getToPattern(), fields);

            migratedDependencyPatterns.add(new DependencyPatternImpl(nextPattern.getType(), fromPattern, toPattern));
            migrated = migrated || !fromPattern.equals(nextPattern.getFromPattern()) || !toPattern.equals(nextPattern.getToPattern());
        }
        return migrated;
    }

    private static String updatePattern(final String pattern, final List<ProgrammingElementImpl> fields)
    {
        for (final ProgrammingElementImpl next : fields)
        {
            if (next.getFqName().equals(pattern))
            {
                return pattern + FIELD_DESCRIPTOR;
            }
        }
        return pattern;
    }

    private static boolean migrateElementPatterns(final List<ProgrammingElementImpl> fields, final List<IElementPattern> elementPatterns,
            final List<IElementPattern> migratedPatterns)
    {
        boolean migrated = false;
        for (final IElementPattern nextPattern : elementPatterns)
        {
            if (nextPattern.getHash() != null)
            {
                migratedPatterns.add(nextPattern);
            }

            ElementPatternImpl pattern = null;
            for (final INamedElement nextField : fields)
            {
                if (nextField.getFqName().equals(nextPattern.getPattern()))
                {
                    pattern = new ElementPatternImpl(nextPattern.getType(), nextPattern.getPattern() + FIELD_DESCRIPTOR);
                    break;
                }
            }
            if (pattern != null)
            {
                migratedPatterns.add(pattern);
                migrated = true;
            }
            else
            {
                migratedPatterns.add(nextPattern);
            }
        }

        return migrated;
    }

    private static List<ProgrammingElementImpl> getAffectedFields(final List<IIssue> issues, final boolean elementIssue)
    {
        final List<ProgrammingElementImpl> fields = new ArrayList<>();
        for (final IIssue nextIssue : issues)
        {
            if (elementIssue && nextIssue instanceof INamedElementIssue)
            {
                for (final INamedElement nextElement : ((INamedElementIssue) nextIssue).getAffectedNamedElements())
                {
                    collectField(fields, nextElement);
                }
            }
            else if (!elementIssue && nextIssue instanceof IDependencyIssue)
            {
                final IDependencyIssue depIssue = (IDependencyIssue) nextIssue;
                collectField(fields, depIssue.getFrom());
                collectField(fields, depIssue.getTo());
            }
        }
        return fields;
    }

    private static void collectField(final List<ProgrammingElementImpl> fields, final INamedElement element)
    {
        final String kind = element.getKind();
        switch (kind)
        {
        case "JavaField":
        case "CSharpEvent":
        case "CSharpField":
        case "CSharpProperty":
        case "PythonField":
            assert element instanceof ProgrammingElementImpl : "Not a programming element: " + element;
            fields.add((ProgrammingElementImpl) element);
            break;
        default:
            break;
        }
    }
}
