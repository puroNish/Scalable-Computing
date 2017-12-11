package ccRepoFiles;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.org.apache.xml.internal.utils.URI;

@XmlRootElement(name="repoDetails")
public class repoDetailsBean {
	
	public repoDetailsBean() {}
	
	private URI repoUrl;
	private String repoBranch;
	private String repoFile;
	public URI getRepoUrl() {
		return repoUrl;
	}
	@XmlElement
	public void setRepoUrl(URI repoUrl) {
		this.repoUrl = repoUrl;
	}
	public String getRepoBranch() {
		return repoBranch;
	}
	@XmlElement 
	public void setRepoBranch(String repoBranch) {
		this.repoBranch = repoBranch;
	}
	public String getRepoFile() {
		return repoFile;
	}
	@XmlElement 
	public void setRepoFile(String repoFile) {
		this.repoFile = repoFile;
	}
}
