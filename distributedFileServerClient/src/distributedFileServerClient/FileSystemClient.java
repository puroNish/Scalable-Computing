package distributedFileServerClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class FileSystemClient {
	final static String workingDir = "C:\\FileClient";
	static boolean writeAction = false;
	static boolean readAction = false;
	// static boolean deleteAction = false;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileSystemClient obj = new FileSystemClient();
		System.out.println("File Server is up!!");
		int input = 0;
		Scanner scanner = new Scanner(System.in);
		obj.showListOfFiles();
		System.out.println("Select file ::");
		String fileName = scanner.next();
		System.out.println(fileName);
		while (!writeAction && !readAction) {
			System.out.println("Select action required ::");
			System.out.println("1> To read a file");
			System.out.println("2> To write a file");
			// System.out.println("3> To delete a file");
			input = scanner.nextInt();
			obj.setAction(input);
		}
		FileMetaDataBean fileData = obj.getFileDetails(fileName);
		String serverName = fileData.getServersWithFile().get(0);
		ServersWithFilesBean serverData = obj.getServerDetails(serverName);
		if(readAction) {
			obj.readFileFromServer(fileName, serverData);
		}
		scanner.close();
	}
	
	private void readFileFromServer(String fileName, ServersWithFilesBean serverData) throws MalformedURLException, FileNotFoundException {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		URL repoUrl = new URL("http",serverData.getServerAddress(),Integer.parseInt(serverData.getPortNumber()),"/");
		WebResource resource = client.resource(repoUrl.toString()).path(serverData.getServerName()).path("rest").path(serverData.getServerName()).path("download").path(fileName);
		File newFile = new File(workingDir);
		newFile = resource.accept(MediaType.APPLICATION_OCTET_STREAM).get(File.class);
		FileOutputStream str = new FileOutputStream(newFile);
		System.out.println(str);
	}

	private void setAction(int input) {
		if (input == 1) {
			writeAction = false;
			readAction = true;
			// deleteAction = false;
			System.out.println("Action set to 'read-only'");
		} else if (input == 2) {
			writeAction = true;
			readAction = false;
			// deleteAction = false;
			System.out.println("Action set to 'read - write'");
		} else {
			System.out.println("Invalid option. \nPlease select a valid option.");
		}

		/*
		 * else if (input == 3) { writeAction = false; readAction = false; deleteAction
		 * = true; }
		 */
	}
	
	

	private WebResource connectToTheFileService(String serviceName) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource("http://localhost:8080/NameServer/rest/NameServer");
		WebResource nameResource = resource.path(serviceName);
		return nameResource;
	}

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	private void showListOfFiles() {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("getListOfFiles");
		String filesAvailable = resource.accept(MediaType.APPLICATION_XML).get(String.class);
		System.out.println("Files available on Servers :: ");
		System.out.println(filesAvailable);
	}

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	private FileMetaDataBean getFileDetails(String fileName) {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("getFileDetails").path(fileName);
		FileMetaDataBean fileData = resource.accept(MediaType.APPLICATION_XML).get(FileMetaDataBean.class);
		return fileData;
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	private ServersWithFilesBean getServerDetails(String serverName) {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("getServerDetails").path(serverName);
		ServersWithFilesBean serverData = resource.accept(MediaType.APPLICATION_XML).get(ServersWithFilesBean.class);
		return serverData;
	}

}
