package org;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

@ApplicationPath("ccServer")
@WebServlet
public class RepoCCRestServer {
	static ArrayList<RepoClientBean> repoList;
	final String repoUrl = "https://github.com/sack0809/ScalableComputing.git";
	final String repoBranch = "Assignment1";
	static boolean repoListEmpty=true;
	

/*	public static void main(String[] args) throws IOException {
		populateRepoListIfEmpty();
		RepoCCRestServer obj = new RepoCCRestServer();
		System.out.println(obj.getRepoDetails());
	}*/
	
	private static void populateRepoListIfEmpty() throws IOException {
		if(repoListEmpty) {
			RepoCCRestServer obj = new RepoCCRestServer();
			repoList = obj.createRepo();
			repoListEmpty = false;
		}
		
	}
	
	@GET
	@Path("/getRepoDetails")
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
	public String getRepoDetails() throws IOException {
		populateRepoListIfEmpty();
		String repoDetails;
		repoDetails = repoUrl + "***" + repoBranch;
		return repoDetails;
	}
	
	@GET
	@Path("/assignCommitToMe/{clientUniqueID}")
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
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
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
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
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
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
	@Produces(javax.ws.rs.core.MediaType.TEXT_PLAIN)
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

}
