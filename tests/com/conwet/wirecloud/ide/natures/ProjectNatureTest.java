/*
 *  Copyright (c) 2015 CoNWeT Lab., Universidad Politécnica de Madrid
 *  
 *  This file is part of Wirecloud IDE.
 *
 *  Wirecloud IDE is free software: you can redistribute it and/or modify
 *  it under the terms of the European Union Public Licence (EUPL)
 *  as published by the European Commission, either version 1.1
 *  of the License, or (at your option) any later version.
 *
 *  Wirecloud IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 *
 *  You should have received a copy of the European Union Public Licence
 *  along with Wirecloud IDE.
 *  If not, see <https://joinup.ec.europa.eu/software/page/eupl/licence-eupl>.
 */

package com.conwet.wirecloud.ide.natures;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import static org.mockito.Mockito.mock;


public class ProjectNatureTest extends TestCase {

    @Test
    public void testOperatorNatureSetGetOperator() {
        IProjectNature nature = new OperatorProjectNature();
        IProject project = mock(IProject.class);
        nature.setProject(project);
        assertEquals(project, nature.getProject());
        
    }

    @Test
    public void testOperatorNatureConfigure() throws CoreException {
        IProjectNature nature = new OperatorProjectNature();
        IProject project = mock(IProject.class);
        nature.setProject(project);
        nature.configure();
    }

    @Test
    public void testOperatorNatureDeconfigure() throws CoreException {
        IProjectNature nature = new OperatorProjectNature();
        IProject project = mock(IProject.class);
        nature.setProject(project);
        nature.deconfigure();
    }

    @Test
    public void testWidgetNatureSetGetOperator() {
        IProjectNature nature = new WidgetProjectNature();
        IProject project = mock(IProject.class);
        nature.setProject(project);
        assertEquals(project, nature.getProject());
        
    }

    @Test
    public void testWidgetNatureConfigure() throws CoreException {
        IProjectNature nature = new WidgetProjectNature();
        IProject project = mock(IProject.class);
        nature.setProject(project);
        nature.configure();
    }

    @Test
    public void testWidgetNatureDeconfigure() throws CoreException {
        IProjectNature nature = new WidgetProjectNature();
        IProject project = mock(IProject.class);
        nature.setProject(project);
        nature.deconfigure();
    }
}
