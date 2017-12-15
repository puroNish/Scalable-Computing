package distributedFileServerClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class FileSystemClient {
	static int randomVal = (int) (Math.random() * 100);
	final static String authServerAddress = new String("http://localhost:8080/AuthenticationServer/rest/AuthServer");
	final static String clinetID = "ClientID_" + randomVal;
	final static String workingDir = "/FileClient";
	final static String cacheDir = workingDir.concat("/cache");
	static boolean writeAction = false;
	static boolean readAction = false;
	static String nameServerAddress;
	// static boolean deleteAction = false;

	public static String getNameServerAddress() {
		return nameServerAddress;
	}

	public static void setNameServerAddress(String nameServerAddress) {
		FileSystemClient.nameServerAddress = nameServerAddress;
		System.out.println(FileSystemClient.nameServerAddress);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		FileSystemClient obj = new FileSystemClient();
		System.out.println("File Server is up!!");
		System.out.println("Need to authenticate first!!");
		System.out.print("User Name :: ");
		String uID = scanner.next();
		System.out.print("Password :: ");
		String pass = scanner.next();
		// String address = obj.authenticate(uID, pass); // commented because of runtime
		// issue taking up too much time
		String address = new String("http://localhost:8080/NameServer/rest/NameServer");
		// System.out.println(">>>>>>>>"+address);
		// if (!address.equals("WRONG")) {
		setNameServerAddress(address);
		int input = 0;
		ArrayList<String> files = obj.showListOfFiles();
		boolean correctFileSelected = false;

		System.out.println("Select file ::");
		String fileName = scanner.next();

		while (!correctFileSelected) {
			System.out.println("No such file ::" + fileName);
			fileName = scanner.next();
			if (files.contains(fileName)) {
				correctFileSelected = true;
			}
		}
		FileMetaDataBean fileData = new FileMetaDataBean();
		while (!writeAction && !readAction) {
			System.out.println("Select action required ::");
			System.out.println("1> To read a file");
			System.out.println("2> To write a file");
			// System.out.println("3> To delete a file");
			input = scanner.nextInt();
			obj.setAction(input);

		}
		if (readAction) {
			fileData = obj.getFileDetailsForRead(fileName);
		} else if (writeAction) {
			fileData = obj.getFileDetailsForWrite(fileName);
			if (obj.missedWriteAccess(fileData)) {
				System.out.println("Could not get file for write!! Locked by other client!!");
				obj.setAction(1);
			}
		}
		String serverName = fileData.getServersWithFile().get(0);
		ServersWithFilesBean serverData = obj.getServerDetails(serverName);
		if (readAction) {
			obj.readFileFromServer(fileName, serverData);
		}
		while (writeAction) {
			obj.getFileForWriteFromServer(fileName, serverData);
			System.out.println("Update and release write lock? {1:Yes  ;2: No}");
			int release = scanner.nextInt();
			if (release == 1) {
				obj.releaseWriteLock(fileName);
			}

		}
		// }
		scanner.close();
	}

	private boolean missedWriteAccess(FileMetaDataBean fileData) {
		if (fileData.isWriteLock() && fileData.getServerWithWriteAccess().equals(clinetID)) {
			return false;
		}
		return true;

	}

	private void getFileForWriteFromServer(String fileName, ServersWithFilesBean serverData)
			throws NumberFormatException, IOException {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		URL repoUrl = new URL("http", serverData.getServerAddress(), Integer.parseInt(serverData.getPortNumber()), "/");
		WebTarget resource = client.target(repoUrl.toString()).path(serverData.getServerName()).path("rest")
				.path("downloadUtility").path("download").path(fileName);
		Response response = resource.request(MediaType.APPLICATION_OCTET_STREAM).get();
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			InputStream in = response.readEntity(InputStream.class);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			System.out.println();
			System.out.println("********** File Name >> " + fileName + "************************");
			System.out.println();
			while ((line = br.readLine()) != null) {
				System.out.println(":::: " + line);
			}
			System.out.println();
			System.out.println("**********>>END OF FILE<<**********");
			System.out.println();
			saveFileForCache(in, fileName);
		} else {
			System.out.println("ERROR ERROR");
		}
	}

	@GET
	private void releaseWriteLock(String fileName) {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("releaseWriteLock/release").path(clinetID).path(fileName);
		String pending = resource.get(String.class);
		if (pending.equals("false")) {
			obj.releaseWriteLock(fileName);
		} else {
			System.out.println("File write lock released");
			obj.setAction(1);
		}
	}

	private void readFileFromServer(String fileName, ServersWithFilesBean serverData)
			throws NumberFormatException, IOException {
		File newFile = new File(cacheDir, fileName);
		if (newFile.exists()) {
			InputStream in = new FileInputStream(newFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			System.out.println();
			System.out.println("********** File Name >> " + fileName + "************************");
			System.out.println();
			while ((line = br.readLine()) != null) {
				System.out.println(":::: " + line);
			}
			System.out.println();
			System.out.println("**********>>END OF FILE<<**********");
			System.out.println();
			saveFileForCache(in, fileName);
		} else {
			javax.ws.rs.client.Client client = ClientBuilder.newClient();
			URL repoUrl = new URL("http", serverData.getServerAddress(), Integer.parseInt(serverData.getPortNumber()),
					"/");
			WebTarget resource = client.target(repoUrl.toString()).path(serverData.getServerName()).path("rest")
					.path("downloadUtility").path("download").path(fileName);
			Response response = resource.request(MediaType.APPLICATION_OCTET_STREAM).get();
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				InputStream in = response.readEntity(InputStream.class);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				System.out.println();
				System.out.println("********** File Name >> " + fileName + "************************");
				System.out.println();
				while ((line = br.readLine()) != null) {
					System.out.println(":::: " + line);
				}
				System.out.println();
				System.out.println("**********>>END OF FILE<<**********");
				System.out.println();
				saveFileForCache(in, fileName);
			} else {
				System.out.println("ERROR ERROR");
			}
		}
	}

	private File searchCacheForFile(String fileName) {
		File newFile = new File(cacheDir, fileName);
		if (newFile.exists()) {
			return newFile;
		}
		return null;
	}

	private void saveFileForCache(InputStream in, String fileName) throws IOException {
		File newFile = new File(cacheDir, fileName);
		newFile.createNewFile();
		Path path = newFile.toPath();
		Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		in.close();
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
		System.out.println(nameServerAddress);
		WebResource resource = client.resource(nameServerAddress);
		WebResource nameResource = resource.path(serviceName);
		return nameResource;
	}

	private WebResource connectToTheAuthService(String serviceName) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		System.out.println(authServerAddress);
		WebResource resource = client.resource(authServerAddress);
		WebResource nameResource = resource.path(serviceName);
		return nameResource;
	}

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	private ArrayList<String> showListOfFiles() {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("getListOfFiles");
		String filesAvailable = resource.accept(MediaType.APPLICATION_XML).get(String.class);
		// ArrayList files =
		// resource.accept(MediaType.APPLICATION_XML).get(ArrayList.class);
		ArrayList<String> files = new ArrayList<>();
		files.add(filesAvailable);
		System.out.println("Files available on Servers :: ");
		System.out.println(filesAvailable);
		return files;
	}

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	private FileMetaDataBean getFileDetailsForRead(String fileName) {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("getFileDetails").path("read-only").path(fileName);
		FileMetaDataBean fileData = resource.accept(MediaType.APPLICATION_XML).get(FileMetaDataBean.class);
		return fileData;
	}

	@GET
	private String authenticate(String uID, String pass) {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheAuthService("authClient").path(uID).path(clinetID).path(pass);
		String fileData = resource.accept(MediaType.TEXT_PLAIN).get(String.class);
		System.out.println(resource.getURI());
		return fileData;
	}

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	private FileMetaDataBean getFileDetailsForWrite(String fileName) {
		FileSystemClient obj = new FileSystemClient();
		WebResource resource = obj.connectToTheFileService("getFileDetails").path("read-write").path(clinetID)
				.path(fileName);
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
