package survfate.jobinfosearch.obj.jobsite;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import survfate.jobinfosearch.obj.Job;
import survfate.jobinfosearch.obj.JobSite;

public class VNWorks extends JobSite {
	public String baseURL = "http://www.vietnamworks.com/";
	public SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

	public VNWorks(String query, String queryLocation) throws IOException, ParseException {
		String queryURL = getRequestURL(query, queryLocation);
		Document firstDoc = Jsoup.connect(queryURL).get();

		// httpOnlyCookie expire at end of session
		Map<String, String> httpOnlyCookie = new HashMap<String, String>();
		httpOnlyCookie.put("lang", "2");
		httpOnlyCookie.put("PHPSESSID", "b5ean5gtuomv65pjlr70mpmk40");
		httpOnlyCookie.put("gr_split_GLOBAL", "A");
		httpOnlyCookie.put("gr_splitv_GLOBAL", "1");
		httpOnlyCookie.put("gr_reco", "1555dbb29dc-c5df1264178c9032");
		httpOnlyCookie.put("VNW128450527232960", "4e4c07c307e63707b2a771f7c34eff614276520");
		httpOnlyCookie.put("VNWWS128450527232960",
				"MjNhNGFkYWRmY2RiNTMyNzY2N2ViZmMyMmRhZGZmMjcxZWY3MzIxZWQ1MmM4ODMxODk1ZWI1ODg4Yjk3N2Y5MQ%7CZjY0ZTU5MzBhMjBiNjQ4ZDc5YmUxZTk4NjQ5YWFmYTVjOGI3Nzg3ZjhhMjJhYjg5YTllOTRkZGI1MTE2YjcxZA");

		totalJob = Integer.valueOf(firstDoc.getElementsByAttributeValueContaining("class", "form-group col-sm-12")
				.first().child(0).child(0).text());

		int lastPage = (totalJob > 50) ? Integer.valueOf(Jsoup.connect(queryURL + "/trang-1000").followRedirects(true)
				.execute().url().toString().substring(Jsoup.connect(queryURL + "/trang-1000").followRedirects(true)
						.execute().url().toString().length() - 1))
				: 1;
		for (int i = 1; i <= lastPage; i++) {
			Document responseDoc = Jsoup.connect(queryURL + "/trang-" + String.valueOf(i)).cookies(httpOnlyCookie)
					.get();

			Elements elements = responseDoc.getElementsByAttributeValueContaining("class", "col-sm-8 col-sm-pull-3");
			for (Element e : elements) {
				String jobName = e.child(0).text();
				String requirements = e.child(4).child(1).child(0).text();
				String pubDateString = e.child(4).child(0).child(0).child(0).text().replaceAll("Đăng tuyển: ", "");
				Date expDate = null;
				Date pubDate = (!pubDateString.equals("Hôm nay")) ? dateFormat.parse(pubDateString)
						: Calendar.getInstance().getTime();
				String location = e.child(3).child(0).child(0).text();
				String companyName = (e.previousElementSibling().previousElementSibling().child(1).child(0).children()
						.first() != null)
								? e.previousElementSibling().previousElementSibling().child(1).child(0).children()
										.first().attr("title")
								: e.previousElementSibling().previousElementSibling().child(1)
										.attr("data-original-title");
				String salaries = e.parent().child(0).child(0).text();
				URL originalURL = new URL(e.child(0).child(0).attr("href"));
				String description = null;

				listJob.add(new Job(getSiteName(), jobName, requirements, expDate, pubDate, location, companyName,
						salaries, originalURL, description));
			}
		}

	}

	@Override
	public String getSiteName() {
		return "VietnamWorks.com";
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
			return baseURL + formattedQuery + "-kv";
		case "Hồ Chí Minh":
			return baseURL + formattedQuery + "+tai-ho-chi-minh-v29-vn";
		case "Hà Nội":
			return baseURL + formattedQuery + "+tai-ha-noi-v24-vn";
		case "Đà Nẵng":
			return baseURL + formattedQuery + "+tai-da-nang-v17-vn";
		default:
			return null;
		}
	}

	@Override
	public List<Job> getListJob() {
		return listJob;
	}

}
