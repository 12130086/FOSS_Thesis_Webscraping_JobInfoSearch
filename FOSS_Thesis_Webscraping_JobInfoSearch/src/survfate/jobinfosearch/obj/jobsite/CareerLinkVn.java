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

public class CareerLinkVn extends JobSite {
	public String baseURL = "https://www.careerlink.vn/vieclam/tim-kiem-viec-lam?view=detail";
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

	public CareerLinkVn(String query, String queryLocation) throws IOException, ParseException {
		String queryURL = getRequestURL(query, queryLocation);
		Document firstDoc = Jsoup.connect(queryURL).userAgent("Mozilla").get();
		totalJob = Integer.valueOf(firstDoc.getElementsByClass("col-md-7").text()
				.substring(0, firstDoc.getElementsByClass("col-md-7").text().indexOf(":")).replaceAll("\\D+", ""));

		if (totalJob > 0) {
			int lastPage = (int) Math.ceil(totalJob / 30.0);

			for (int i = 1; i <= lastPage; i++) {
				Document responseDoc = Jsoup.connect(queryURL + "&page=" + i).userAgent("Mozilla").get();

				for (Element e : responseDoc.getElementById("save-job-form").getElementsByClass("list-group-item")) {
					String jobName = e.child(0).text();
					String requirements = e.child(1).child(2).text();
					Date expDate = null;
					Date pubDate = dateFormat.parse(e.child(1)
							.getElementsByAttributeValueContaining("class", "date pull-right").first().text());
					String location = queryLocation;
					String companyName = e.child(1).child(0).text();
					String salaries = e.getElementsByClass("media-body").first().child(0).text().split("\\|")[0].trim();
					URL originalURL = new URL("https://www.careerlink.vn" + e.child(0).child(1).attr("href"));
					String description = e.getElementsByClass("media-body").first().child(1).text();

					listJob.add(new Job(getSiteName(), jobName, requirements, expDate, pubDate, location, companyName,
							salaries, originalURL, description));

				}
			}
		}

	}

	@Override
	public String getSiteName() {
		return "CareerLink.vn";
	}

	@Override
	protected String getBaseURL() {
		return baseURL;
	}

	@Override
	protected String getRequestURL(String query, String queryLocation) {
		String formattedQuery = query.toLowerCase().replaceAll(" ", "%2520");
		switch (queryLocation) {
		case "Tất cả":
			return baseURL + "&keywords=" + formattedQuery + "&keyword_use=A";
		case "Hồ Chí Minh":
			return baseURL + "&keywords=" + formattedQuery + "&keyword_use=A&province_codes=HCM";
		case "Hà Nội":
			return baseURL + "&keywords=" + formattedQuery + "&keyword_use=A&province_codes=HN";
		case "Đà Nẵng":
			return baseURL + "&keywords=" + formattedQuery + "&keyword_use=A&province_codes=DN";
		default:
			return null;
		}
	}

	@Override
	public List<Job> getListJob() {
		return listJob;
	}

}
