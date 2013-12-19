package com.conwet.wirecloud.ide;

import java.io.File;
import java.io.IOException;

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

public class MashableApplicationComponentLibraryInitilizer extends
		JsGlobalScopeContainerInitializer {

	private static final String LIBRARY_ID = "com.conwet.wirecloud.ide.mac_js_library";

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
	public IIncludePathEntry[] getIncludepathEntries() {

		try {
			// get the Bundle object of the plugin
			Bundle bundle = Platform.getBundle("com.conwet.wirecloud.eclipse.plugin");
			// get the java.io.File object corresponding to the root of the
			// bundles installation directory
			File bundleFile = FileLocator.getBundleFile(bundle);
			// add the location pointing to the library relative to that bundle
			// root
			File libraryLocation = new File(bundleFile,
					"static/WidgetAPI/WidgetAPI.js");
			// create a Path object from it
			IPath pa = new Path(libraryLocation.getAbsolutePath());

			/*
			 * create an IIncludePathEntry of the type "library" from this path
			 * my library only contains one folder (for now) so this is it
			 */
			IIncludePathEntry entry = JavaScriptCore.newLibraryEntry(pa, pa, pa);
			// put the entry (or entries if you had more) into an array and
			// return
			IIncludePathEntry[] entries = { entry };
			return entries;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
