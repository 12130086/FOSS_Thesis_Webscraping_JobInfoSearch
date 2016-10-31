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

public class InternEduVn extends JobSite {
	public String baseURL = "http://internship.edu.vn";
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

	public InternEduVn(String query, String queryLocation) throws IOException, ParseException {
		String queryURL = getRequestURL(query, queryLocation);
		Document firstDoc = Jsoup.connect(queryURL).userAgent("Mozilla").get();
		totalJob = firstDoc.getElementsByAttributeValueMatching("class", "tpj_row[0-1]").size();
		if (totalJob > 0)
			for (Element e : firstDoc.getElementsByAttributeValueMatching("class", "tpj_row[0-1]")) {
				String jobName = e.child(3).text();
				String requirements = null;
				Date expDate = dateFormat.parse(e.child(2).text());
				Date pubDate = dateFormat.parse(e.child(1).text());
				String location = e.child(4).text();
				String companyName = e.child(5).text();
				String salaries = null;
				URL originalURL = new URL(baseURL + e.child(3).child(0).attr("href"));
				String description = null;

				listJob.add(new Job(getSiteName(), jobName, requirements, expDate, pubDate, location, companyName,
						salaries, originalURL, description));
			}

	}

	@Override
	public String getSiteName() {
		return "Internship.edu.vn";
	}

	@Override
	protected String getBaseURL() {
		return baseURL;
	}

	@Override
	protected String getRequestURL(String query, String queryLocation) {
		String formattedQuery = query.toLowerCase().replaceAll(" ", "+");
		switch (queryLocation) {
		case "Tất cả":
			return baseURL + "/vi/component/tpjobs/simplesearch?limit=0" + "&keyword=" + formattedQuery + "&id_city=0";
		case "Hồ Chí Minh":
			return baseURL + "/vi/component/tpjobs/simplesearch?limit=0" + "&keyword=" + formattedQuery + "&id_city=22";
		case "Hà Nội":
			return baseURL + "/vi/component/tpjobs/simplesearch?limit=0" + "&keyword=" + formattedQuery + "&id_city=23";
		case "Đà Nẵng":
			return baseURL + "/vi/component/tpjobs/simplesearch?limit=0" + "&keyword=" + formattedQuery + "&id_city=40";
		default:
			return null;
		}
	}

	@Override
	public List<Job> getListJob() {
		return listJob;
	}

}
