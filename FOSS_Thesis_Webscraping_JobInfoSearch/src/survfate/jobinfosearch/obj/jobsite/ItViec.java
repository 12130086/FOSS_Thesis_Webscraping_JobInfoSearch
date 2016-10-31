package survfate.jobinfosearch.obj.jobsite;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import survfate.jobinfosearch.obj.Job;
import survfate.jobinfosearch.obj.JobSite;

public class ItViec extends JobSite {
	public String baseURL = "https://itviec.com";
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);

	public ItViec(String query, String queryLocation) throws IOException, ParseException {
		String queryURL = getRequestURL(query, queryLocation);

		Document firstDoc = Jsoup.connect(queryURL).userAgent("Mozilla").get();
		totalJob = Integer.parseInt(firstDoc.getElementsByClass("search-page-header-keyword").parents().first().text()
				.replaceAll("\\D+", ""));

		// httpOnlyCookie expire at end of session
		String[] httpOnlyCookie = { "_ITViec_session",
				"BAh7CkkiD3Nlc3Npb25faWQGOgZFVEkiJWQ0YWZkZTk5ZTU4MThmMzk0Yzk1NjE4ODk0NWIwZWQyBjsAVEkiGXdhcmRlbi51c2VyLnVzZXIua2V5BjsAVFsHWwZpAuBgSSIiJDJhJDEwJGpxNXYvRUZqTktFRFAzaHdkeUIzRnUGOwBUSSIVc2hvd2VkX2xpZ2h0X2JveAY7AEZpB0kiDnVzZXJfYXV0aAY7AFRDOi1BY3RpdmVTdXBwb3J0OjpIYXNoV2l0aEluZGlmZmVyZW50QWNjZXNzewdJIgplbWFpbAY7AFRJIjJzdXJ2ZmF0ZS5qb2JpbmZvc2VhcmNoLml0dmlldC5jb21AbWFpbHNhYy5jb20GOwBUSSINcGFzc3dvcmQGOwBUSSIyc3VydmZhdGUuam9iaW5mb3NlYXJjaC5pdHZpZXQuY29tQG1haWxzYWMuY29tBjsAVEkiEF9jc3JmX3Rva2VuBjsARkkiMWU0d1RJT2ZiQ0VLYmxGTlJOanV4VWNHZTgzUzltK00rTFFUQUIyenZqMlU9BjsARg%3D%3D--c7c59a3e66df1606a0ac345cb311c0e3b04714dc" };
		if (totalJob > 0) {
			int lastPage = (int) Math.ceil(totalJob / 20.0);

			for (int i = 1; i <= lastPage; i++) {
				Document responseDoc = Jsoup.connect(queryURL + "?page=" + i)
						.cookie(httpOnlyCookie[0], httpOnlyCookie[1]).get();
				for (Element e : responseDoc.getElementsByClass("job_content")) {
					String jobName = e.child(1).child(0).text();
					String requirements = e.child(3).text();
					Date expDate = null;
					Date pubDate = (e.child(2).child(0).attr("class").equals("text"))
							? dateFormat.parse(e.child(2).child(0).text()) : null; // e.child(2).child(0).text()
					String location = e.child(2).child(1).text();
					String companyName = e.child(0).child(0).child(0).childNode(0).attr("alt").replaceAll(" Small Logo",
							"");
					String salaries = e.child(1).child(1).text();
					URL originalURL = new URL(baseURL + e.child(1).child(0).child(0).attr("href"));
					String description = e.child(1).child(2).text();

					listJob.add(new Job(getSiteName(), jobName, requirements, expDate, pubDate, location, companyName,
							salaries, originalURL, description));
				}
			}
		}

	}

	@Override
	public String getSiteName() {
		return "ITviec.com";
	}

	@Override
	protected String getBaseURL() {
		return baseURL;
	}

	@Override
	protected String getRequestURL(String query, String queryLocation) {
		String formattedQuery = query.toLowerCase().replaceAll(" ", "-");
		switch (queryLocation) {
		case "Tất cả":
			return baseURL + "/jobs/" + formattedQuery;
		case "Hồ Chí Minh":
			return baseURL + "/jobs/ho-chi-minh-hcm/" + formattedQuery;
		case "Hà Nội":
			return baseURL + "/jobs/ha-noi/" + formattedQuery;
		case "Đà Nẵng":
			return baseURL + "/jobs/da-nang/" + formattedQuery;
		default:
			return null;
		}
	}

	@Override
	public List<Job> getListJob() {
		return listJob;
	}

	public static void main(String[] args) throws IOException, ParseException {
		new ItViec("java", "Tất cả");
	}
}
