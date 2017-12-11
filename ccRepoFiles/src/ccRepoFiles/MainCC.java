package ccRepoFiles;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class MainCC {

	public static void main(String[] args) throws Exception {
		// MainCC mainObj = new MainCC();
		// File tempFile = new File("/cs7ns2/blinky/branches/pca10040/blank/config");
		// repoCCRestServer obj = new repoCCRestServer();
		File tempFile1 = new File("/tempFileJava");
		if (tempFile1.exists()) {
			tempFile1.delete();
		}
		File tempFile2 = new File("/tempDir");
		if (tempFile2.exists()) {
			tempFile2.delete();
		}
		// FileUtils.deleteDirectory(tempFile);
		Git git = Git.cloneRepository()
				// .setURI("https://gitlab.scss.tcd.ie/jdukes/cs7ns2-zephyr.git")
				.setURI("https://github.com/sack0809/ScalableComputing.git").setGitDir(tempFile1)
				.setDirectory(tempFile2)
				// .setBranch("Assignment1")
				.setCloneAllBranches(true)
				// .setCredentialsProvider(new UsernamePasswordCredentialsProvider("purohitn",
				// "Meenal@8590"))
				.call();
		
		Repository repository = git.getRepository();
		Iterable<RevCommit> logs = git.log().call();
		ArrayList<ObjectId> commitIds = new ArrayList<>();

		try (RevWalk revWalk = new RevWalk(repository)) {
			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.setRecursive(true);
			for (RevCommit commit : logs) {
				// System.out.println(commit.name());
				// ObjectId commitId = repository.resolve(commit.name());
				// System.out.println(commitId);
				// commitIds.add(commitId);
				treeWalk.addTree(commit.getTree());
				while (treeWalk.next()) {
					// System.out.println("found: " + treeWalk.getPathString());
					// System.out.println(treeWalk);
					commitIds.add(treeWalk.getObjectId(0));
					// System.out.println(treeWalk.getObjectId(0).getName());
					// System.out.println(treeWalk.getPathString());
					// ObjectLoader loader = repository.open(objectId);
					// loader.copyTo(System.out);
				}
				// ObjectLoader loader = repository.open(commitIds.get(0));
				// loader.copyTo(System.out);

				treeWalk.close();
			}
			revWalk.close();
			System.out.println(commitIds.get(0).getName().toString());
		} finally {
			git.close();
			FileUtils.forceDelete(tempFile1);
			FileUtils.forceDelete(tempFile2);
		}

	}
}
