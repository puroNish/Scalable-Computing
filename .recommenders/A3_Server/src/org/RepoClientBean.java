package org;

public class RepoClientBean {
	String repoUrl;
	String repoCommit;
	Boolean ccReceived;
	Boolean clientAssigned;
	String clientUniqueID;
	String repoBranch;
	int cc;

	public RepoClientBean() {
		setCcReceived(false);
		setRepoBranch(null);
		setRepoCommit(null);
		setClientAssigned(false);
		setRepoUrl(null);
	}

	public int getCc() {
		return cc;
	}

	public void setCc(int cc) {
		this.cc = cc;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setClientUniqueID(String clientUniqueID) {
		this.clientUniqueID = clientUniqueID;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getRepoCommit() {
		return repoCommit;
	}

	public void setRepoCommit(String repoCommit) {
		this.repoCommit = repoCommit;
	}

	public Boolean getCcReceived() {
		return ccReceived;
	}

	public void setCcReceived(Boolean ccReceived) {
		this.ccReceived = ccReceived;
	}

	public Boolean getClientAssigned() {
		return clientAssigned;
	}

	public void setClientAssigned(Boolean clientAssigned) {
		this.clientAssigned = clientAssigned;
	}

	public String getClientUniqueID() {
		return clientUniqueID;
	}

	public String getRepoBranch() {
		return repoBranch;
	}

	public void setRepoBranch(String repoBranch) {
		this.repoBranch = repoBranch;
	}

}
