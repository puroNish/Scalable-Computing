package repoClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ClientForRepoService {

	public static void main(String[] args) throws Exception {
		ClientForRepoService obj = new ClientForRepoService();
		String clientID = new String("Nish_1");
		 int cc = obj.getCommitFromService();
		 System.out.println(String.valueOf(cc));
		 String path = "sendCC"+"/"+clientID+"/"+cc;
//		 System.out.println(path);
		 System.out.println(obj.sendDataToServer(path));

	}

	public String sendDataToServer(String pathToService) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource("http://localhost:8080/ccRepoFiles");
		// System.out.println("values Send ::" + clientId + " and " + cc);
		WebResource nameResource = resource.path("rest").path("getCC").path(pathToService);
//		System.out.println("Client Response ::: \n" + getResponse(nameResource));
		return nameResource.accept(MediaType.TEXT_PLAIN).get(String.class);
	}

	
	public int getCommitFromService() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		// HttpClient client = new DefaultHttpClient();
		// HttpGet request = new
		// HttpGet("http://localhost:8080/ccRepoFiles/rest/getCC/cloneRepo");
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String response = null;
		File tempFile1 = new File("/temp1");
		if (tempFile1.exists()) {
			tempFile1.delete();
		}
		File tempFile2 = new File("/temp2");
		if (tempFile2.exists()) {
			tempFile2.delete();
		}
		File newFile = new File("tempReadFile");
		if (newFile.exists()) {
			newFile.delete();
		}
		try {
			URL resetEndpoint = new URL("http://localhost:8080/ccRepoFiles/rest/getCC/cloneRepo");
			connection = (HttpURLConnection) resetEndpoint.openConnection();
			// Set request method to GET as required from the API
			connection.setRequestMethod("GET");

			// Read the response
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder jsonSb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				jsonSb.append(line);
			}
			response = jsonSb.toString();

			System.out.println(response);
			// String temp = EntityUtils.toString(response.getEntity(),"UTF-8");
			// System.out.println(response);

			Git git = Git.cloneRepository() //
					.setURI("https://github.com/sack0809/ScalableComputing.git").setGitDir(tempFile1)
					.setDirectory(tempFile2).setBranch("Assignment1")
					// .setCredentialsProvider(new UsernamePasswordCredentialsProvider("purohitn",
					// "Meenal@8590"))
					.setCloneAllBranches(true).call(); // System.out.println(git);
			// git.checkout().setName("471c487333e9ee705bda2d3e860c80b346afcc4e").call();
			ObjectId temp33 = ObjectId.fromString(response);
			Repository repository = git.getRepository();

			// ObjectId tempId = new ObjectId(Integer.parseInt(response), 0, 0, 0, 0);
			ObjectLoader loader = repository.open(temp33);
			FileOutputStream str = new FileOutputStream(newFile);
			loader.copyTo(str);
//			loader.copyTo(System.out);
			
			CcCalculator calculator = new CcCalculator();
			int finalResponse = calculator.calculateCC(newFile);
			System.out.println(finalResponse);

			repository.close();
			// git.close();
			str.close();
			return calculator.calculateCC(newFile);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			FileUtils.forceDelete(tempFile1);
			FileUtils.forceDelete(tempFile2);
//			 FileUtils.forceDelete(newFile);
		}
	}
	

}
