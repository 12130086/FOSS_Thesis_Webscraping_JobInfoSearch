package survfate.jobinfosearch.obj;

import java.net.URL;
import java.util.Date;

public class Job {
	private String jobSiteName;
	private String jobName;
	private String requirements;
	private Date expDate;
	private Date pubDate;
	private String location;
	private String companyName;
	private String salaries;
	private URL originalURL;
	private String description;

	public Job(String jobSiteName, String jobName, String requirements, Date expDate, Date pubDate, String location,
			String companyName, String salaries, URL originalURL, String description) {
		super();
		this.jobSiteName = jobSiteName;
		this.jobName = jobName;
		this.requirements = requirements;
		this.expDate = expDate;
		this.pubDate = pubDate;
		this.location = location;
		this.companyName = companyName;
		this.salaries = salaries;
		this.originalURL = originalURL;
		this.description = description;
	}

	public String getJobSiteName() {
		return jobSiteName;
	}

	public String getJobName() {
		return jobName;
	}

	public String getRequirements() {
		return requirements;
	}

	public Date getExpDate() {
		return expDate;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public String getLocation() {
		return location;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getSalaries() {
		return salaries;
	}

	public URL getOriginalURL() {
		return originalURL;
	}

	public String getDescription() {
		return description;
	}

}
