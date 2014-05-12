import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsonRequest {
	
	private static int totalPages = 1;
	private static String geUrl;
	private static boolean isFirst = false;
	private static String jobUrl= "";

	
	/*
	 * Get GE ajax_search url to find the hidden data in javascipt
	 */
	public static void getJsonData() {
		
		Thread t = new Thread() {
			@Override
			public void run() {
				HttpClient httpClient = HttpClientBuilder.create().build();
				int index = 1;
				long sTime = System.currentTimeMillis()/1000;
				
				do {
					StringBuilder s = new StringBuilder();
					s.append(Utils.GE_URL_1).append("" + index).append(Utils.GE_URL_2);
					geUrl = s.toString();
					HttpPost httpPost = new HttpPost(geUrl);
					try {
						HttpResponse response = httpClient.execute(httpPost);
						String jsonString = EntityUtils.toString(response
								.getEntity());
						jsonDecode(jsonString);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					index++;
				}while(index < totalPages);
				
				System.out.print("\n" + (System.currentTimeMillis() / 1000 - sTime) + " sec");
			}
		};
		t.start();
	}

	/**
	 * Decode the JSON string form GE
	 * 
	 * @param srcData
	 *            The JSON string form getJsonData method
	 */
	private static void jsonDecode(String srcData) {

		JSONObject jsonObject = new JSONObject(srcData);

		if (!isFirst) {
			totalPages = Integer.parseInt(jsonObject.getJSONObject(
					"OtherInformation").getString("MaxPages"));
			isFirst = true;
		}

		JSONObject jo = jsonObject.getJSONObject("Business")
				.getJSONObject("GE Capital").getJSONObject("Jobs");

		Iterator<?> keys = jo.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			//System.out.println(key);
			if (jo.get(key) instanceof JSONObject) {
				JSONObject jobNum = jo.getJSONObject(key);
				//System.out.print(jobNum.getString("LastUpdated"));
				jobUrl = jobNum.getString("JobDetailLink");
				//System.out.println(jobUrl);
				getJobDetail(jobUrl);
			}
		}
	}

	/**
	 * parse GE url for job details from Fieldlabel and TEXT
	 * 
	 * @param url
	 *            JobDetailLink for parsing job detail
	 */
	private static void getJobDetail(String url) {

		try {
			Document doc = Jsoup.connect(url).get();

			Elements infos = doc.getElementsByClass("TEXT");
			
			for (Element infoText : infos) {
				//System.out.print(infoText.attr("id") + " : ");
				//System.out.println(infoText.text());
				infoText.attr("id");
				infoText.text();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
