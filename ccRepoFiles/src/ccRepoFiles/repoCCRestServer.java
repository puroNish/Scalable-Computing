package ccRepoFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

@Path("/getCC")
@WebServlet
public class repoCCRestServer {
	static ArrayList<RepoClientBean> repoList;
	final String repoUrl = "https://github.com/sack0809/ScalableComputing.git";
	final String repoBranch = "Assignment1";
	static boolean repoListEmpty=true;
	

	public repoCCRestServer() {
	}
	
	public static void main(String[] args) throws Exception {
//		repoCCRestServer obj = new repoCCRestServer();
		populateRepoListIfEmpty();
		System.out.println(repoList.size());
	}
	
	private static void populateRepoListIfEmpty() throws IOException {
		if(repoListEmpty) {
			repoCCRestServer obj = new repoCCRestServer();
			repoList = obj.createRepo();
			repoListEmpty = false;
		}
		
	}
	
	@GET
	@Path("/getRepoDetails")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRepoDetails() throws IOException {
		populateRepoListIfEmpty();
		String repoDetails;
		repoDetails = repoUrl + "***" + repoBranch;
		return repoDetails;
	}
	
	@GET
	@Path("/assignCommitToMe/{clientUniqueID}")
	@Produces(MediaType.TEXT_PLAIN)
	public String assignCommitToClient(@PathParam("clientUniqueID") String clientUniqueID) throws IOException {
		populateRepoListIfEmpty();
		for(RepoClientBean commitObj : repoList) {
			if(commitObj.getClientAssigned() && !commitObj.getCcReceived() && commitObj.getClientUniqueID().equals(clientUniqueID)) {
				return new String("Task already assigned to the client. Commit ID ***"+commitObj.getRepoCommit());
			}
			if(!commitObj.getClientAssigned()) {
				commitObj.setClientAssigned(true);
				commitObj.setClientUniqueID(clientUniqueID);
				return commitObj.getRepoCommit();
			}
		}
		return new String("COMPLETED");
	}
	
	@GET
	@Path("/sendCC/{clientUniqueID}/{ccReceived}")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean resultCollection(@PathParam("clientUniqueID") String clientUniqueID, @PathParam("ccReceived") String ccReceived) {
		for(RepoClientBean commitObj : repoList) {
			if(commitObj.getClientAssigned() && clientUniqueID.equals(commitObj.getClientUniqueID().toString())) {
				commitObj.setCcReceived(true);
				commitObj.setCc(Integer.parseInt(ccReceived));
				return true;
			}
		}
		return false;
	}
	
	@GET
	@Path("/getCCForFile/{commitId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCCForCommit(@PathParam("commitId") String commitId) {
		for(RepoClientBean commitObj : repoList) {
			if(commitObj.getClientAssigned() && commitId.equals(commitObj.getRepoCommit().toString())) {
				
				return Integer.toString(commitObj.getCc());
			}
		}
		return "Commit Not Found";
	}

	@GET
	@Path("/serverStatus")
	@Produces(MediaType.TEXT_PLAIN)
	public String pingServer() {
		return new String("Server is working ");
	}

