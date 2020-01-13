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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.DependencyPatternType;
import com.hello2morrow.sonargraph.integration.access.model.ElementPatternType;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMatching;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.Priority;
import com.hello2morrow.sonargraph.integration.access.model.RefactoringStatus;
import com.hello2morrow.sonargraph.integration.access.model.ResolutionType;
import com.hello2morrow.sonargraph.integration.access.model.internal.DeleteRefactoringImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.DependencyPatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ElementPatternImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.FixResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IgnoreResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.IssueImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MatchingImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MoveRefactoringImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MoveRenameRefactoringImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.RenameRefactoringImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.ToDoResolutionImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.ValidationEventHandlerImpl.ValidationMessageCauses;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDeleteRefactoring;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDependencyPattern;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdDependencyPatternType;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementPattern;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdElementPatternType;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdFixMe;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIgnore;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdIssue;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMatching;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMoveRefactoring;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdMoveRenameRefactoring;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdRefactoring;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdRenameRefactoring;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdResolution;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdSoftwareSystemReport;
import com.hello2morrow.sonargraph.integration.access.persistence.report.XsdToDo;

@SuppressWarnings("deprecation") //We need to handle deprecated ResolutionImpl
public class ResolutionConverter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionConverter.class);

    private final SoftwareSystemImpl softwareSystem;
    private final XsdSoftwareSystemReport report;

    private final Map<Object, IssueImpl> globalXmlIdToIssueMap;

    public ResolutionConverter(final SoftwareSystemImpl softwareSystem, final XsdSoftwareSystemReport report,
            final Map<Object, IssueImpl> globalXmlIdToIssueMap)
    {
        this.softwareSystem = softwareSystem;
        this.report = report;
        this.globalXmlIdToIssueMap = globalXmlIdToIssueMap;
    }

    public void convert(final Result result)
    {
        if (report.getResolutions() == null)
        {
            //no resolutions to be processed
            return;
        }

        for (final XsdResolution nextXsdResolution : report.getResolutions().getResolution())
        {
            ResolutionType type;
            if (nextXsdResolution.isRefactoring())
            {
                type = ResolutionType.REFACTORING;
            }
            else
            {
                try
                {
                    type = ResolutionType.valueOf(nextXsdResolution.getType().toUpperCase());
                }
                catch (final Exception e)
                {
                    LOGGER.error("Failed to process resolution type '" + nextXsdResolution.getType() + "'", e);
                    result.addError(ValidationMessageCauses.NOT_SUPPORTED_ENUM_CONSTANT,
                            "Resolution type '" + nextXsdResolution.getType() + "' is not supported and will be ignored.");
                    continue;
                }
            }

            Priority priority;
            try
            {
                priority = Priority.valueOf(Utility.convertStandardNameToConstantName(nextXsdResolution.getPrio()));
            }
            catch (final Exception e)
            {
                LOGGER.error("Failed to process priority type '" + nextXsdResolution.getPrio() + "'", e);
                result.addWarning(ValidationMessageCauses.NOT_SUPPORTED_ENUM_CONSTANT,
                        "Priority type '" + nextXsdResolution.getPrio() + "' is not supported, setting to '" + Priority.NONE + "'");
                priority = Priority.NONE;
            }

            final List<IIssue> issues = new ArrayList<>();
            for (final Object nextXsdIssue : nextXsdResolution.getIssueIds())
            {
                final XsdIssue xsdIssue = (XsdIssue) nextXsdIssue;
                final IssueImpl nextIssueImpl = globalXmlIdToIssueMap.get(xsdIssue);
                assert nextIssueImpl != null : "No issue with id '" + xsdIssue.getId() + "' exists";
                nextIssueImpl.setResolutionType(type);
                issues.add(nextIssueImpl);
            }

            final List<IElementPattern> elementPatterns = createElementPatterns(result, nextXsdResolution);
            final List<IDependencyPattern> dependencyPatterns = createDependencyPatterns(result, nextXsdResolution, type);
            final IMatching matching = createMatching(result, nextXsdResolution);

            final Integer matchingElementsCount = nextXsdResolution.getMatchingElementsCount();

            final String fqName = nextXsdResolution.getFqName();
            final String description = nextXsdResolution.getDescription();
            final String information = nextXsdResolution.getInformation();
            final String assignee = nextXsdResolution.getAssignee();
            final Date dateTime = nextXsdResolution.getDate().toGregorianCalendar().getTime();
            final String descriptor = nextXsdResolution.getDescriptor();

            final IResolution resolutionToAdd;
            if (nextXsdResolution instanceof XsdIgnore)
            {
                resolutionToAdd = new IgnoreResolutionImpl(fqName, priority, issues, matchingElementsCount, description, information, assignee,
                        dateTime, elementPatterns, dependencyPatterns, matching, descriptor);
            }
            else if (nextXsdResolution instanceof XsdToDo)
            {
                resolutionToAdd = new ToDoResolutionImpl(fqName, priority, issues, matchingElementsCount, description, information, assignee,
                        dateTime, elementPatterns, dependencyPatterns, matching, descriptor);
            }
            else if (nextXsdResolution instanceof XsdFixMe)
            {
                resolutionToAdd = new FixResolutionImpl(fqName, priority, issues, matchingElementsCount, description, information, assignee,
                        dateTime, elementPatterns, dependencyPatterns, matching, descriptor);
            }
            else if (nextXsdResolution instanceof XsdRefactoring)
            {
                final XsdRefactoring xsdRefactoring = (XsdRefactoring) nextXsdResolution;
                final int potentiallyDoneElementCount = xsdRefactoring.getNumberOfPotentiallyDoneElements();
                final RefactoringStatus status = RefactoringStatus.createFromStandardName(xsdRefactoring.getStatus());
                if (nextXsdResolution instanceof XsdDeleteRefactoring)
                {
                    final int numberOfAffectedParserDependencies = ((XsdDeleteRefactoring) nextXsdResolution).getNumberOfAffectedParserDependencies();
                    resolutionToAdd = new DeleteRefactoringImpl(fqName, priority, issues, matchingElementsCount, numberOfAffectedParserDependencies,
                            description, information, assignee, dateTime, elementPatterns, dependencyPatterns, matching, descriptor, status,
                            potentiallyDoneElementCount);
                }
                else if (nextXsdResolution instanceof XsdRenameRefactoring)
                {
                    final String newName = ((XsdRenameRefactoring) nextXsdResolution).getNewName();
                    resolutionToAdd = new RenameRefactoringImpl(fqName, priority, issues, matchingElementsCount, description, information, assignee,
                            dateTime, elementPatterns, dependencyPatterns, matching, descriptor, status, potentiallyDoneElementCount, newName);
                }
                else if (nextXsdResolution instanceof XsdMoveRenameRefactoring)
                {
                    final XsdMoveRenameRefactoring xsdMoveRename = (XsdMoveRenameRefactoring) nextXsdResolution;
                    final String targetRootDirectoryFqName = xsdMoveRename.getTargetRootDirectoryFqName();
                    final String moveToParentName = xsdMoveRename.getMoveToParentName();
                    final String elementKind = xsdMoveRename.getElementKind();
                    final String newName = xsdMoveRename.getNewName();
                    resolutionToAdd = new MoveRenameRefactoringImpl(fqName, priority, issues, matchingElementsCount, description, information,
                            assignee, dateTime, elementPatterns, dependencyPatterns, matching, descriptor, status, potentiallyDoneElementCount,
                            targetRootDirectoryFqName, moveToParentName, elementKind, newName);
                }
                else if (nextXsdResolution instanceof XsdMoveRefactoring)
                {
                    final XsdMoveRefactoring xsdMove = (XsdMoveRefactoring) nextXsdResolution;
                    final String targetRootDirectoryFqName = xsdMove.getTargetRootDirectoryFqName();
                    final String moveToParentName = xsdMove.getMoveToParentName();
                    final String elementKind = xsdMove.getElementKind();
                    resolutionToAdd = new MoveRefactoringImpl(fqName, priority, issues, matchingElementsCount, description, information, assignee,
                            dateTime, elementPatterns, dependencyPatterns, matching, descriptor, status, potentiallyDoneElementCount,
                            targetRootDirectoryFqName, moveToParentName, elementKind);
                }
                else
                {
                    assert false : "Unsupported refactoring " + fqName + ", descriptor: " + descriptor;
                    resolutionToAdd = null;
                }
            }
            else
            {
                //handle deprecated resolution
                resolutionToAdd = new ResolutionImpl(fqName, type, priority, issues, matchingElementsCount, nextXsdResolution.isApplicable(),
                        nextXsdResolution.getNumberOfAffectedParserDependencies(), description, information, assignee, dateTime, elementPatterns,
                        dependencyPatterns, matching, descriptor);
            }
            softwareSystem.addResolution(resolutionToAdd);
        }
    }

    private IMatching createMatching(final Result result, final XsdResolution xsdResolution)
    {
        IMatching matching;
        final XsdMatching xsdMatching = xsdResolution.getMatching();
        if (xsdMatching != null)
        {
            final String matchingInfo = xsdMatching.getInfo();
            if (matchingInfo != null && !matchingInfo.isEmpty())
            {
                final List<IElementPattern> matchingPatterns = new ArrayList<>();
                for (final XsdElementPattern nextXsdElementPattern : xsdMatching.getElementPattern())
                {
                    final XsdElementPatternType nextXsdElementPatternType = nextXsdElementPattern.getType();
                    if (nextXsdElementPatternType != null && nextXsdElementPatternType == XsdElementPatternType.FULLY_QUALIFIED_NAME)
                    {
                        final String nextXsdValue = nextXsdElementPattern.getValue();
                        if (nextXsdValue != null && !nextXsdValue.isEmpty())
                        {
                            matchingPatterns.add(new ElementPatternImpl(ElementPatternType.FULLY_QUALIFIED_NAME, nextXsdValue));
                        }
                        else
                        {
                            result.addWarning(ValidationMessageCauses.XML_VALIDATION_WARNING, "Empty matching pattern");
                            break;
                        }
                    }
                    else
                    {
                        result.addWarning(ValidationMessageCauses.XML_VALIDATION_WARNING, "Unspported pattern type: " + nextXsdElementPatternType);
                    }
                }
                matching = new MatchingImpl(matchingInfo, matchingPatterns);
            }
            else
            {
                result.addWarning(ValidationMessageCauses.XML_VALIDATION_WARNING, "Empty matching info");
                matching = null;
            }
        }
        else
        {
            matching = null;
        }
        return matching;
    }

    public List<IDependencyPattern> createDependencyPatterns(final Result result, final XsdResolution nextResolution, final ResolutionType type)
    {
        final List<IDependencyPattern> dependencyPatterns = new ArrayList<>();
        for (final XsdDependencyPattern xsdDependency : nextResolution.getDependencyPattern())
        {
            final XsdDependencyPatternType xsdPatternType = xsdDependency.getType();
            final String fromPattern = xsdDependency.getFrom();
            final String toPattern = xsdDependency.getTo();
            if (fromPattern == null || fromPattern.isEmpty())
            {
                result.addError(ValidationMessageCauses.XML_VALIDATION_WARNING, "From pattern is empty.");
                continue;
            }
            if (toPattern == null || toPattern.isEmpty())
            {
                result.addError(ValidationMessageCauses.XML_VALIDATION_WARNING, "To pattern is empty.");
                continue;
            }

            final IDependencyPattern dependencyPattern;
            if (xsdPatternType == null || xsdPatternType == XsdDependencyPatternType.WILDCARD)
            {
                dependencyPattern = new DependencyPatternImpl(DependencyPatternType.WILDCARD, fromPattern, toPattern);
            }
            else
            {
                assert xsdPatternType == XsdDependencyPatternType.PARSER_DEPENDENCY_ENDPOINT : "Unexpected type: " + type;
                dependencyPattern = new DependencyPatternImpl(DependencyPatternType.PARSER_DEPENDENCY_ENDPOINT, fromPattern, toPattern);
            }
            dependencyPatterns.add(dependencyPattern);
        }
        return dependencyPatterns;
    }

    public List<IElementPattern> createElementPatterns(final Result result, final XsdResolution nextResolution)
    {
        final List<IElementPattern> elementPatterns = new ArrayList<>();
        for (final XsdElementPattern nextXsdElementPattern : nextResolution.getElementPattern())
        {
            final XsdElementPatternType xsdPatternType = nextXsdElementPattern.getType();
            final ElementPatternType patternType = ElementPatternType.valueOf(xsdPatternType.name());
            final String pattern = nextXsdElementPattern.getValue();

            if (pattern == null || pattern.isEmpty())
            {
                result.addError(ValidationMessageCauses.XML_VALIDATION_WARNING, "Pattern of resolution is empty.");
                continue;
            }

            final IElementPattern elementPattern = new ElementPatternImpl(patternType, pattern);
            elementPatterns.add(elementPattern);
        }
        return elementPatterns;
    }
}
