package com.conwet.wirecloud;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class WirecloudServer extends ServerDelegate {
	private ArrayList<String> listToRetreat;


	@Override
	public ServerPort[] getServerPorts() {
		List<ServerPort> ports = new ArrayList<ServerPort>();
		ports.add(new ServerPort("server", "HTTP", 80, "HTTP"));         //$NON-NLS-1$

		return (ServerPort[])ports.toArray(new ServerPort[ports.size()]);
	}

	@Override
	public IStatus canModifyModules(IModule[] arg0, IModule[] arg1) {
		return Status.OK_STATUS;
	}

	@Override
	public IModule[] getChildModules(IModule[] arg0) {
		return arg0;
	}


	@Override
	public IModule[] getRootModules(IModule arg0) throws CoreException {

		return new IModule[]{arg0};
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove,
			IProgressMonitor monitor) throws CoreException {

		IServer server = this.getServer();
		String TOKEN = server.getAttribute("TOKEN", "wirecloud_token");
		int PORT = server.getAttribute("PORT", 80);
		Zip zipper = new Zip();
		WirecloudAPI api=null;
		try {
			if(server.getHost().equals("localhost"))
				api = new WirecloudAPI("http://" + server.getHost() + ":" + PORT);
			else
				api = new WirecloudAPI(server.getHost());
			api.setToken(TOKEN);
			if(add.length>0){
				for (IModule module : add) {
					IProject project = module.getProject();
					String newPath = "/tmp/" + project.getName() + ".wgt";
					zipper.zipFile(project.getLocation().toOSString(), newPath, true);
					api.deployWGT(newPath, TOKEN);
				}
			}
			if(remove.length>0){
				listToRetreat=new ArrayList<>();
				for (IModule module : remove) {
					IProject project = module.getProject();
					File fXmlFile = new File(project.getLocation() + "/config.xml");
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();
					
					NodeList nList = doc.getElementsByTagName("Catalog.ResourceDescription");
					Node nNode = nList.item(0);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						String vendor = eElement.getElementsByTagName("Vendor").item(0).getTextContent();
						String Name = eElement.getElementsByTagName("Name").item(0).getTextContent();
						String version = eElement.getElementsByTagName("Version").item(0).getTextContent();
						String element = vendor+"/"+Name+"/"+version;
						//System.out.println(element);
						listToRetreat.add(element);
					}
				}
				Iterator<String> resourcesToDeleteIterator = this.listToRetreat.iterator();
				
				while(resourcesToDeleteIterator.hasNext()){
					api.deleteCatalogueResource(resourcesToDeleteIterator.next());
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configurationChanged() {
		// TODO Auto-generated method stub
		super.configurationChanged();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}


	@Override
	public void importRuntimeConfiguration(IRuntime runtime,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.importRuntimeConfiguration(runtime, monitor);
	}


	@Override
	public boolean isUseProjectSpecificSchedulingRuleOnPublish() {
		// TODO Auto-generated method stub
		return super.isUseProjectSpecificSchedulingRuleOnPublish();
	}





}