	private ArrayList<RepoClientBean> createRepo() throws IOException {
		File tempFile1 = new File("/tempFileJava");
		if (tempFile1.exists()) {
			FileUtils.forceDelete(tempFile1);
		}
		File tempFile2 = new File("/tempDir");
		if (tempFile2.exists()) {
			FileUtils.forceDelete(tempFile2);
		}
		ArrayList<RepoClientBean> repoClientBeanList = new ArrayList<>();
		try {
			Git git = Git.cloneRepository()
					// .setURI("https://gitlab.scss.tcd.ie/jdukes/cs7ns2-zephyr.git")
					.setURI(repoUrl).setGitDir(tempFile1).setDirectory(tempFile2).setBranch(repoBranch)
					.setCloneAllBranches(true).call();
			// System.out.println("git");
			Repository repository = git.getRepository();
			Iterable<RevCommit> logs = git.log().call();
			// ArrayList<ObjectId> commitIds = new ArrayList<>();
			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.setRecursive(true);
			try (RevWalk revWalk = new RevWalk(repository)) {
				for (RevCommit commit : logs) {
					treeWalk.addTree(commit.getTree());
					while (treeWalk.next()) {
						// ObjectId repoCommit = treeWalk.getObjectId(0);
						// System.out.println(treeWalk.getObjectId(0).getName());
						// System.out.println(loader.getSize());
						String repoCommitId = treeWalk.getObjectId(0).getName();
						if (!repoCommitId.startsWith("00000")) {
							RepoClientBean repoBean = new RepoClientBean();
							repoBean.setRepoUrl(repoUrl);
							repoBean.setRepoBranch(repoBranch);
							repoBean.setRepoCommit(repoCommitId);
							repoClientBeanList.add(repoBean);
						}
						// commitIds.add(treeWalk.getObjectId(0));
					}
					treeWalk.close();
				}

				revWalk.close();
				git.close();
			}
			return repoClientBeanList;
		} catch (InvalidRemoteException | TransportException e) {
			// TODO Auto-generated catch block e.printStackTrace();
			return repoClientBeanList;
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block e.printStackTrace(); }
			return repoClientBeanList;
		}
	}

	@GET
	@Path("/getCCForFile")
	@Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	public repoCCBean calculateCCForFile(repoDetailsBean inputJSON) throws IOException {
		repoCCBean responseBean = new repoCCBean();
		String fileName = "C:\\Users\\nishp\\Music\\demo.java";
		CycloResolution cc = new CycloResolution();
		responseBean.setRepoFileCC(cc.calculateCC(inputJSON.getRepoFile()));
		responseBean.setRepoFileCC(cc.calculateCC(fileName));
		responseBean.setRepoFile((inputJSON.getRepoFile()));
		responseBean.setRepoBranch((inputJSON.getRepoBranch()));
		responseBean.setRepoUrl((inputJSON.getRepoUrl()));
		return responseBean;
	}

	@GET
	@Path("/cloneRepo")
	@Consumes(MediaType.TEXT_PLAIN)
	public String justCloneRepo()
			throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		File tempFile1 = new File("/tempFileJava");
		if (tempFile1.exists()) {
			tempFile1.delete();
		}
		File tempFile2 = new File("/tempDir");
		if (tempFile2.exists()) {
			tempFile2.delete();
		}
		Git git;
		try {
			git = Git.cloneRepository()
					// .setURI("https://gitlab.scss.tcd.ie/jdukes/cs7ns2-zephyr.git")
					.setURI("https://github.com/sack0809/ScalableComputing.git").setGitDir(tempFile1)
					.setDirectory(tempFile2).setBranch("Assignment1")
					// .setCredentialsProvider(new UsernamePasswordCredentialsProvider("purohitn",
					// "Meenal@8590"))
					.setCloneAllBranches(true).call();
			// System.out.println(git);
			Repository repository = git.getRepository();
			Iterable<RevCommit> logs = git.log().call();
			ArrayList<ObjectId> commitIds = new ArrayList<>();
			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.setRecursive(true);
			int i=0;
			try (RevWalk revWalk = new RevWalk(repository)) {
				System.out.println(i++);
				for (RevCommit commit : logs) {
					// System.out.println(commit.name());
					// ObjectId commitId = repository.resolve(commit.name());
					// System.out.println(commitId);
					// commitIds.add(commitId);
					// System.out.println(">>>>"+commit.name());
					treeWalk.addTree(commit.getTree());
					while (treeWalk.next()) {
						// System.out.println("found: " + treeWalk.getPathString());
						// System.out.println(treeWalk);
						commitIds.add(treeWalk.getObjectId(0));
						String repoCommitId = treeWalk.getObjectId(0).getName();
						if (!repoCommitId.startsWith("000"))
							System.out.println(repoCommitId);
						// System.out.println(treeWalk.getObjectId(0).getName());
						// System.out.println(treeWalk.getPathString());
						// ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
						// System.out.println(loader.);
						// loader.copyTo(System.out);
					}
					// ObjectLoader loader = repository.open(commitIds.get(0));
					// loader.copyTo(System.out);
					treeWalk.close();
				}
				revWalk.close();
				git.close();
			} finally {
				FileUtils.forceDelete(tempFile1);
				FileUtils.forceDelete(tempFile2);
			}

			return commitIds.get(0).getName().toString();

		} catch (InvalidRemoteException | TransportException e) {
			// TODO Auto-generated catch block e.printStackTrace();
			return new String("Kuch fata BC");
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block e.printStackTrace(); }
			return new String("Kuch fata BC");
		}

	}



	@GET
	@Path("/testServer")
	@Produces(MediaType.TEXT_PLAIN)
	public String restChalaKeNahi() {
		return new String("Chala BC");
	}

	@GET
	@Path("/mainServer")
	@Produces(MediaType.TEXT_PLAIN)
	public void mainWaalaKaam() {

	}

	@GET
	@Path("/getDataFromClient/{clientId}/{CC}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String getDataFromClient(@PathParam("clientId") String clientId, @PathParam("CC") String cc) {
		String response = new String("Value received from :: " + clientId + "\n CC received :: " + cc);
		return response;

	}

}
