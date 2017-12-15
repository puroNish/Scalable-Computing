package org;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/NameServer")
public class NameServer {

	static final ArrayList<ServersWithFilesBean> registeredServers = new ArrayList<ServersWithFilesBean>();
	static final ArrayList<FileMetaDataBean> registeredFileMetadata = new ArrayList<FileMetaDataBean>();
	
	static final ArrayList<String> fileNameList = new ArrayList<String>();
	static ArrayList<String> authenticatedClients = new ArrayList<>();
	
	public static ArrayList<String> getAuthenticatedclients() {
		return authenticatedClients;
	}

	public static void addAuthenticatedclients(String authenticatedclients) {
		authenticatedClients.add(authenticatedclients);
	}

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
	@Path("/registerClient")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String registerClient(String clientID) {
		if(authenticatedClients.contains(clientID)) {
			return "EXISTS";
		}else {
			authenticatedClients.add(clientID);
			return "ClientID added";
		}
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
	
	@Produces(MediaType.TEXT_PLAIN)
	private static void replicationRequest(ServersWithFilesBean serverWithFile, ServersWithFilesBean serverBackup, String fileName) throws NumberFormatException, MalformedURLException {
		URL repoUrl = new URL("http", serverBackup.getServerAddress(), Integer.parseInt(serverBackup.getPortNumber()), "/");
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(repoUrl.toString()).path(serverBackup.getServerName()).path("rest").path("replication")
				.path("replicateFile").path(fileName).path(serverWithFile.getServerName()).path(serverWithFile.getServerAddress()).path(serverWithFile.getPortNumber());
		resource.request().get();
		
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
	@Path("/getFileDetails/read-only/{fileName}")
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
	@Path("/getFileDetails/read-write/{clientID}/{fileName}")
	@Produces(MediaType.APPLICATION_XML)
	public FileMetaDataBean getFileDetailsForWrite(@PathParam("fileName") String fileName,@PathParam("clientID") String clientID) {
		FileMetaDataBean emptyFileData = new FileMetaDataBean();
		for(FileMetaDataBean fileMetaData :  registeredFileMetadata) {
			if(fileMetaData.getFileName().equals(fileName)) {
				if(fileMetaData.isWriteLock()) {
					return fileMetaData;
				}else {
					fileMetaData.setWriteLock(true);
					fileMetaData.setServerWithWriteAccess(clientID);
					return fileMetaData;
				}
			}
		}
		return emptyFileData;
	}
	
	@GET
	@Path("/releaseWriteLock/release/{clientID}/{fileName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String releaseLoackForFile(@PathParam("fileName") String fileName,@PathParam("clientID") String clientID) {
		for(FileMetaDataBean fileMetaData :  registeredFileMetadata) {
			if(fileMetaData.getFileName().equals(fileName)) {
				if(fileMetaData.isWriteLock()) {
					if(fileMetaData.getServerWithWriteAccess().equals(clientID)) {
						fileMetaData.setWriteLock(false);
						fileMetaData.setServerWithWriteAccess(null);
						return "true";
					}
				}
			}
		}
		return "false";
	}
	
	@GET
	@Path("/checkFileAccess/{fileName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkWriteAccessForFile(@PathParam("fileName") String fileName) {
		for(FileMetaDataBean fileMetaData :  registeredFileMetadata) {
			if(fileMetaData.getFileName().equals(fileName)) {
				if(fileMetaData.isWriteLock()) {
					return new String("File is write protected by :: " + fileMetaData.getServerWithWriteAccess());
				}
			}
		}
		return new String("File is not locked for writing!!");
		
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
