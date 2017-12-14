package org;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/NameServer")
public class NameServer {

	static final ArrayList<ServersWithFilesBean> registeredServers = new ArrayList<ServersWithFilesBean>();
	static final ArrayList<FileMetaDataBean> registeredFileMetadata = new ArrayList<FileMetaDataBean>();
	static final ArrayList<String> fileNameList = new ArrayList<String>();
	static boolean serverReady = false;

	public NameServer() {
		serverReady = true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@GET
	@Path("/testServer")
	@Produces(MediaType.TEXT_PLAIN)
	public String testServer() {
		if (serverReady)
			return new String("Server is UP");
		new NameServer();
		return new String("Server still loading");
	}

	@GET
	@Path("/activeFileServers")
	@Produces(MediaType.TEXT_PLAIN)
	public String getActiveServers() {
		if (serverReady) {
			return Integer.toString(registeredServers.size());
		}
		new NameServer();
		return new String("No Active File Server yet!!");

		// return Integer.toString(registeredServers.size());
	}

	@POST
	@Path("/registerFileServer")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public String registerServer(ServersWithFilesBean serverBean) {
		for (ServersWithFilesBean fileServerBean : registeredServers) {
			if (fileServerBean.getServerName().equals(serverBean.getServerName())) {
				return new String("EXISTS");
			}
		}
		registeredServers.add(serverBean);
		populateInitialDatabase(serverBean);
		return new String("REGISTERED");
	}

	private void populateInitialDatabase(ServersWithFilesBean serverBean) {
		ArrayList<FileMetaServerDataBean> filesOnServer = serverBean.getListOfFiles();
		String serverName = serverBean.getServerName();
		for (FileMetaServerDataBean fileFromServer : filesOnServer) {
			if (registeredFileMetadata.size() != 0)
				for (FileMetaDataBean fileOnServer : registeredFileMetadata) {
					if (fileFromServer.getFileName().equals(fileOnServer.getFileName())) {
						ArrayList<String> serverList = fileOnServer.getServersWithFile();
						serverList.add(serverName);
						fileOnServer.setServersWithFile(serverList);
					} else {
						addNewFileToDatabase(fileFromServer.getFileName(), serverName);
					}
				}
			addNewFileToDatabase(fileFromServer.getFileName(), serverName);
		}
	}

	private void addNewFileToDatabase(String fileName, String serverName) {
		FileMetaDataBean mainBean = new FileMetaDataBean();
		ArrayList<String> serverList = new ArrayList<>();
		serverList.add(serverName);
		mainBean.setServersWithFile(serverList);
		mainBean.setServerWithWriteAccess(null);
		mainBean.setWriteLock(false);
		mainBean.setFileName(fileName);
		registeredFileMetadata.add(mainBean);
		fileNameList.add(fileName);
	}

	@GET
	@Path("/getListOfFiles")
	@Produces(MediaType.APPLICATION_XML)
	public String getListOfFiles() {
		if(fileNameList.isEmpty()) {
			return null;
		}
/*//		String[] temp = fileNameList.toArray(new String[fileNameList.size()]);
		return Response.status(Response.Status.OK).entity(fileNameList.toArray(new String[fileNameList.size()])).build();*/
		return fileNameList.get(0);
	}
	
	@GET
	@Path("/getFileDetails/{fileName}")
	@Produces(MediaType.APPLICATION_XML)
	public FileMetaDataBean getFileDetails(@PathParam("fileName") String fileName) {
		for(FileMetaDataBean fileMetaData :  registeredFileMetadata) {
			if(fileMetaData.getFileName().equals(fileName)) {
				return fileMetaData;
			}
		}
		return null;
	}
	
	@GET
	@Path("/getServerDetails/{serverName}")
	@Produces(MediaType.APPLICATION_XML)
	public ServersWithFilesBean getServerDetails(@PathParam("serverName") String serverName) {
		for(ServersWithFilesBean serverMetaData :  registeredServers) {
			if(serverMetaData.getServerName().equals(serverName)) {
				return serverMetaData;
			}
		}
		return null;
	}

}
