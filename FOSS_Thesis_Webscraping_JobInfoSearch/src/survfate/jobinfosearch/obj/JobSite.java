package survfate.jobinfosearch.obj;

import java.util.ArrayList;
import java.util.List;

public abstract class JobSite {

	protected int totalJob;
	protected List<Job> listJob = new ArrayList<Job>();

	abstract public String getSiteName();

	abstract protected String getBaseURL();

	// Build Request URL with site-specific Parameters
	abstract protected String getRequestURL(String query, String queryLocation);

	public int getTotalJob() {
		return totalJob;
	}

	public List<Job> getListJob() {
		return listJob;
	}

}
