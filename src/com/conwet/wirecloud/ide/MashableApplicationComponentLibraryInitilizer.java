package com.conwet.wirecloud.ide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.osgi.framework.Bundle;

import com.conwet.wirecloud.ide.natures.OperatorProjectNature;
import com.conwet.wirecloud.ide.natures.WidgetProjectNature;

public class MashableApplicationComponentLibraryInitilizer extends
		JsGlobalScopeContainerInitializer {

	private static final String LIBRARY_ID = "com.conwet.wirecloud.ide.mac_js_library";
	private IJavaScriptProject project;

	public IPath getPath() {
		return new Path(LIBRARY_ID);
	}

	@Override
	public LibraryLocation getLibraryLocation() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Widget API support (Open Spec of the Application Mashup GE at FI-WARE)";
	}

	@Override
	public String getDescription(IPath containerPath, IJavaScriptProject project) {
		return "";
	}

	@Override
	public void initialize(IPath containerPath, IJavaScriptProject project)
			throws CoreException {

		this.project = project;
		super.initialize(containerPath, project);
	}

	@Override
	public IIncludePathEntry[] getIncludepathEntries() {

		try {
			Bundle bundle = Platform.getBundle("com.conwet.wirecloud.eclipse.plugin");
			File bundleFile = FileLocator.getBundleFile(bundle);

			//
			File libraryFolder = new File(bundleFile, "static/WidgetAPI");
			IPath libraryPath = new Path(libraryFolder.getAbsolutePath());

			ArrayList<IIncludePathEntry> entries = new ArrayList<IIncludePathEntry>();
			if (this.project.getProject().hasNature(WidgetProjectNature.NATURE_ID)) {
				File widgetAPIjs = new File(libraryFolder, "WidgetAPI.js");
				Path widgetAPIjsPath = new Path(widgetAPIjs.getAbsolutePath());
				entries.add(JavaScriptCore.newLibraryEntry(widgetAPIjsPath, widgetAPIjsPath, libraryPath));
			} else if (this.project.getProject().hasNature(OperatorProjectNature.NATURE_ID)) {
				File operatorAPIjs = new File(libraryFolder, "OperatorAPI.js");
				Path operatorAPIjsPath = new Path(operatorAPIjs.getAbsolutePath());
				entries.add(JavaScriptCore.newLibraryEntry(operatorAPIjsPath, operatorAPIjsPath, libraryPath));
			}

			return (IIncludePathEntry[]) entries.toArray(new IIncludePathEntry[entries
					.size()]);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}
}
