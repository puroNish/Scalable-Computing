package org;

import java.io.File;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Path("/FileServer1")
public class FileServer {
	final static String nameServerAddress = new String("http://localhost:8080/NameServer/rest/NameServer");
	final static String workingDir = "C:\\FileServer\\FileServer1";
	final static String serverName = new String("FileServer1");
	final static String serverUrl = new String("127.0.0.1");
	final static String serverPort = new String("8080");
	static ArrayList<FileMetaServerDataBean> listOfFiles = new ArrayList<FileMetaServerDataBean>();
	static {
		loadFileSystem();
		registerServer();
	}

	public static void main(String[] args) {
		loadFileSystem();
		registerServer();

		FileServer obj = new FileServer();
//		Response response = obj.getFile("one.txt");
//		System.out.println(response.getEntity().toString());
		System.out.println(obj.testServer());
/*        Response response = target.request()
                .header(HttpHeaders.COOKIE, this.cookie)
                .header(HttpHeaders.CONTENT_TYPE, "multipart/mixed")
                .accept("multipart/mixed").get();*/

	}

	@GET
	@Path("/testServer")
	@Produces(MediaType.TEXT_PLAIN)
	public String testServer() {
		return new String("Server is UP");
	}

	public static void loadFileSystem() {
		File fileSystemRoot = new File(workingDir);
		File[] fileSystem = fileSystemRoot.listFiles();
		for (File tempFile : fileSystem) {
			FileMetaServerDataBean tempBean = new FileMetaServerDataBean();
			tempBean.setFileName(tempFile.getName());
			tempBean.setServerName(serverName);
			listOfFiles.add(tempBean);
		}
	}

	@GET
	@Path("/download/{fileName}")
	
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("fileName") String fileName) {
		for (FileMetaServerDataBean tempBean : listOfFiles) {
			if (tempBean.getFileName().equals(fileName)) {
				File file = new File(workingDir.concat("\\").concat(fileName));
				return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
						.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();
			}
		}
		return Response.serverError().build();
	}

	@POST
	@Path("/registerServer")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.TEXT_PLAIN)
	public static void registerServer() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(nameServerAddress);
		FileServerBean fileServerBean = new FileServerBean();
		fileServerBean.setServerName(serverName);
		fileServerBean.setServerAddress(serverUrl);
		fileServerBean.setPortNumber(serverPort);
		fileServerBean.setListOfFiles(listOfFiles);
		WebResource testServerResource = resource.path("testServer");
		WebResource nameServerResource = resource.path("registerFileServer");
		boolean notRegistered = true;
		String testServer = testServerResource.accept(MediaType.TEXT_PLAIN).get(String.class);
		// if (testServer.equals("Server is UP")) {
		while (notRegistered && testServer.equals("Server is UP")) {
			ClientResponse response = nameServerResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class,
					fileServerBean);
			String output = response.getEntity(String.class);
			if (output.equals("EXISTS") || output.equals("REGISTERED")) {
				notRegistered = false;
			}
		}
		client.destroy();
		// }
	}
}
